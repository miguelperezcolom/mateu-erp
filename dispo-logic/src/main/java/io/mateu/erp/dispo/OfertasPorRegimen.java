package io.mateu.erp.dispo;

import io.mateu.erp.dispo.interfaces.product.IRoom;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter@Setter
public class OfertasPorRegimen {

    private Map<IRoom, OfertasPorHabitacion> ofertasPorHabitacion = new HashMap<>();

}
