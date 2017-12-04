package io.mateu.erp.model.product.hotel.offer;


import io.mateu.erp.dispo.*;
import io.mateu.erp.dispo.interfaces.product.IBoard;
import io.mateu.erp.dispo.interfaces.product.IHotelOffer;
import io.mateu.erp.dispo.interfaces.product.IRoom;
import io.mateu.erp.model.product.hotel.DatesRange;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter@Setter
public class StayAndPayOffer extends AbstractHotelOffer {

    private int stayNights;

    private int payNights;

    private WhichNights whichNights;


    @Override
    public double aplicar(IBoard board, IRoom room, LineaReserva lineaReserva, ValoracionLineaReserva vlr, IHotelOffer o, CondicionesPorRegimen cpr) {
        double importeOferta = 0;
        List<Rango> rangos = new ArrayList<>();
        if (getStayDates().getRanges().size() == 0) {
            rangos.add(new Rango(lineaReserva.getEntrada(), lineaReserva.getSalida().minusDays(1)));
        } else {
            for (DatesRange r : getStayDates().getRanges()) {
                if (Helper.intersects(r.getStart(), r.getEnd(), lineaReserva.getEntrada(), lineaReserva.getSalida())) {
                    Rango rx = new Rango(r.getStart(), r.getEnd(), lineaReserva.getEntrada(), lineaReserva.getSalida(), lineaReserva.getNumeroNoches());
                    rangos.add(rx);
                }
            }
        }

        boolean[] aplica = new boolean[lineaReserva.getNumeroNoches()];
        double[] bases = new double[lineaReserva.getNumeroNoches()];

        for (Rango rx : rangos) for (int i = rx.getDesde(); i < rx.getHasta(); i++) {
            aplica[i] = true;
            double base = 0;
            ValoracionPorDia vpd = vlr.getDias().get(i);
            if (isOnRoom()) base += vpd.getTotalAlojamiento();
            if (isOnBoardBasis()) base += vpd.getTotalRegimen();
            if (isOnDiscounts()) base = vpd.getTotalAcumulado();
            bases[i] = base;
        }

        for (int i = 0; i < aplica.length; i++) if (aplica[i]) {

            int j = i + 1;
            int lamasbarata = i;
            int lamascara = i;
            double masbarata = bases[i];
            double mascara = bases[i];
            double total = bases[i];
            int noches = 1;
            while (noches < getStayNights() && j < aplica.length && aplica[j]) {
                if (masbarata > bases[j]) {
                    masbarata = bases[j];
                    lamasbarata = j;
                }
                if (mascara > bases[j]) {
                    masbarata = bases[j];
                    lamascara = j;
                }
                total += bases[j];
                j++;
                noches++;
            }
            double media = total / noches;

            if (noches == getStayNights()) {

                int quenoche = i;
                double base = bases[i];
                switch (getWhichNights()) {
                    case FIRST:
                        break;
                    case LAST:
                        quenoche = j - 1;
                        base = bases[quenoche];
                        break;
                    case CHEAPEST:
                        quenoche = lamasbarata;
                        base = masbarata;
                        break;
                    case MOSTEXPENSIVE:
                        quenoche = lamascara;
                        base = mascara;
                        break;
                    case AVERAGE:
                        quenoche = i;
                        base = media;
                        break;
                }

                ValoracionPorDia vpd = vlr.getDias().get(quenoche);
                vpd.getOfertas().put(o, -1d * base);
                vpd.setTotalAcumulado(vpd.getTotalAcumulado() - base);
                importeOferta += base;

            }

            i = j - 1;
        }

        return importeOferta;
    }

}
