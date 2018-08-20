package io.mateu.erp.dispo;

import io.mateu.erp.dispo.interfaces.portfolio.IHotel;
import io.mateu.erp.dispo.interfaces.product.IRoom;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter@Setter
public class CombinacionesHabitaciones {

    private List<CombinacionHabitaciones> combinaciones = new ArrayList<>();

    public CombinacionesHabitaciones(IHotel hotel, List<LineaReserva> ocupaciones, Cupo cupo) {

        Map<String, List<IRoom>> habsQueEncajan = new HashMap<>();
        Map<IRoom, IRoom> propietariosCupo = new HashMap<>();

        for (IRoom r : hotel.getRooms()) {

            for (LineaReserva o : ocupaciones) {
                if (r.fits(o.getAdultos(), o.getNinos(), o.getBebes())) {
                    List<IRoom> l = habsQueEncajan.get(o.getFirmaOcupacion());
                    if (l == null) habsQueEncajan.put(o.getFirmaOcupacion(), l = new ArrayList<>());
                    l.add(r);
                }
            }


        }


        Map<String, IRoom> asignacion = new HashMap<>();
        int pos = 0;
        for (LineaReserva o : ocupaciones) {
            if (habsQueEncajan.containsKey(o.getFirmaOcupacion())) for (IRoom r : habsQueEncajan.get(o.getFirmaOcupacion())) {
                asignacion.put(o.getFirmaOcupacion(), r);
                if (pos == ocupaciones.size() - 1) {
                    combinaciones.add(new CombinacionHabitaciones(asignacion));
                }
                asignacion.remove(o.getFirmaOcupacion());
            }
            pos++;
        }

    }
}
