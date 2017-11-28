package io.mateu.erp.dispo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class OfertasPorHabitacion {

    private List<OfertasPorDia> dias = new ArrayList<>();

    public OfertasPorHabitacion(int totalNights) {
        for (int i = 0; i <= totalNights; i++) dias.add(new OfertasPorDia());
    }
}
