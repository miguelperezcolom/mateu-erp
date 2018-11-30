package io.mateu.erp.model.product.hotel.offer;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import io.mateu.erp.dispo.*;
import io.mateu.erp.dispo.interfaces.product.IBoard;
import io.mateu.erp.dispo.interfaces.product.IHotelOffer;
import io.mateu.erp.dispo.interfaces.product.IRoom;
import io.mateu.erp.model.product.hotel.*;
import io.mateu.mdd.core.annotations.DependsOn;
import io.mateu.mdd.core.annotations.SameLine;
import io.mateu.mdd.core.util.DatesRange;
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

    @DependsOn("hotel")
    public DataProvider getRoomTypeCodeDataProvider() {
        List<RoomType> l = new ArrayList<>();
        Hotel h = getHotel();
        if (h != null) for (Room r : h.getRooms()) {
            l.add(r.getType());
        }
        return new ListDataProvider<RoomType>(l);
    }



    @ManyToOne
    private BoardType board;

    @DependsOn("hotel")
    public DataProvider getBoardTypeCodeDataProvider() {
        List<BoardType> l = new ArrayList<>();
        Hotel h = getHotel();
        if (h != null) for (Board r : h.getBoards()) {
            l.add(r.getType());
        }
        return new ListDataProvider<BoardType>(l);
    }



    private double lodgingPrice;
    @SameLine
    private FareValue singleUsePrice;

    private double adultPrice;
    @SameLine
    private double mealAdultPrice;

    private FareValue juniorPrice;
    @SameLine
    private FareValue childPrice;
    @SameLine
    private FareValue infantPrice = new FareValue("0");




    private FareValue mealJuniorPrice;
    @SameLine
    private FareValue mealChildPrice;
    @SameLine
    private FareValue mealInfantPrice;




    private FareValue extraAdultPrice;
    @SameLine
    private FareValue extraJuniorPrice;
    @SameLine
    private FareValue extraChildPrice;
    @SameLine
    private FareValue extraInfantPrice;

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

                if (getLodgingPrice() >= 0) rfz.setLodgingPrice(getLodgingPrice());
                if (getAdultPrice() >= 0) rfz.setAdultPrice(getAdultPrice());
                if (getMealAdultPrice() >= 0) rfz.setMealAdultPrice(getMealAdultPrice());

                if (getJuniorPrice() != null) rfz.setJuniorPrice(new FareValue(getJuniorPrice()));
                if (getChildPrice() != null) rfz.setChildPrice(new FareValue(getChildPrice()));
                if (getInfantPrice() != null) rfz.setInfantPrice(new FareValue(getInfantPrice()));

                if (getMealJuniorPrice() != null) rfz.setMealJuniorPrice(new FareValue(getMealJuniorPrice()));
                if (getMealChildPrice() != null) rfz.setMealChildPrice(new FareValue(getMealChildPrice()));
                if (getMealInfantPrice() != null) rfz.setMealInfantPrice(new FareValue(getMealInfantPrice()));

                if (getExtraAdultPrice() != null) rfz.setExtraAdultPrice(new FareValue(getExtraAdultPrice()));
                if (getExtraJuniorPrice() != null) rfz.setExtraJuniorPrice(new FareValue(getExtraJuniorPrice()));
                if (getExtraChildPrice() != null) rfz.setExtraChildPrice(new FareValue(getExtraChildPrice()));
                if (getExtraInfantPrice() != null) rfz.setExtraInfantPrice(new FareValue(getExtraInfantPrice()));

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
