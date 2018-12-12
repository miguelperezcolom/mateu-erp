package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.booking.parts.HotelBookingLine;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.product.hotel.RoomType;
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
public class InventoryCube {
    private final Inventory inventory;

    LocalDate ayer = null;

    // dimensiones = fecha, habitacion
    private int[][] cubo = null;

    private LocalDate inicio = null;
    private LocalDate fin = null;
    private List<RoomType> rooms = new ArrayList<>();
    int maxdias = 0;

    public InventoryCube(Inventory inventory) throws Throwable {

        this.inventory = inventory;

        init();
        
        build();
    }

    private void build() throws Throwable {

        // aplicamos las operaciones

        for (HotelContract c : inventory.getContracts()) {
            if (c.getTerms() != null) for (Allotment a : c.getTerms().getAllotment()) apply(new InventoryOperation(a.getRoom(), a.getQuantity(), InventoryAction.ADD, a.getStart(), a.getEnd()));
        }

        for (Inventory dependant : inventory.getDependantInventories()) {
            for (HotelContract c : dependant.getContracts()) {
                if (c.getTerms() != null) for (Allotment a : c.getTerms().getAllotment()) apply(new InventoryOperation(a.getRoom(), -1 * a.getQuantity(), InventoryAction.ADD, a.getStart(), a.getEnd()));
            }
        }

        for (InventoryOperation o : getOperations()) {
            apply(o);
        }

        for (HotelBookingLine l : inventory.getBookings()) {
            if (l.getBooking().isActive() && l.isActive()) apply(new InventoryOperation(l.getRoom().getType(), -1 * l.getRooms(), InventoryAction.ADD, l.getStart(), l.getEnd().minusDays(1)));
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
                        cubo[fecha][poshab] += o.getQuantity();
                        break;
                    case SUBSTRACT:
                        cubo[fecha][poshab] -= o.getQuantity();
                        break;
                    case SET:
                        cubo[fecha][poshab] = o.getQuantity();
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

        cubo = new int[maxdias + 1][rooms.size()];

    }

    public void save(EntityManager em) {

        // vaciamos las líneas actuales

        for (InventoryLine l : getInventory().getLines()) em.remove(l);
        getInventory().getLines().clear();

        for (int poshab = 0; poshab < rooms.size(); poshab++) {
            int firmaActual = Integer.MIN_VALUE;
            int desdefecha = -1;
            for (int posfecha = 0; posfecha <= maxdias; posfecha++) {
                int firma = cubo[posfecha][poshab];
                if (firma != firmaActual) {
                    if (firmaActual != Integer.MIN_VALUE) save(em, desdefecha, posfecha, poshab);
                    desdefecha = posfecha;
                    firmaActual = firma;
                }
            }
            if (firmaActual != Integer.MIN_VALUE) {
                save(em, desdefecha, maxdias, poshab);
            }
        }

    }

    private void save(EntityManager em, int desdefecha, int hastafecha, int poshab) {
        InventoryLine l;
        getInventory().getLines().add(l = new InventoryLine());
        em.persist(l);
        l.setStart(inicio.plusDays(desdefecha));
        l.setEnd(inicio.plusDays(hastafecha));
        l.setInventory(getInventory());
        l.setRoom(rooms.get(poshab));
        l.setQuantity(cubo[desdefecha][poshab]);
    }
}
