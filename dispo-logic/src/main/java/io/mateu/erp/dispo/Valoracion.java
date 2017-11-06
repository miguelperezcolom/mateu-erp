package io.mateu.erp.dispo;

import io.mateu.erp.dispo.interfaces.portfolio.IHotel;
import io.mateu.erp.dispo.interfaces.product.IBoard;
import io.mateu.erp.dispo.interfaces.product.IHotelContract;
import io.mateu.erp.dispo.interfaces.product.IOferta;
import io.mateu.erp.dispo.interfaces.product.IRoom;
import io.mateu.erp.model.product.hotel.*;
import lombok.Getter;
import lombok.Setter;
import org.easytravelapi.common.Amount;
import org.easytravelapi.hotel.BoardPrice;
import org.easytravelapi.hotel.Occupancy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;

@Getter@Setter
public class Valoracion {

    private final CombinacionHabitaciones combinacionHabitaciones;
    private final DispoRQ rq;
    private final CombinacionContratosOfertas combinacionContratosOfertas;
    private final Cupo cupo;
    private final IHotel hotel;
    private Map<IBoard, ValoracionPorRegimen> valoracionesPorRegimen = new HashMap<>();

    private Map<IBoard, RestriccionesPorRegimen> restriccionesPorRegimen = new HashMap<>();


    public Valoracion(DispoRQ rq, IHotel hotel, CombinacionContratosOfertas combinacionContratosOfertas, CombinacionHabitaciones combinacionHabitaciones, Cupo cupo) {

        this.hotel = hotel;
        this.combinacionContratosOfertas = combinacionContratosOfertas;
        this.cupo = cupo;
        this.combinacionHabitaciones = combinacionHabitaciones;
        this.rq = rq;
        
        for (IHotelContract contrato : combinacionContratosOfertas.getContratos()) add(rq, hotel, contrato);
        for (IOferta oferta : combinacionContratosOfertas.getOfertas()) add(oferta);

    }

    private void add(DispoRQ rq, IHotel hotel, IHotelContract contrato) {

        HotelContractPhoto terms = contrato.getTerms();

        if (terms != null) {

            // rellenamos con las condiciones del contrato

            for (IBoard board : hotel.getBoards()) {
                for (String idReg : terms.getBoards())
                    if (board.getCode().equals(idReg)) {
                        for (Fare f : terms.getFares()) {
                            for (DatesRange dr : f.getDates())
                                if (Helper.intersects(dr.getStart(), dr.getEnd(), rq.getCheckInLocalDate(), rq.getCheckOutLocalDate())) {
                                    Rango r = new Rango(dr.getStart(), dr.getEnd(), rq.getCheckInLocalDate(), rq.getCheckOutLocalDate(), rq.getTotalNights());
                                    ValoracionPorRegimen vpr = valoracionesPorRegimen.get(board);
                                    if (vpr == null) valoracionesPorRegimen.put(board, vpr = new ValoracionPorRegimen(rq.getTotalNights()));
                                    for (int i = r.getDesde(); i <= r.getHasta(); i++) {
                                        ValoracionPorDia vpd = vpr.getDias().get(i);
                                        vpd.getCondiciones().setFarePerRoom(f.getFarePerRoom());
                                    }
                                }
                        }
                    }
            }

            // rellenamos las restricciones

            for (IBoard board : hotel.getBoards()) {

                RestriccionesPorRegimen rpr = restriccionesPorRegimen.get(board);
                if (rpr == null) restriccionesPorRegimen.put(board, rpr = new RestriccionesPorRegimen());

                for (IRoom room : combinacionHabitaciones.getAsignacion().values()) {

                    RestriccionesPorHabitacion rph = rpr.getRestriccionesPorHabitacion().get(room);
                    if (rph == null) rpr.getRestriccionesPorHabitacion().put(room, rph = new RestriccionesPorHabitacion(rq.getTotalNights()));


                    for (ReleaseRule r : terms.getReleaseRules()) {
                        if (Helper.intersects(r.getStart(), r.getEnd(), rq.getCheckInLocalDate(), rq.getCheckOutLocalDate())) {
                            Rango rx = new Rango(r.getStart(), r.getEnd(), rq.getCheckInLocalDate(), rq.getCheckOutLocalDate(), rq.getTotalNights());

                            for (int i = rx.getDesde(); i <= rx.getHasta(); i++) {
                                RestriccionesPorDia rpd = rph.getDias().get(i);
                                rpd.getReleases().add(r);
                            }
                        }
                    }


                    for (MinimumStayRule r : terms.getMinimumStayRules()) {
                        if (Helper.intersects(r.getStart(), r.getEnd(), rq.getCheckInLocalDate(), rq.getCheckOutLocalDate())) {
                            Rango rx = new Rango(r.getStart(), r.getEnd(), rq.getCheckInLocalDate(), rq.getCheckOutLocalDate(), rq.getTotalNights());

                            for (int i = rx.getDesde(); i <= rx.getHasta(); i++) {
                                RestriccionesPorDia rpd = rph.getDias().get(i);
                                rpd.getMinimumStays().add(r);
                            }
                        }
                    }

                    for (WeekDaysRule r : terms.getWeekDaysRules()) {
                        if (Helper.intersects(r.getStart(), r.getEnd(), rq.getCheckInLocalDate(), rq.getCheckOutLocalDate())) {
                            Rango rx = new Rango(r.getStart(), r.getEnd(), rq.getCheckInLocalDate(), rq.getCheckOutLocalDate(), rq.getTotalNights());

                            for (int i = rx.getDesde(); i <= rx.getHasta(); i++) {
                                RestriccionesPorDia rpd = rph.getDias().get(i);
                                rpd.getWeekDays().add(r);
                            }
                        }
                    }

                    //todo: añadir suplementos


                }
            }




        }


    }

    private void add(IOferta oferta) {
    }


    public List<BoardPrice> toBoardPrices() {

        List<BoardPrice> resultados = new ArrayList<>();

        for (IBoard board : valoracionesPorRegimen.keySet()) {
            ValoracionPorRegimen vpr = valoracionesPorRegimen.get(board);
            RestriccionesPorRegimen rpr = restriccionesPorRegimen.get(board);

            if (vpr != null) {

                // comprobamos que hay precio todos los días
                boolean ok = true;

                for (IRoom room : combinacionHabitaciones.getAsignacion().values())
                    for (ValoracionPorDia vpd : vpr.getDias())
                        if (vpd.getCondiciones().getFarePerRoom().get(room.getCode()) == null) {
                            ok = false;
                            break;
                }

                if (ok) {
                    for (IRoom room : combinacionHabitaciones.getAsignacion().values()) for (ValoracionPorDia vpd : vpr.getDias()) {
                        RoomFare rf = vpd.getCondiciones().getFarePerRoom().get(room.getCode());
                        BoardFare bf = rf.getFarePerBoard().get(board.getCode());
                        ImportePorDia i = vpd.getImportes();
                        i.setHabitacion(bf.getRoomPrice().getValue());
                        i.setAlojamiento(bf.getPaxPrice().getValue());
                        //todo: completar tratamiento regimenes y descuentos pax y niños
                        /*
                        i.setDesayuno(bf.getPaxPrice().getValue());
                        i.setAlmuerzo(bf.getPaxPrice().getValue());
                        i.setCena(bf.getPaxPrice().getValue());
                        i.setExtrasRegimen(bf.getPaxPrice().getValue());
                        i.setExtrasAlojamiento(bf.getPaxPrice().getValue());
                        */
                    }

                    double totalParaEsteRegimen = 0;
                    for (Occupancy o : combinacionHabitaciones.getAsignacion().keySet()) for (ValoracionPorDia vpd : vpr.getDias()) {
                        IRoom room = combinacionHabitaciones.getAsignacion().get(o);
                        RoomFare rf = vpd.getCondiciones().getFarePerRoom().get(room.getCode());
                        BoardFare bf = rf.getFarePerBoard().get(board.getCode());
                        ImportePorDia i = vpd.getImportes();
                        i.setHabitacion(bf.getRoomPrice().getValue());
                        i.setAlojamiento(bf.getPaxPrice().getValue());
                        //todo: completar tratamiento regimenes y descuentos pax y niños
                        /*
                        i.setDesayuno(bf.getPaxPrice().getValue());
                        i.setAlmuerzo(bf.getPaxPrice().getValue());
                        i.setCena(bf.getPaxPrice().getValue());
                        i.setExtrasRegimen(bf.getPaxPrice().getValue());
                        i.setExtrasAlojamiento(bf.getPaxPrice().getValue());
                        */
                        totalParaEsteRegimen += i.getTotal(o);
                    }

                    // comprobamos restricciones

                    boolean restriccionesOk = true;
                    boolean onRequest = false;
                    String onRequestText = null;

                    boolean cupoOk = true;
                    for (Occupancy o : combinacionHabitaciones.getAsignacion().keySet()) {

                        Integer disponible = cupo.getResumen().getDisponible().get(combinacionHabitaciones.getAsignacion().get(o).getCode());
                        if (disponible == null || disponible < o.getNumberOfRooms()) cupoOk = false;

                        if (!cupoOk) break;
                    }

                    if (!cupoOk) {
                        onRequest = true;
                        onRequestText = "Allotment";
                    }


                    long release = Helper.noches(LocalDate.now(), rq.getCheckInLocalDate());
                    for (IRoom room : combinacionHabitaciones.getAsignacion().values()) {
                        RestriccionesPorHabitacion rph = rpr.getRestriccionesPorHabitacion().get(room);
                        for (RestriccionesPorDia rpd : rph.getDias()) {
                            for (ReleaseRule r : rpd.getReleases()) if (r.getRelease() > release) {
                                if (r.getRooms().size() == 0 || r.getRooms().contains(room.getId())) {
                                    onRequest = true;
                                    onRequestText = "Release";
                                    break;
                                }
                            }

                            for (MinimumStayRule r : rpd.getMinimumStays()) if (r.getNights() > rq.getTotalNights()) {
                                if (r.getRooms().size() == 0 || r.getRooms().contains(room.getId())) {
                                    if (r.getBoards().size() == 0 || r.getBoards().contains(board.getCode())) {
                                        onRequest = r.isOnRequest();
                                        restriccionesOk &= !r.isOnRequest();
                                        //todo: aplicar suplementos
                                        onRequestText = "Minimum stay";
                                        break;
                                    }
                                }
                            }

                            int totalNoches = (int) DAYS.between(rq.getCheckInLocalDate(), rq.getCheckOutLocalDate());

                            int weekDayIn = rq.getCheckInLocalDate().getDayOfWeek().getValue();
                            int weekDayOut = rq.getCheckOutLocalDate().getDayOfWeek().getValue();

                            for (WeekDaysRule r : rpd.getWeekDays()) {
                                if (r.isCheckin() && !r.getWeekDays()[weekDayIn]) {
                                    onRequest = r.isOnRequest();
                                    restriccionesOk &= !r.isOnRequest();
                                    onRequestText = "Check in day";
                                    break;
                                }

                                if (r.isCheckout() && !r.getWeekDays()[weekDayOut]) {
                                    onRequest = r.isOnRequest();
                                    restriccionesOk &= !r.isOnRequest();
                                    onRequestText = "Check out day";
                                    break;
                                }

                                if (r.isStay()) {
                                    boolean todosPresentes = true;
                                    if (totalNoches < 7) {
                                        int numeroObligatorios = 0;
                                        for (int i = 0; i < r.getWeekDays().length; i++) if (r.getWeekDays()[i]) numeroObligatorios++;
                                        int numeroPresentes = 0;
                                        for (int i = 0; i < totalNoches; i++) {
                                            numeroPresentes += (r.getWeekDays()[rq.getCheckInLocalDate().plusDays(i).getDayOfWeek().getValue()])?1:0;
                                        }
                                        todosPresentes = numeroPresentes == numeroObligatorios;
                                    }
                                    if (!todosPresentes) {
                                        onRequest = r.isOnRequest();
                                        restriccionesOk &= !r.isOnRequest();
                                        onRequestText = "Stay day";
                                    }
                                }

                            }

                        }
                    }


                    if (restriccionesOk) {
                        BoardPrice p;
                        resultados.add(p = new BoardPrice());
                        p.setBoardBasisId(board.getCode());
                        p.setBoardBasisName(board.getName());
                        p.setNetPrice(new Amount("EUR", Helper.roundEuros(totalParaEsteRegimen)));

                        p.setOnRequest(onRequest);
                        p.setOnRequestText(onRequestText);
                    }

                }


            }
        }


        return resultados;
    }
}
