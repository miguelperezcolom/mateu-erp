package io.mateu.erp.dispo;

import io.mateu.erp.dispo.interfaces.portfolio.IHotel;
import io.mateu.erp.dispo.interfaces.product.IStopSaleLine;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class ParosVentas {

    private List<ParoDia> dias = new ArrayList<>();

    private ParoDia resumen = new ParoDia();

    public ParosVentas(IHotel hotel, LocalDate checkIn, LocalDate checkOut, int totalNights) {

        for (int i = 0; i < totalNights; i++) {
            dias.add(new ParoDia());
        }

        for (IStopSaleLine l : hotel.getStopSalesLines()) {
            if (Helper.intersects(l.getStart(), l.getEnd(), checkIn, checkOut)) {

                Rango rango = new Rango(l.getStart(), l.getEnd(), checkIn, checkOut, totalNights);

                for (int noche = rango.getDesde(); noche < rango.getHasta(); noche++) {
                    ParoDia p = getDias().get(noche);
                    p.setAllClosed(l.getAgencyIds().size() == 0 && l.getRoomIds().size() == 0);
                    if (!p.isAllClosed()) {
                        p.getRoomsClosed().addAll(l.getRoomIds());
                        p.getClientsClosed().addAll(l.getAgencyIds());
                    }
                }

            }
        }

    }
}
