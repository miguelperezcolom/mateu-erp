package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.booking.parts.HotelBookingLine;
import io.mateu.erp.model.partners.Agency;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class InventoryCalendarCube {
    private final Inventory inventory;

    LocalDate ayer = null;

    // dimensiones = fecha, habitacion
    private int[][][] cubo = null;

    private LocalDate inicio = null;
    private LocalDate fin = null;
    private List<RoomType> rooms = new ArrayList<>();
    int maxdias = 0;
    private List<Agency> agencias = new ArrayList<>();

    public InventoryCalendarCube(Inventory inventory) throws Throwable {

        this.inventory = inventory;

        init();
        
        build();
    }

    private void build() throws Throwable {

        // aplicamos las operaciones

        for (HotelContract c : inventory.getContracts()) {
            if (c.getTerms() != null) for (Allotment a : c.getTerms().getAllotment()) apply(new InventoryOperation(a.getRoom(), a.getQuantity(), InventoryAction.ADD, a.getStart(), a.getEnd()), c);
        }

        for (Inventory dependant : inventory.getDependantInventories()) {
            for (HotelContract c : dependant.getContracts()) if (!inventory.getContracts().contains(c)) {
                if (c.getTerms() != null) for (Allotment a : c.getTerms().getAllotment()) apply(new InventoryOperation(a.getRoom(), a.getQuantity(), InventoryAction.ADD, a.getStart(), a.getEnd()), true);
            }
        }

        for (InventoryOperation o : getOperations()) {
            apply(o);
        }

        for (HotelBookingLine l : inventory.getBookings()) {
            if (l.getBooking().isActive() && l.isActive()) apply(new InventoryOperation(l.getRoom().getType(), l.getRooms(), InventoryAction.ADD, l.getStart(), l.getEnd()), l.getBooking().getAgency());
        }

    }

    private RoomType getRoomFromCode(String roomCode) {
        RoomType r = null;
        for (Room x : inventory.getHotel().getRooms()) if (x.getType().getCode().equalsIgnoreCase(roomCode)) {
            r = x.getType();
            break;
        }
        return r;
    }

    private List<InventoryOperation> getOperations() throws Throwable {
        return Helper.selectObjects("select x from " + InventoryOperation.class.getName() + " x where x.inventory.id = " + inventory.getId() + " order by x.id");
    }


    private void apply(InventoryOperation o) {
        apply(o, false, null, null);
    }

    private void apply(InventoryOperation o, boolean desviado) {
        apply(o, desviado, null, null);
    }

    private void apply(InventoryOperation o, Agency agencia) {
        apply(o, false, agencia, null);
    }

    private void apply(InventoryOperation o, HotelContract c) {
        apply(o, false, null, c);
    }

    private void apply(InventoryOperation o, boolean desviado, Agency agencia, HotelContract c) {

        if (o.getEnd().isAfter(ayer) && o.getRoom() != null) {

            int desdeFecha = (o.getStart() != null)?(int) ChronoUnit.DAYS.between(inicio, (o.getStart().isAfter(ayer))?o.getStart():ayer): 0;
            int hastaFecha = (o.getEnd() != null)?(int) ChronoUnit.DAYS.between(inicio, o.getEnd()):maxdias;
            if (agencia != null) hastaFecha -= 1;
            for (int fecha = desdeFecha; fecha <= hastaFecha; fecha++) {
                int poshab = rooms.indexOf(o.getRoom());
                if (desviado) {
                    switch (o.getAction()) {
                        case ADD:
                            cubo[fecha][poshab][1] -= o.getQuantity();
                            cubo[fecha][poshab][3] += o.getQuantity();
                            break;
                        case SUBSTRACT:
                            cubo[fecha][poshab][1] += o.getQuantity();
                            cubo[fecha][poshab][3] -= o.getQuantity();
                            break;
                        case SET:
                            cubo[fecha][poshab][1] = cubo[fecha][poshab][0] - o.getQuantity();
                            cubo[fecha][poshab][2] = o.getQuantity();
                            break;
                    }
                } else if (agencia != null) {
                    int posAgencia = agencias.indexOf(agencia);
                    if (posAgencia < 0) {
                        agencias.add(agencia);
                        posAgencia = agencias.indexOf(agencia);
                    }
                    switch (o.getAction()) {
                        case ADD:
                            cubo[fecha][poshab][1] -= o.getQuantity();
                            cubo[fecha][poshab][2] += o.getQuantity();
                            cubo[fecha][poshab][4 + posAgencia] += o.getQuantity();
                            break;
                        case SUBSTRACT:
                            cubo[fecha][poshab][1] += o.getQuantity();
                            cubo[fecha][poshab][2] -= o.getQuantity();
                            cubo[fecha][poshab][4 + posAgencia] -= o.getQuantity();
                            break;
                    }
                } else if (c != null) {
                    switch (o.getAction()) {
                        case ADD:
                            cubo[fecha][poshab][0] += o.getQuantity();
                            cubo[fecha][poshab][1] += o.getQuantity();
                            break;
                        case SUBSTRACT:
                            cubo[fecha][poshab][0] -= o.getQuantity();
                            cubo[fecha][poshab][1] -= o.getQuantity();
                            break;
                        case SET:
                            cubo[fecha][poshab][0] = o.getQuantity();
                            cubo[fecha][poshab][1] = o.getQuantity();
                            break;
                    }
                } else {
                    switch (o.getAction()) {
                        case ADD:
                            cubo[fecha][poshab][1] += o.getQuantity();
                            break;
                        case SUBSTRACT:
                            cubo[fecha][poshab][1] -= o.getQuantity();
                            break;
                        case SET:
                            cubo[fecha][poshab][1] = o.getQuantity();
                            break;
                    }
                }
            }

        }

    }

    private void init() throws Throwable {

        // buscamos la fecha de inicio, final, habitaciones, etc para crear la estructura

        ayer = LocalDate.now().minusDays(1);

        for (HotelContract c : inventory.getContracts()) if (c.getTerms() != null) for (Allotment o : c.getTerms().getAllotment()) if (o.getEnd().isAfter(ayer)) {
            if (inicio == null || inicio.isAfter(o.getStart())) inicio = (o.getStart().isAfter(ayer))?o.getStart():ayer;
            if (fin == null || fin.isBefore(o.getEnd())) fin = o.getEnd();
            if (!rooms.contains(o.getRoom())) rooms.add(o.getRoom());
        }

        for (HotelContract c : inventory.getSecurityContracts()) if (c.getTerms() != null) for (Allotment o : c.getTerms().getSecurityAllotment()) if (o.getEnd().isAfter(ayer)) {
            if (inicio == null || inicio.isAfter(o.getStart())) inicio = (o.getStart().isAfter(ayer))?o.getStart():ayer;
            if (fin == null || fin.isBefore(o.getEnd())) fin = o.getEnd();
            if (!rooms.contains(o.getRoom())) rooms.add(o.getRoom());
        }

        for (InventoryOperation o : getOperations()) if (o.getEnd().isAfter(ayer)) {
            if (inicio == null || inicio.isAfter(o.getStart())) inicio = (o.getStart().isAfter(ayer))?o.getStart():ayer;
            if (fin == null || fin.isBefore(o.getEnd())) fin = o.getEnd();
            if (!rooms.contains(o.getRoom())) rooms.add(o.getRoom());
        }


        if (inicio != null && fin != null) maxdias = (int) ChronoUnit.DAYS.between(inicio, fin);

        cubo = new int[maxdias + 1][rooms.size()][100];

    }

    public int[][][] getCubo() {
        return cubo;
    }

    public int[] getCubo(LocalDate fecha, RoomType room) {
        if (inicio != null && fecha != null) {
            int posfecha = (int) ChronoUnit.DAYS.between(inicio, fecha);
            int poshab = rooms.indexOf(room);

            if (posfecha >= 0 && posfecha < cubo.length && poshab >= 0 && poshab < cubo[posfecha].length) return cubo[posfecha][poshab];
            else return new int[100];
        } else return new int[100];
    }

}
