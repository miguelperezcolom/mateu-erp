package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityManager;
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

    public InventoryCalendarCube(Inventory inventory) throws Throwable {

        this.inventory = inventory;

        init();
        
        build();
    }

    private void build() throws Throwable {

        // aplicamos las operaciones

        for (HotelContract c : inventory.getContracts()) {
            if (c.getTerms() != null) for (Allotment a : c.getTerms().getAllotment()) apply(new InventoryOperation(getRoomFromCode(a.getRoom()), a.getQuantity(), InventoryAction.SET, a.getStart(), a.getEnd()));
        }

        for (Inventory dependant : inventory.getDependantInventories()) {
            for (HotelContract c : dependant.getContracts()) {
                if (c.getTerms() != null) for (Allotment a : c.getTerms().getAllotment()) apply(new InventoryOperation(getRoomFromCode(a.getRoom()), -1 * a.getQuantity(), InventoryAction.ADD, a.getStart(), a.getEnd()));
            }
        }

        for (InventoryOperation o : getOperations()) {
            apply(o);
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

        if (o.getEnd().isAfter(ayer) && o.getRoom() != null) {

            int desdeFecha = (o.getStart() != null)?(int) ChronoUnit.DAYS.between(inicio, (o.getStart().isAfter(ayer))?o.getStart():ayer): 0;
            int hastaFecha = (o.getEnd() != null)?(int) ChronoUnit.DAYS.between(inicio, o.getEnd()):maxdias;
            for (int fecha = desdeFecha; fecha <= hastaFecha; fecha++) {
                int poshab = rooms.indexOf(o.getRoom());
                switch (o.getAction()) {
                    case ADD:
                        cubo[fecha][poshab][0] += o.getQuantity();
                        cubo[fecha][poshab][2] += o.getQuantity();
                        break;
                    case SUBSTRACT:
                        cubo[fecha][poshab][1] += o.getQuantity();
                        cubo[fecha][poshab][2] -= o.getQuantity();
                        break;
                    case SET:
                        cubo[fecha][poshab][0] = o.getQuantity();
                        cubo[fecha][poshab][2] = o.getQuantity();
                        break;
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
            if (!rooms.contains(o.getRoom())) rooms.add(getRoomFromCode(o.getRoom()));
        }

        for (HotelContract c : inventory.getSecurityContracts()) if (c.getTerms() != null) for (Allotment o : c.getTerms().getSecurityAllotment()) if (o.getEnd().isAfter(ayer)) {
            if (inicio == null || inicio.isAfter(o.getStart())) inicio = (o.getStart().isAfter(ayer))?o.getStart():ayer;
            if (fin == null || fin.isBefore(o.getEnd())) fin = o.getEnd();
            if (!rooms.contains(o.getRoom())) rooms.add(getRoomFromCode(o.getRoom()));
        }

        for (InventoryOperation o : getOperations()) if (o.getEnd().isAfter(ayer)) {
            if (inicio == null || inicio.isAfter(o.getStart())) inicio = (o.getStart().isAfter(ayer))?o.getStart():ayer;
            if (fin == null || fin.isBefore(o.getEnd())) fin = o.getEnd();
            if (!rooms.contains(o.getRoom())) rooms.add(o.getRoom());
        }


        if (inicio != null && fin != null) maxdias = (int) ChronoUnit.DAYS.between(inicio, fin);

        cubo = new int[maxdias + 1][rooms.size()][4];

    }

    public int[][][] getCubo() {
        return cubo;
    }

    public int[] getCubo(LocalDate fecha, RoomType room) {
        int posfecha = (int) ChronoUnit.DAYS.between(inicio, fecha);
        int poshab = rooms.indexOf(room);

        if (posfecha >= 0 && posfecha < cubo.length && poshab >= 0 && poshab < cubo[posfecha].length) return cubo[posfecha][poshab];
        else return new int[] {0, 0, 0, 0};
    }

}
