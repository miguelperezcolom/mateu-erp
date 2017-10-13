package io.mateu.erp.dispo;

import io.mateu.erp.dispo.interfaces.portfolio.IHotel;
import io.mateu.erp.dispo.interfaces.product.IHotelContract;
import io.mateu.erp.dispo.interfaces.product.IOferta;
import io.mateu.erp.dispo.interfaces.product.IRoom;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class ContratosYOfertas {

    private List<IHotelContract> contratos = new ArrayList<>();

    private List<IOferta> ofertas = new ArrayList<>();

    public ContratosYOfertas(IHotel hotel, DispoRQ rq, ParosVentas paros, Cupo cupo, CombinacionesHabitaciones combinacionesHabitaciones) {

        for (IHotelContract c : hotel.getContracts()) {
            if (Helper.cabe(c.getValidFrom(), c.getValidTo(), rq.getCheckInLocalDate(), rq.getCheckOutLocalDate())) {

                for (CombinacionHabitaciones ch : combinacionesHabitaciones.getCombinaciones()) {

                    //todo: es posible contratos diferentes para habitaciones diferentes? Y por fechas? lo permitimos?

                    boolean hayPrecioParaTodas = true;
                    for (IRoom r : ch.getAsignacion().values()) {
                        if (c.getTerms() != null && !c.getTerms().getRooms().contains(r.getCode())) {
                            hayPrecioParaTodas = false;
                            break;
                        }
                    }
                    if (hayPrecioParaTodas) {
                        contratos.add(c);
                        break;
                    }

                }

            }
        }



        //todo: añadir ofertas


    }
}
