package io.mateu.erp.dispo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class Condiciones {

    private List<CondicionesPorDia> dias = new ArrayList<>();

    private double total;

    public Condiciones(int totalNights) {
        for (int i = 0; i < totalNights; i++) dias.add(new CondicionesPorDia());
    }
}
