package io.mateu.erp.dispo;

import io.mateu.erp.dispo.interfaces.product.IRoom;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter@Setter
public class SuplementosPorRegimen {

    private Map<IRoom, SuplementosPorHabitacion> suplementosPorHabitacion = new HashMap<>();

}
