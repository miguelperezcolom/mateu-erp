package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.partners.Partner;
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
public class StopSalesCube {
    private final StopSales stopSales;

    LocalDate ayer = null;

    // dimensiones = fecha, habitacion, actor
    private StopSalesCubeValue[][] cubo = null;

    private LocalDate inicio = null;
    private LocalDate fin = null;
    private List<RoomType> rooms = new ArrayList<>();
    private List<Partner> actors = new ArrayList<>();
    int maxdias = 0;

    public StopSalesCube(StopSales stopSales) throws Throwable {

        this.stopSales = stopSales;

        init();

        build();
    }

    private void build() throws Throwable {

        // aplicamos las operaciones

        for (StopSalesOperation o : getOperations()) {
            apply(o);
        }
        
    }

    private List<StopSalesOperation> getOperations() throws Throwable {
        return Helper.selectObjects("select x from " + StopSalesOperation.class.getName() + " x where x.stopSales.id = " + stopSales.getId() + " order by x.id");
    }

    private void apply(StopSalesOperation o) {

        if (o.getEnd() != null && o.getEnd().isAfter(ayer)) {

            int desdeFecha = (o.getStart() != null)?(int) ChronoUnit.DAYS.between(inicio, (o.getStart().isAfter(ayer))?o.getStart():ayer): 0;
            int hastaFecha = (o.getEnd() != null)?(int) ChronoUnit.DAYS.between(inicio, o.getEnd()):maxdias;
            for (int fecha = desdeFecha; fecha <= hastaFecha; fecha++) {

                List<RoomType> habs = (o.getRooms().size() > 0)?o.getRooms():rooms;
                for (RoomType r : habs) {
                    int poshab = rooms.indexOf(r);
                    switch (o.getAction()) {
                        case OPEN:
                            cubo[fecha][poshab] = StopSalesCubeValue.OPEN;
                            break;
                        case CLOSE:
                            cubo[fecha][poshab] = StopSalesCubeValue.CLOSED;
                            break;
                    }
                }

                List<Partner> acts = (o.getActors().size() > 0)?o.getActors():actors;
                for (Partner a : acts) {
                    int posact = actors.indexOf(a);
                    switch (o.getAction()) {
                        case OPEN:
                            cubo[fecha][rooms.size() + posact] = StopSalesCubeValue.OPEN;
                            break;
                        case CLOSE:
                            cubo[fecha][rooms.size() + posact] = StopSalesCubeValue.CLOSED;
                            break;
                    }

                }

            }

        }
    }

    private void init() throws Throwable {

        // buscamos la fecha de inicio, final, habitaciones, etc para crear la estructura

        ayer = LocalDate.now().minusDays(1);

        for (StopSalesOperation o : getOperations()) if (o.getEnd() != null && o.getEnd().isAfter(ayer)) {
            if (inicio == null || inicio.isAfter(o.getStart())) inicio = (o.getStart().isAfter(ayer))?o.getStart():ayer;
            if (fin == null || fin.isBefore(o.getEnd())) fin = o.getEnd();
            for (RoomType r : o.getRooms()) if (!rooms.contains(r)) rooms.add(r);
            for (Partner r : o.getActors()) if (!actors.contains(r)) actors.add(r);
        }

        if (rooms.size() == 0) rooms.add(new RoomType()); // dummy room
        if (actors.size() == 0) actors.add(new Partner()); // dummy actor

        if (inicio != null && fin != null) maxdias = (int) ChronoUnit.DAYS.between(inicio, fin);

        cubo = new StopSalesCubeValue[maxdias + 1][rooms.size() + actors.size()];

    }

    public void save(EntityManager em) {

        // vaciamos las líneas actuales
        for (StopSalesLine l : getStopSales().getLines()) em.remove(l);
        getStopSales().getLines().clear();

        String firmaActual = null;
        int desdefecha = -1;
        for (int posfecha = 0; posfecha <= maxdias; posfecha++) {
            String firma = getFirma(posfecha);
            if (!firma.equals(firmaActual)) {
                if (firmaActual != null) save(em, desdefecha, posfecha - 1, firmaActual);
                desdefecha = posfecha;
                firmaActual = firma;
            }
        }
        if (firmaActual != null) {
            save(em, desdefecha, maxdias, firmaActual);
        }

    }

    private String getFirma(int posfecha) {
        StringBuffer sb = new StringBuffer();
        for (int pos = 0; pos < cubo[posfecha].length; pos++) {
            sb.append((StopSalesCubeValue.CLOSED.equals(cubo[posfecha][pos]))?0:1);
        }
        return sb.toString();
    }

    private void save(EntityManager em, int desdefecha, int hastafecha, String firma) {
        if (firma != null && firma.contains("0")) {
            StopSalesLine l;
            getStopSales().getLines().add(l = new StopSalesLine());
            em.persist(l);
            l.setStart(inicio.plusDays(desdefecha));
            l.setEnd(inicio.plusDays(hastafecha));
            l.setOnNormalInventory(true);
            l.setOnSecurityInventory(true);
            l.setStopSales(getStopSales());
            for (int posact = 0; posact < rooms.size(); posact++) if (StopSalesCubeValue.CLOSED.equals(cubo[desdefecha][posact])) l.getRooms().add(rooms.get(posact));
            if (l.getRooms().size() == rooms.size()) l.getRooms().clear();
            for (int posact = 0; posact < actors.size(); posact++) if (StopSalesCubeValue.CLOSED.equals(cubo[desdefecha][rooms.size() + posact])) l.getActors().add(actors.get(posact));
            if (l.getActors().size() == actors.size()) l.getActors().clear();
        }
    }
}
