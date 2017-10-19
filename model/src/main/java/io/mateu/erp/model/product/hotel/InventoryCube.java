package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.financials.Actor;
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

    public InventoryCube(Inventory inventory) {

        this.inventory = inventory;

        init();
        
        build();
    }

    private void build() {

        // aplicamos las operaciones

        for (InventoryOperation o : getInventory().getOperations()) {
            apply(o);
        }
        
    }

    private void apply(InventoryOperation o) {

        if (o.getEnd().isAfter(ayer)) {

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

    private void init() {

        // buscamos la fecha de inicio, final, habitaciones, etc para crear la estructura

        ayer = LocalDate.now().minusDays(1);

        for (InventoryOperation o : getInventory().getOperations()) if (o.getEnd().isAfter(ayer)) {
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
