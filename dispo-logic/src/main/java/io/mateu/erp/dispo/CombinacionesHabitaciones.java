package io.mateu.erp.dispo;

import io.mateu.erp.dispo.interfaces.portfolio.IHotel;
import io.mateu.erp.dispo.interfaces.product.IRoom;
import lombok.Getter;
import lombok.Setter;
import org.easytravelapi.hotel.Occupancy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter@Setter
public class CombinacionesHabitaciones {

    private List<CombinacionHabitaciones> combinaciones = new ArrayList<>();

    public CombinacionesHabitaciones(IHotel hotel, DispoRQ rq, Cupo cupo) {

        Map<Occupancy, List<IRoom>> habsQueEncajan = new HashMap<>();

        for (IRoom r : hotel.getRooms()) {

            for (Occupancy o : rq.getOccupancies()) {
                if (r.fits(o)) {
                    List<IRoom> l = habsQueEncajan.get(o);
                    if (l == null) habsQueEncajan.put(o, l = new ArrayList<>());
                    l.add(r);
                }
            }

        }

        Map<Occupancy, IRoom> asignacion = new HashMap<>();
        int pos = 0;
        for (Occupancy o : rq.getOccupancies()) {
            for (IRoom r : habsQueEncajan.get(o)) {
                asignacion.put(o, r);
                if (pos == rq.getOccupancies().size() - 1) {
                    combinaciones.add(new CombinacionHabitaciones(asignacion));
                }
                asignacion.remove(o);
            }
            pos++;
        }

    }
}
