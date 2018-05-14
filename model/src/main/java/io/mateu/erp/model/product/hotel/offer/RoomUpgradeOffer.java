package io.mateu.erp.model.product.hotel.offer;

import io.mateu.erp.dispo.*;
import io.mateu.erp.dispo.interfaces.product.IBoard;
import io.mateu.erp.dispo.interfaces.product.IHotelOffer;
import io.mateu.erp.dispo.interfaces.product.IRoom;
import io.mateu.erp.model.product.hotel.LinearFareLine;
import io.mateu.erp.model.product.hotel.Room;
import io.mateu.ui.mdd.server.util.DatesRange;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter@Setter
public class RoomUpgradeOffer extends AbstractHotelOffer {

    @ManyToOne
    private Room get;

    @ManyToOne
    private Room pay;

    @Override
    public double aplicar(IBoard board, IRoom room, LineaReserva lineaReserva, ValoracionLineaReserva vlr, IHotelOffer o, Condiciones cpr) {
        double importeOferta = 0;

        if (room.getCode().equalsIgnoreCase(getGet().getCode())) {

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
            LinearFareLine[] rfs = new LinearFareLine[lineaReserva.getNumeroNoches()];

            boolean hayTarifa = true;

            for (Rango rx : rangos) for (int i = rx.getDesde(); i < rx.getHasta(); i++) {
                aplica[i] = true;

                CondicionesPorDia cpd = cpr.getDias().get(i);
                LinearFareLine rf;
                rfs[i] = rf = cpd.getFarePerRoomAndBoard().get(getPay().getCode() + "-" + board.getCode());

                if (rfs[i] == null) {
                    hayTarifa = false;
                    break;
                }
            }

            if (hayTarifa) for (int i = 0; i < aplica.length; i++) if (aplica[i]) {
                ValoracionPorDia vpd = vlr.getDias().get(i);

                ValoracionPorDia vpdx = new ValoracionPorDia(lineaReserva.getPax());

                Valoracion.aplicarTarifa(vpdx, rfs[i], lineaReserva, getPay());

                double importe = vpd.getTotalAlojamiento() + vpd.getTotalRegimen() - (vpdx.getTotalAlojamiento() + vpdx.getTotalRegimen());


                vpd.getOfertas().put(o, -1d * importe);
                vpd.setTotalAcumulado(vpd.getTotalAcumulado() - importe);
                importeOferta += importe;
            }

        }

        return importeOferta;
    }


}
