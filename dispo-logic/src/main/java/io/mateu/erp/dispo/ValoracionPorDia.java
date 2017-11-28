package io.mateu.erp.dispo;

import io.mateu.erp.dispo.interfaces.product.IHotelOffer;
import io.mateu.erp.model.product.hotel.Supplement;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter@Setter
public class ValoracionPorDia {

    private double importeHabitacion;

    private List<ImportePorDia> importesPax = new ArrayList<>();

    private Map<Supplement, Double> suplementos = new HashMap<>();

    private Map<IHotelOffer, Double> ofertas = new HashMap<>();

    private double totalAlojamiento;
    private double totalRegimen;
    private double totalAcumulado;
    private double total;

    public ValoracionPorDia(int totalPax) {
        for (int i = 0; i < totalPax; i++) importesPax.add(new ImportePorDia());
    }

    public void totalizar() {
        double t = 0;

        t += importeHabitacion;
        for (ImportePorDia i : getImportesPax()) {
            t += i.getHabitacion();
            t += i.getAlojamiento();
            t += i.getDesayuno();
            t += i.getAlmuerzo();
            t += i.getCena();
            t += i.getExtrasAlojamiento();
            t += i.getExtrasRegimen();
            t += i.getDescuentoPax();
        }
        for (Double x : getSuplementos().values()) t += x;
        for (Double x : getOfertas().values()) t += x;

        setTotal(Helper.roundEuros(t));
    }
}
