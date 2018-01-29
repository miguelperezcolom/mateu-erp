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
public class FreeChildrenOffer extends AbstractHotelOffer {

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
            double importe = 0;
            for (int p = lineaReserva.getAdultos(); p < lineaReserva.getPax(); p++) {
                double base = 0;
                ImportePorDia ipd = vpd.getImportesPax().get(p);
                if (isOnRoom()) base += ipd.getAlojamiento() + ipd.getHabitacion() + ipd.getExtrasAlojamiento();
                if (isOnBoardBasis()) base += ipd.getDesayuno() + ipd.getAlmuerzo() + ipd.getCena() + ipd.getExtrasRegimen();
                if (isOnDiscounts()) base = ipd.getTotal();
                importe += base;
            }
            vpd.getOfertas().put(o, -1d * importe);
            vpd.setTotalAcumulado(vpd.getTotalAcumulado() - importe);
            importeOferta += importe;
        }

        return importeOferta;
    }

}
