package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.financials.Actor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class StopSalesCube {
    private final StopSales stopSales;

    LocalDate ayer = null;

    // dimensiones = fecha, habitacion, actor
    private StopSalesCubeValue[][][] cubo = null;

    private LocalDate inicio = null;
    private LocalDate fin = null;
    private List<RoomType> rooms = new ArrayList<>();
    private List<Actor> actors = new ArrayList<>();
    int maxdias = 0;

    public StopSalesCube(StopSales stopSales) {

        this.stopSales = stopSales;

        init();
        
        build();
    }

    private void build() {

        // aplicamos las operaciones

        for (StopSalesOperation o : getStopSales().getOperations()) {
            apply(o);
        }
        
    }

    private void apply(StopSalesOperation o) {

        if (o.getEnd().isAfter(ayer)) {

            int desdeFecha = (o.getStart() != null)?(int) ChronoUnit.DAYS.between(inicio, (o.getStart().isAfter(ayer))?o.getStart():ayer): 0;
            int hastaFecha = (o.getEnd() != null)?(int) ChronoUnit.DAYS.between(inicio, o.getEnd()):maxdias;
            for (int fecha = desdeFecha; fecha <= hastaFecha; fecha++) {
                List<RoomType> habs = (o.getRooms().size() > 0)?o.getRooms():rooms;
                for (RoomType r : habs) {
                    int poshab = rooms.indexOf(r);
                    List<Actor> acts = (o.getActors().size() > 0)?o.getActors():actors;
                    for (Actor a : acts) {
                        int posact = actors.indexOf(a);
                        switch (o.getAction()) {
                            case OPEN:
                                cubo[fecha][poshab][posact] = StopSalesCubeValue.OPEN;
                                break;
                            case CLOSE:
                                cubo[fecha][poshab][posact] = StopSalesCubeValue.CLOSED;
                                break;
                        }

                    }
                }
            }

        }
    }

    private void init() {

        // buscamos la fecha de inicio, final, habitaciones, etc para crear la estructura

        ayer = LocalDate.now().minusDays(1);

        for (StopSalesOperation o : getStopSales().getOperations()) if (o.getEnd().isAfter(ayer)) {
            if (inicio == null || inicio.isAfter(o.getStart())) inicio = (o.getStart().isAfter(ayer))?o.getStart():ayer;
            if (fin == null || fin.isBefore(o.getEnd())) fin = o.getEnd();
            for (RoomType r : o.getRooms()) if (!rooms.contains(r)) rooms.add(r);
            for (Actor r : o.getActors()) if (!actors.contains(r)) actors.add(r);
        }

        if (rooms.size() == 0) rooms.add(new RoomType()); // dummy room
        if (actors.size() == 0) actors.add(new Actor()); // dummy actor

        if (inicio != null && fin != null) maxdias = (int) ChronoUnit.DAYS.between(inicio, fin);

        cubo = new StopSalesCubeValue[maxdias + 1][rooms.size()][actors.size()];

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
                if (firmaActual != null) save(em, desdefecha, posfecha, firmaActual);
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
        for (int poshab = 0; poshab < rooms.size(); poshab++) {
            for (int posact = 0; posact < actors.size(); posact++) {
                sb.append((StopSalesCubeValue.CLOSED.equals(cubo[posfecha][poshab][posact]))?0:1);
            }
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
            for (int posact = 0; posact < actors.size(); posact++) l.getActors().add(actors.get(posact));
            if (l.getActors().size() == actors.size()) l.getActors().clear();
            for (int posact = 0; posact < rooms.size(); posact++) l.getRooms().add(rooms.get(posact));
            if (l.getRooms().size() == rooms.size()) l.getRooms().clear();
        }
    }
}
