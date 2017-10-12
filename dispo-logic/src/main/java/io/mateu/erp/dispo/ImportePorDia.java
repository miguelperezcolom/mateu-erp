package io.mateu.erp.dispo;

import lombok.Getter;
import lombok.Setter;
import org.easytravelapi.hotel.Occupancy;

@Getter@Setter
public class ImportePorDia {

    private double habitacion;

    private double alojamiento;

    private double desayuno;

    private double almuerzo;

    private double cena;

    private double extrasRegimen;

    private double extrasAlojamiento;

    public double getTotal(Occupancy o) {

        double total = 0;

        total += o.getNumberOfRooms() * habitacion;

        total += o.getNumberOfRooms() * o.getPaxPerRoom() * alojamiento;

        return total;

    }
}
