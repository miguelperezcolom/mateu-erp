package io.mateu.erp.dispo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class SuplementosPorHabitacion {

    private List<SuplementosPorDia> dias = new ArrayList<>();

    public SuplementosPorHabitacion(int totalNights) {
        for (int i = 0; i < totalNights; i++) dias.add(new SuplementosPorDia());
    }
}
