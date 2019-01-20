package io.mateu.erp.model.product.hotel.offer;

import io.mateu.erp.dispo.*;
import io.mateu.erp.dispo.interfaces.product.IBoard;
import io.mateu.erp.dispo.interfaces.product.IHotelOffer;
import io.mateu.erp.dispo.interfaces.product.IRoom;
import io.mateu.erp.model.booking.parts.HotelBookingLine;
import io.mateu.mdd.core.util.DatesRange;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@Entity
@Getter@Setter
public class DiscountOffer extends AbstractHotelOffer {

    private Per per;

    private Scope scope;

    private boolean percent;

    private double value;

    @Override
    public double aplicar(IBoard board, IRoom room, LineaReserva lineaReserva, ValoracionLineaReserva vlr, IHotelOffer o, Condiciones cpr) {
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

        for (Rango rx : rangos) for (int i = rx.getDesde(); i < rx.getHasta(); i++) aplica[i] = true;

        for (int i = 0; i < aplica.length; i++) if (aplica[i]) {
            ValoracionPorDia vpd = vlr.getDias().get(i);
            double importe = getValue();
            if (isPercent()) {
                double base = 0;
                if (isOnRoom()) base += vpd.getTotalAlojamiento();
                if (isOnBoardBasis()) base += vpd.getTotalRegimen();
                if (isOnDiscounts()) base = vpd.getTotalAcumulado();
                importe = base * getValue() / 100d;
            } else {
                if (Scope.BOOKING.equals(getScope()) && importeOferta != 0) {
                    importe = 0;
                } else {
                    if (Per.PAX.equals(getPer())) {
                        importe *= lineaReserva.getPax();
                    } else if (Per.BOOKING.equals(getPer())) {
                        //todo: comprobar que no aplicamos la oferta más de una vez en toda la reserva (no con las otras líneas de reserva)
                    }
                }
            }
            vpd.getOfertas().put(o, -1d * importe);
            vpd.setTotalAcumulado(vpd.getTotalAcumulado() - importe);
            importeOferta += importe;
        }

        return importeOferta;
    }

    @Override
    public double aplicar(HotelBookingLine l, double[][] valorEstancia, double[][] valorRegimen, double[] valorOfertas) {
        int noches = new Long(DAYS.between(l.getStart(), l.getEnd())).intValue() - 1;

        double importeOferta = 0;
        List<Rango> rangos = new ArrayList<>();
        if (getStayDates().getRanges().size() == 0) {
            rangos.add(new Rango(l.getStart(), l.getEnd().minusDays(1)));
        } else {
            for (DatesRange r : getStayDates().getRanges()) {
                if (Helper.intersects(r.getStart(), r.getEnd(), l.getStart(), l.getEnd())) {
                    Rango rx = new Rango(r.getStart(), r.getEnd(), l.getStart(), l.getEnd(), noches);
                    rangos.add(rx);
                }
            }
        }


        boolean[] aplica = new boolean[noches];

        for (Rango rx : rangos) for (int i = rx.getDesde(); i < rx.getHasta(); i++) aplica[i] = true;

        for (int i = 0; i < aplica.length; i++) if (aplica[i]) {
            double importe = getValue();
            if (isPercent()) {
                double base = 0;
                if (isOnRoom()) base += getTotal(valorEstancia, i);
                if (isOnBoardBasis()) base += getTotal(valorRegimen, i);
                //if (isOnDiscounts()) base = getTotal(valorDescuentos, i); //todo: añadir oferta sobre descuentos
                importe = base * getValue() / 100d;
            } else {
                if (Scope.BOOKING.equals(getScope()) && importeOferta != 0) {
                    importe = 0;
                } else {
                    if (Per.PAX.equals(getPer())) {
                        importe *= l.getRooms() * (l.getAdultsPerRoon() + l.getChildrenPerRoom());
                    } else if (Per.BOOKING.equals(getPer())) {
                        //todo: comprobar que no aplicamos la oferta más de una vez en toda la reserva (no con las otras líneas de reserva)
                    }
                }
            }
            valorOfertas[i] += -1d * importe;
            importeOferta += importe;
        }

        return importeOferta;
    }

    private double getTotal(double[][] valor, int noche) {
        double total = 0;
        for (int p = 0; p < valor[noche].length; p++) total += valor[noche][p];
        return Helper.roundEuros(total);
    }
}
