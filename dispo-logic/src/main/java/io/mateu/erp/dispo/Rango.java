package io.mateu.erp.dispo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

@Getter@Setter
public class Rango {

    private int desde;

    private int hasta;

    public Rango(LocalDate checkIn, LocalDate checkOut) {
        desde = 0;
        hasta = (int) (DAYS.between(checkIn, checkOut) - 1);
    }

    public Rango(LocalDate start, LocalDate end, LocalDate checkIn, LocalDate checkOut, int totalNights) {

        if (start != null) desde = (int) DAYS.between(checkIn, start);
        if (desde < 0) desde = 0;
        hasta = totalNights;
        if (end != null) hasta = (int) DAYS.between(checkIn, end);
        if (hasta > totalNights) hasta = totalNights;
    }
}
