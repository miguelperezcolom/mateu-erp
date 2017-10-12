package io.mateu.erp.dispo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class RestriccionesPorHabitacion {

    private List<RestriccionesPorDia> dias = new ArrayList<>();

    public RestriccionesPorHabitacion(int totalNights) {
        for (int i = 0; i <= totalNights; i++) dias.add(new RestriccionesPorDia());
    }
}
