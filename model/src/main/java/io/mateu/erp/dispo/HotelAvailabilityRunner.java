package io.mateu.erp.dispo;

import io.mateu.erp.dispo.interfaces.common.IPartner;
import io.mateu.erp.dispo.interfaces.portfolio.IHotel;
import io.mateu.erp.dispo.interfaces.product.IHotelContract;
import io.mateu.erp.dispo.interfaces.product.IRoom;
import io.mateu.erp.model.product.hotel.CancellationRule;
import org.easytravelapi.common.Amount;
import org.easytravelapi.common.CancellationCost;
import org.easytravelapi.common.Remark;
import org.easytravelapi.hotel.*;
import org.easytravelapi.hotel.Occupancy;

import java.time.LocalDate;
import java.util.*;


public class HotelAvailabilityRunner {

    public AvailableHotel check(IPartner agency, IHotel hotel, long idAgencia, long idPos, ModeloDispo modelo, DispoRQ rq) {
        return check(agency, hotel, idAgencia, idPos, modelo, rq, false, LocalDate.now());
    }

    public AvailableHotel check(IPartner agency, IHotel hotel, long idAgencia, long idPos, ModeloDispo modelo, DispoRQ rq, boolean ignoreMINLOS, LocalDate formalizationDate) {

        if (DispoLogger.isTraceEnabled()) DispoLogger.trace("check(" + agency.getName() + "," + hotel.getName() + "," + rq.toString() + "," + ignoreMINLOS + "," + formalizationDate + ")");

        // todo: añadir ifs para ahorar proceso

        AvailableHotel ah = new AvailableHotel();

        ah.setBestDeal("NOTAVAILABLE");

        Amount bestDeal = null;


        if (agency.isActive() && hotel.isActive()) {

            // comprobar paros de ventas

            ParosVentas paros = new ParosVentas(hotel, rq.getCheckInLocalDate(), rq.getCheckOutLocalDate(), rq.getTotalNights());

            boolean parado = false;
            for (ParoDia pd : paros.getDias()) if (pd.isAllClosed() || (pd.getClientsClosed().contains(idAgencia) && pd.getRoomsClosed().size() == 0)) parado = true;

            if (!parado) {

                // comprobar cupo

                Cupos cupos = new Cupos(hotel, paros.getResumen(), rq);

                List<LineaReserva> lineasReserva = traducirParaPrecio(hotel, rq);


                for (Cupo cupo : cupos.getCupos()) {

                    // combinar habitaciones

                    CombinacionesHabitaciones combinacionesHabitaciones = new CombinacionesHabitaciones(hotel, lineasReserva, cupo);

                    if (combinacionesHabitaciones.getCombinaciones().size() > 0) {

                        // seleccionar contratos y ofertas

                        ContratosYOfertas contratosYOfertas = new ContratosYOfertas(agency, hotel, rq.getCheckInLocalDate(), rq.getCheckOutLocalDate(), rq.getRelease(), rq.getTotalNights(), lineasReserva, paros, cupo, combinacionesHabitaciones, formalizationDate);

                        // combinar contratos y ofertas

                        CombinacionesContratosOfertas combinacionesContratosOfertas = new CombinacionesContratosOfertas(contratosYOfertas);

                        for (CombinacionContratosOfertas combinacionContratosOfertas : combinacionesContratosOfertas.getCombinaciones()) {

                            for (CombinacionHabitaciones combinacionHabitaciones : combinacionesHabitaciones.getCombinaciones()) {

                                List<BoardPrice> opcionesValidas = new ArrayList<>();

                                // calcular y acumular resultado
                                Valoracion v = new Valoracion(rq, hotel, combinacionContratosOfertas, lineasReserva, combinacionHabitaciones, cupo, ignoreMINLOS);
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
                                    for (int i = 0; i < lineasReserva.size(); i++) {

                                        LineaReserva lineaReserva = lineasReserva.get(i);

                                        Occupancy oc = rq.getOccupancies().get(i);

                                        Allocation a;
                                        o.getDistribution().add(a = new Allocation());

                                        a.setNumberOfRooms(oc.getNumberOfRooms());
                                        a.setAges(oc.getAges());
                                        a.setPaxPerRoom(oc.getPaxPerRoom());
                                        IRoom r = combinacionHabitaciones.getAsignacion().get(lineaReserva.getFirmaOcupacion());
                                        a.setRoomId(r.getCode());
                                        a.setRoomName(r.getName());

                                        if (i > 0) sb.append(" and ");
                                        sb.append(lineaReserva.getPax());
                                        sb.append(" pax in ");
                                        sb.append(1);
                                        sb.append(" ");
                                        sb.append(a.getRoomName());
                                    }

                                    o.setDistributionString(sb.toString());

                                    for (BoardPrice x : preciosMasBaratosPorRegimen.values()) {
                                        KeyValue k = new KeyValue(rq, idAgencia, idPos, hotel.getId(), combinacionContratosOfertas.getContratos().get(0).getId(), o.getDistribution(), x);
                                        x.setKey(k.toString());
                                        o.getPrices().add(x);
                                        if (bestDeal == null || bestDeal.getValue() < x.getNetPrice().getValue()) bestDeal = x.getNetPrice();
                                    }
                                }

                            }

                        }

                    }

                }

            }


        }


        // si hay resultados completar el hotel. Si no, devolver null

        if (true || ah.getOptions().size() > 0) {
            ah.setHotelId("" + hotel.getId());
            ah.setHotelName(hotel.getName());
            ah.setLatitude(hotel.getLat());
            ah.setLongitude(hotel.getLon());
            ah.setHotelCategoryId(hotel.getCategoryId());
            ah.setHotelCategoryName(hotel.getCategoryName());

            if (bestDeal != null) ah.setBestDeal("" + bestDeal.getValue() + " " + bestDeal.getCurrencyIsoCode());

            return ah;
        } else return null;

    }

    private List<LineaReserva> traducirParaPrecio(IHotel hotel, DispoRQ rq) {
        List<LineaReserva> l = new ArrayList<>();
        for (Occupancy o : rq.getOccupancies()) {

            List<Integer>[] edades = new List[o.getNumberOfRooms()];
            int i = 0;
            if (o.getAges() != null) for (int e : o.getAges()) {
                int posroom = i % o.getNumberOfRooms();
                if (edades[posroom] == null) edades[posroom] = new ArrayList<>();
                edades[posroom].add(e);
                i++;
            }


            for (int h = 0; h < o.getNumberOfRooms(); h++) {
                int ju = 0;
                int ni = 0;
                int bb = 0;
                if (edades[h] != null) for (int e : edades[h]) {
                    if (e < hotel.getChildStartAge()) bb++;
                    else if (e < hotel.getJuniorStartAge()) ni++;
                    else if (e < hotel.getAdultStartAge()) ju++;
                }
                if (hotel.getJuniorStartAge() <= 0) {
                    ni += ju;
                    ju = 0;
                }
                int ad = o.getPaxPerRoom() - ju - ni - bb;

                l.add(new LineaReserva(rq.getCheckInLocalDate(), rq.getCheckOutLocalDate(), rq.getRelease(), rq.getTotalNights(), ad, ju, ni, bb, Helper.toIntArray(edades[h])));
            }
        }
        return l;
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
