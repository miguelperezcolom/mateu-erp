package io.mateu.erp.dispo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class ValoracionLineaReserva {

    private List<ValoracionPorDia> dias = new ArrayList<>();

    private double total;

    public ValoracionLineaReserva(LineaReserva lineaReserva, int totalNights) {
        for (int i = 0; i < totalNights; i++) dias.add(new ValoracionPorDia(lineaReserva.getPax()));
    }

    public void totalizar() {
        double t = 0;

        for (ValoracionPorDia v : getDias()) {
            v.totalizar();
            t += v.getTotal();
        }

        setTotal(Helper.roundEuros(t));
    }
}
