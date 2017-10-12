package io.mateu.erp.dispo;

import io.mateu.erp.dispo.interfaces.product.IRoom;
import lombok.Getter;
import lombok.Setter;
import org.easytravelapi.hotel.Occupancy;

import java.util.HashMap;
import java.util.Map;

@Getter@Setter
public class CombinacionHabitaciones {

    private Map<Occupancy, IRoom> asignacion = new HashMap<>();


    public CombinacionHabitaciones(Map<Occupancy, IRoom> asignacion) {
        this.asignacion = new HashMap<>(asignacion);
    }
}
