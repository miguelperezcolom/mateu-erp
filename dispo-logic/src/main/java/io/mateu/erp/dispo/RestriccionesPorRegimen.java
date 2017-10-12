package io.mateu.erp.dispo;

import io.mateu.erp.dispo.interfaces.product.IBoard;
import io.mateu.erp.dispo.interfaces.product.IRoom;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter@Setter
public class RestriccionesPorRegimen {

    private Map<IRoom, RestriccionesPorHabitacion> restriccionesPorHabitacion = new HashMap<>();

}
