package io.mateu.erp.dispo;

import io.mateu.erp.dispo.interfaces.common.IActor;
import io.mateu.erp.dispo.interfaces.portfolio.IHotel;
import io.mateu.erp.dispo.interfaces.product.IHotelContract;
import io.mateu.erp.dispo.interfaces.product.IOferta;
import io.mateu.erp.model.product.hotel.CancellationRule;
import org.easytravelapi.common.Amount;
import org.easytravelapi.common.Booking;
import org.easytravelapi.common.CancellationCost;
import org.easytravelapi.common.Remark;
import org.easytravelapi.hotel.*;
import org.easytravelapi.hotel.Occupancy;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


public class HotelAvailabilityRunner {


    public AvailableHotel check(IActor agency, IHotel hotel, long idAgencia, long idPos, ModeloDispo modelo, DispoRQ rq) {

        // todo: añadir ifs para ahorar proceso

        AvailableHotel ah = new AvailableHotel();

        // comprobar paros de ventas

        ParosVentas paros = new ParosVentas(hotel, rq.getCheckInLocalDate(), rq.getCheckOutLocalDate(), rq.getTotalNights());

        for (ParoDia pd : paros.getDias()) if (pd.isAllClosed() || (pd.getClientsClosed().contains(idAgencia) && pd.getRoomsClosed().size() == 0)) return null;

        // comprobar cupo

        Cupos cupos = new Cupos(hotel, paros.getResumen(), rq);


        for (Cupo cupo : cupos.getCupos()) {

            // combinar habitaciones

            CombinacionesHabitaciones combinacionesHabitaciones = new CombinacionesHabitaciones(hotel, rq, cupo);

            if (combinacionesHabitaciones.getCombinaciones().size() > 0) {

                // seleccionar contratos y ofertas

                ContratosYOfertas contratosYOfertas = new ContratosYOfertas(agency, hotel, rq, paros, cupo, combinacionesHabitaciones);

                // combinar contratos y ofertas

                CombinacionesContratosOfertas combinacionesContratosOfertas = new CombinacionesContratosOfertas(contratosYOfertas);

                List<BoardPrice> opcionesValidas = new ArrayList<>();
                for (CombinacionContratosOfertas combinacionContratosOfertas : combinacionesContratosOfertas.getCombinaciones()) {


                    for (CombinacionHabitaciones combinacionHabitaciones : combinacionesHabitaciones.getCombinaciones()) {


                        // calcular y acumular resultado
                        Valoracion v = new Valoracion(rq, hotel, combinacionContratosOfertas, combinacionHabitaciones, cupo);
                        opcionesValidas.addAll(v.toBoardPrices());


                        // nos quedamos con las más baratas
                        Map<String, Double> minPorRegimen = new HashMap<>();
                        Map<String, BoardPrice> preciosMasBaratosPorRegimen = new HashMap<>();
                        for (BoardPrice o : opcionesValidas) {
                            if (!minPorRegimen.containsKey(o.getBoardBasisId()) || minPorRegimen.get(o.getBoardBasisId()) > o.getNetPrice().getValue()) {
                                minPorRegimen.put(o.getBoardBasisId(), o.getNetPrice().getValue());
                                preciosMasBaratosPorRegimen.put(o.getBoardBasisId(), o);
                            }
                        }

                        if (opcionesValidas.size() > 0) {
                            Option o;
                            ah.getOptions().add(o = new Option());
                            StringBuffer sb = new StringBuffer();
                            for (int i = 0; i < rq.getOccupancies().size(); i++) {

                                Occupancy oc = rq.getOccupancies().get(i);

                                Allocation a;
                                o.getDistribution().add(a = new Allocation());

                                a.setNumberOfRooms(oc.getNumberOfRooms());
                                a.setAges(oc.getAges());
                                a.setPaxPerRoom(oc.getPaxPerRoom());
                                a.setRoomId(combinacionHabitaciones.getAsignacion().get(oc).getCode());
                                a.setRoomName(combinacionHabitaciones.getAsignacion().get(oc).getName());

                                if (i > 0) sb.append(" and ");
                                sb.append(a.getNumberOfRooms() * a.getPaxPerRoom());
                                sb.append(" pax in ");
                                sb.append(a.getNumberOfRooms());
                                sb.append(" ");
                                sb.append(a.getRoomName());
                            }

                            o.setDistributionString(sb.toString());

                            for (BoardPrice x : preciosMasBaratosPorRegimen.values()) {
                                KeyValue k = new KeyValue(rq, idAgencia, idPos, hotel.getId(), combinacionContratosOfertas.getContratos().get(0).getId(), o.getDistribution(), x);
                                x.setKey(k.toString());
                                o.getPrices().add(x);
                            }
                        }

                    }

                }


            }


        }


        // si hay resultados completar el hotel. Si no, devolver null

        if (ah.getOptions().size() > 0) {
            ah.setHotelId("" + hotel.getId());
            ah.setHotelName(hotel.getName());
            ah.setLatitude(hotel.getLat());
            ah.setLongitude(hotel.getLon());
            ah.setHotelCategoryId(hotel.getCategoryId());
            ah.setHotelCategoryName(hotel.getCategoryName());
            return ah;
        } else return null;

    }

    public void fillHotelPriceDetailsResponse(GetHotelPriceDetailsRS rs, long idAgencia, String key, ModeloDispo modelo) {

        KeyValue k = new KeyValue(key);

        {
            Remark r;
            rs.getRemarks().add(r = new Remark());
            r.setType("info");
            r.setText("This is the test environment of the quoon platform");
        }

        IHotelContract contrato = modelo.getHotelContract(k.getSaleContractId());

        LocalDate ci = Helper.toDate(k.getCheckIn());

        for (CancellationRule r : contrato.getTerms().getCancellationRules()) {
            boolean aplica = false;
            if (aplica) {
                CancellationCost c;
                rs.getCancellationCosts().add(c = new CancellationCost());
                Date d = Helper.toDate(ci.minusDays(r.getRelease()).atStartOfDay().plusHours(12)); // añadir hora
                c.setGMTtime("" + d);
                c.setRetail(null);
                double coste = k.getBoardPrice().getNetPrice().getValue();
                coste *= r.getPercent() / 100d;
                coste = Helper.roundEuros(coste);
                c.setNet(new Amount(k.getBoardPrice().getNetPrice().getCurrencyIsoCode(), coste));
                c.setCommission(null);
            }
        }

    }


}
