package io.mateu.erp.model.product.hotel.offer;

import io.mateu.erp.dispo.*;
import io.mateu.erp.dispo.interfaces.product.IBoard;
import io.mateu.erp.dispo.interfaces.product.IHotelOffer;
import io.mateu.erp.dispo.interfaces.product.IRoom;
import io.mateu.erp.model.product.hotel.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter@Setter
public class PriceOffer extends AbstractHotelOffer {

    @ManyToOne
    private RoomType room;

    @ManyToOne
    private BoardType board;

    @Convert(converter = LinearFareLineConverter.class)
    private LinearFareLine fare;

    @Override
    public double aplicar(IBoard board, IRoom room, LineaReserva lineaReserva, ValoracionLineaReserva vlr, IHotelOffer o, Condiciones cpr) {
        double importeOferta = 0;


        //todo: puede que estemos cambiando el precio del régimen base. Acalararlo!

        if ((getRoom() == null || room.getCode().equals(getRoom().getCode())) && (getBoard() == null || board.getCode().equals(getBoard().getCode()))) {

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

            for (Rango rx : rangos) for (int i = rx.getDesde(); i < rx.getHasta(); i++) {
                aplica[i] = true;

                CondicionesPorDia cpd = cpr.getDias().get(i);
                LinearFareLine rfz;
                rfs[i] = rfz = new LinearFareLine(cpd.getFarePerRoomAndBoard().get(room.getCode() + "-" + board.getCode()));

                if (getFare().getLodgingPrice() >= 0) rfz.setLodgingPrice(getFare().getLodgingPrice());
                if (getFare().getAdultPrice() >= 0) rfz.setAdultPrice(getFare().getAdultPrice());
                if (getFare().getMealAdultPrice() >= 0) rfz.setMealAdultPrice(getFare().getMealAdultPrice());

                if (getFare().getJuniorPrice() != null) rfz.setJuniorPrice(new FareValue(getFare().getJuniorPrice()));
                if (getFare().getChildPrice() != null) rfz.setChildPrice(new FareValue(getFare().getChildPrice()));
                if (getFare().getInfantPrice() != null) rfz.setInfantPrice(new FareValue(getFare().getInfantPrice()));

                if (getFare().getMealJuniorPrice() != null) rfz.setMealJuniorPrice(new FareValue(getFare().getMealJuniorPrice()));
                if (getFare().getMealChildPrice() != null) rfz.setMealChildPrice(new FareValue(getFare().getMealChildPrice()));
                if (getFare().getMealInfantPrice() != null) rfz.setMealInfantPrice(new FareValue(getFare().getMealInfantPrice()));

                if (getFare().getExtraAdultPrice() != null) rfz.setExtraAdultPrice(new FareValue(getFare().getExtraAdultPrice()));
                if (getFare().getExtraJuniorPrice() != null) rfz.setExtraJuniorPrice(new FareValue(getFare().getExtraJuniorPrice()));
                if (getFare().getExtraChildPrice() != null) rfz.setExtraChildPrice(new FareValue(getFare().getExtraChildPrice()));
                if (getFare().getExtraInfantPrice() != null) rfz.setExtraInfantPrice(new FareValue(getFare().getExtraInfantPrice()));

            }

            for (int i = 0; i < aplica.length; i++) if (aplica[i]) {
                ValoracionPorDia vpd = vlr.getDias().get(i);

                ValoracionPorDia vpdx = new ValoracionPorDia(lineaReserva.getPax());

                Valoracion.aplicarTarifa(vpdx, rfs[i], lineaReserva, room);

                double importe = vpd.getTotalAlojamiento() + vpd.getTotalRegimen() - (vpdx.getTotalAlojamiento() + vpdx.getTotalRegimen());


                vpd.getOfertas().put(o, -1d * importe);
                vpd.setTotalAcumulado(vpd.getTotalAcumulado() - importe);
                importeOferta += importe;
            }

        }

        return importeOferta;
    }


}
