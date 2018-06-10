package io.mateu.erp.dispo;

import io.mateu.erp.dispo.interfaces.common.IPartner;
import io.mateu.erp.dispo.interfaces.portfolio.IHotel;
import io.mateu.erp.dispo.interfaces.product.IHotelContract;
import io.mateu.erp.dispo.interfaces.product.IHotelOffer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class ContratosYOfertas {

    private List<IHotelContract> contratos = new ArrayList<>();

    private List<IHotelOffer> ofertas = new ArrayList<>();

    public ContratosYOfertas(IPartner agency, IHotel hotel, LocalDate entrada, LocalDate salida, int release, int noches, List<LineaReserva> ocupaciones, ParosVentas paros, Cupo cupo, CombinacionesHabitaciones combinacionesHabitaciones, LocalDate formalizationDate) {

        for (IHotelContract c : hotel.getContracts()) {
            if (Helper.cabe(c.getValidFrom(), c.getValidTo(), entrada, salida)) {

                if (c.getTargets().size() == 0 || c.getTargets().contains(agency)) {

                    for (CombinacionHabitaciones ch : combinacionesHabitaciones.getCombinaciones()) {

                        //todo: es posible contratos diferentes para habitaciones diferentes? Y por fechas? lo permitimos?

                        boolean hayPrecioParaTodas = true;
//                        for (IRoom r : ch.getAsignacion().values()) {
//                            if (c.getTerms() != null && !c.getTerms().getRooms().contains(r.getCode())) {
//                                hayPrecioParaTodas = false;
//                                break;
//                            }
//                        }
                        if (hayPrecioParaTodas) {
                            contratos.add(c);
                            break;
                        }

                    }

                }

            }
        }



        /*


    @Convert(converter = DatesRangeListConverter.class)
    private DatesRanges checkinDates = new DatesRanges();

    @Convert(converter = DatesRangeListConverter.class)
    private DatesRanges stayDates = new DatesRanges();
         */

        //todo: añadir ofertas
        for (IHotelOffer o : hotel.getOffers()) if (o.isActive()) {

            boolean ok = o.getBookingWindowFrom() == null || !o.getBookingWindowFrom().isBefore(formalizationDate);

            ok = ok && o.getBookingWindowTo() == null || !o.getBookingWindowTo().isAfter(formalizationDate);

            ok = ok && o.getLastCheckout() == null || !o.getLastCheckout().isBefore(salida);

            ok = ok && o.getApplicationMinimumNights() <= noches;

            ok = ok && o.getApplicationRelease() <= release;

            ok = ok && o.getApplicationMinimumNights() <= noches;

            ok = ok && o.getTargets().size() == 0 || o.getTargets().contains(agency);

            //todo: filtrar más en este paso (fechas estancia, entrada y habitaciones)

            if (ok) ofertas.add(o);

        }

    }
}
