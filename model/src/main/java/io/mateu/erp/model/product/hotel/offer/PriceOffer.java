package io.mateu.erp.model.product.hotel.offer;

import io.mateu.erp.dispo.*;
import io.mateu.erp.dispo.interfaces.product.IBoard;
import io.mateu.erp.dispo.interfaces.product.IHotelOffer;
import io.mateu.erp.dispo.interfaces.product.IRoom;
import io.mateu.erp.model.product.hotel.BoardFare;
import io.mateu.erp.model.product.hotel.DatesRange;
import io.mateu.erp.model.product.hotel.RoomFare;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Convert;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Getter@Setter
public class PriceOffer extends AbstractHotelOffer {

    @Convert(converter = FarePerRoomConverter.class)
    private FarePerRoom farePerRoom = new FarePerRoom();

    @Override
    public double aplicar(IBoard board, IRoom room, LineaReserva lineaReserva, ValoracionLineaReserva vlr, IHotelOffer o, CondicionesPorRegimen cpr) {
        double importeOferta = 0;

        //todo: puede que estemos cambiando el precio del régimen base. Acalararlo!

        if (getFarePerRoom().getFares().containsKey(room.getCode()) && getFarePerRoom().getFares().get(room.getCode()).getFarePerBoard().containsKey(board.getCode())) {

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
            RoomFare[] rfs = new RoomFare[lineaReserva.getNumeroNoches()];
            BoardFare[] bfs = new BoardFare[lineaReserva.getNumeroNoches()];

            Map<RoomFare, RoomFare> cacherfs = new HashMap<>();
            Map<BoardFare, BoardFare> cachebfs = new HashMap<>();


            RoomFare rf = getFarePerRoom().getFares().get(room.getCode());
            BoardFare bf = getFarePerRoom().getFares().get(room.getCode()).getFarePerBoard().get(board.getCode());

            for (Rango rx : rangos) for (int i = rx.getDesde(); i < rx.getHasta(); i++) {
                aplica[i] = true;

                CondicionesPorDia cpd = cpr.getDias().get(i);
                RoomFare rfz;
                rfs[i] = rfz = cpd.getFarePerRoom().get(room.getCode());
                bfs[i] = rfz.getFarePerBoard().get(board.getCode());

                RoomFare rfx = cacherfs.get(rfs[i]);
                if (rfx == null) {
                    cacherfs.put(rfs[i], rfx = rfs[i].combineWith(rf));
                }

                BoardFare bfx = cachebfs.get(bfs[i]);
                if (bfx == null) {
                    cachebfs.put(bfs[i], bfx = bfs[i].combineWith(bf));
                }

            }

            for (int i = 0; i < aplica.length; i++) if (aplica[i]) {
                ValoracionPorDia vpd = vlr.getDias().get(i);

                ValoracionPorDia vpdx = new ValoracionPorDia(lineaReserva.getPax());

                Valoracion.aplicarTarifa(vpdx, cacherfs.get(rfs[i]), cachebfs.get(bfs[i]), lineaReserva, room);

                double importe = vpd.getTotalAlojamiento() + vpd.getTotalRegimen() - (vpdx.getTotalAlojamiento() + vpdx.getTotalRegimen());


                vpd.getOfertas().put(o, -1d * importe);
                vpd.setTotalAcumulado(vpd.getTotalAcumulado() - importe);
                importeOferta += importe;
            }

        }

        return importeOferta;
    }


}
