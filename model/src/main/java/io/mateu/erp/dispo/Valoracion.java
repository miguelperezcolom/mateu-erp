package io.mateu.erp.dispo;

import io.mateu.erp.dispo.interfaces.portfolio.IHotel;
import io.mateu.erp.dispo.interfaces.product.IBoard;
import io.mateu.erp.dispo.interfaces.product.IHotelContract;
import io.mateu.erp.dispo.interfaces.product.IHotelOffer;
import io.mateu.erp.dispo.interfaces.product.IRoom;
import io.mateu.erp.model.product.hotel.*;
import io.mateu.mdd.core.data.FareValue;
import io.mateu.mdd.core.util.DatesRange;
import lombok.Getter;
import lombok.Setter;
import org.easytravelapi.hotel.BoardPrice;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

@Getter@Setter
public class Valoracion {

    private final CombinacionHabitaciones combinacionHabitaciones;
    private final CombinacionContratosOfertas combinacionContratosOfertas;
    private final Cupo cupo;
    private final IHotel hotel;
    private final LocalDate entrada;
    private final int totalNoches;
    private final LocalDate salida;
    private final List<LineaReserva> lineasReserva;
    private Condiciones condiciones;
    private boolean ignoreMINLOS;

    private Map<IBoard, RestriccionesPorRegimen> restriccionesPorRegimen = new HashMap<>();
    private Map<IBoard, SuplementosPorRegimen> suplementosPorRegimen = new HashMap<>();
    private Map<IBoard, OfertasPorRegimen> ofertasPorRegimen = new HashMap<>();

    private List<IBoard> validBoards = new ArrayList<>();
    private List<DesglosePrecios> desgloses;


    public Valoracion(DispoRQ rq, IHotel hotel, CombinacionContratosOfertas combinacionContratosOfertas, List<LineaReserva> lineasReserva, CombinacionHabitaciones combinacionHabitaciones, Cupo cupo, boolean ignoreMINLOS) {


        // fijamos parámetros
        this.hotel = hotel;
        this.combinacionContratosOfertas = combinacionContratosOfertas;
        this.cupo = cupo;
        this.lineasReserva = lineasReserva;
        this.combinacionHabitaciones = combinacionHabitaciones;
        this.ignoreMINLOS = ignoreMINLOS;
        this.entrada = rq.getCheckInLocalDate();
        this.salida = rq.getCheckOutLocalDate();
        this.totalNoches = rq.getTotalNights();

        // rellenamos condiciones
        for (IHotelContract contrato : combinacionContratosOfertas.getContratos()) add(rq, hotel, contrato);
        for (IHotelOffer oferta : combinacionContratosOfertas.getOfertas()) add(rq, oferta);

    }

    /**
     * añadimos las condiciones de este contrato. básicamente rellenamos un desglose por día de estancia
     * @param rq
     * @param hotel
     * @param contrato
     */
    private void add(DispoRQ rq, IHotel hotel, IHotelContract contrato) {

        HotelContractPhoto terms = contrato.getTerms();

        if (terms != null) {

            // rellenamos con las condiciones del contrato

            Map<String, IBoard> boards = new HashMap<>();
            for (IBoard board : hotel.getBoards()) boards.put(board.getCode(), board);

            for (LinearFare f : terms.getFares()) {
                for (DatesRange dr : f.getDates())
                    if (Helper.intersects(dr.getStart(), dr.getEnd(), rq.getCheckInLocalDate(), rq.getCheckOutLocalDate())) {
                        Rango r = new Rango(dr.getStart(), dr.getEnd(), rq.getCheckInLocalDate(), rq.getCheckOutLocalDate(), rq.getTotalNights());
                        for (LinearFareLine l : f.getLines()) {
                            if (condiciones == null)
                                condiciones = new Condiciones(rq.getTotalNights());
                            for (int i = r.getDesde(); i <= r.getHasta(); i++)
                                if (condiciones.getDias().size() > i) {
                                    CondicionesPorDia cpd = condiciones.getDias().get(i);
                                    cpd.getFarePerRoomAndBoard().put(l.getRoomTypeCode().getCode() + "-" + l.getBoardTypeCode().getCode(), l);
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

                }
            }

            // rellenamos los suplementos

            for (IBoard board : hotel.getBoards()) {

                SuplementosPorRegimen rpr = suplementosPorRegimen.get(board);
                if (rpr == null) suplementosPorRegimen.put(board, rpr = new SuplementosPorRegimen());

                for (IRoom room : combinacionHabitaciones.getAsignacion().values()) {

                    SuplementosPorHabitacion rph = rpr.getSuplementosPorHabitacion().get(room);
                    if (rph == null) rpr.getSuplementosPorHabitacion().put(room, rph = new SuplementosPorHabitacion(rq.getTotalNights()));

                    for (Supplement r : terms.getSupplements()) {
                        if (Helper.intersects(r.getStart(), r.getEnd(), rq.getCheckInLocalDate(), rq.getCheckOutLocalDate())) {
                            Rango rx = new Rango(r.getStart(), r.getEnd(), rq.getCheckInLocalDate(), rq.getCheckOutLocalDate(), rq.getTotalNights());

                            for (int i = rx.getDesde(); i <= rx.getHasta(); i++) if (i < rph.getDias().size()) {
                                SuplementosPorDia rpd = rph.getDias().get(i);
                                rpd.getSuplementos().add(r);
                            }
                        }
                    }

                }
            }


        }


    }

    /**
     * añadimos las condiciones de esta oferta aplicando ya algunos filtros
     * @param rq
     * @param oferta
     */
    private void add(DispoRQ rq, IHotelOffer oferta) {

        //todo: revisar, especialmente si podemos ahorrar cálculo

        for (IBoard board : hotel.getBoards()) {

            OfertasPorRegimen rpr = ofertasPorRegimen.get(board);
            if (rpr == null) ofertasPorRegimen.put(board, rpr = new OfertasPorRegimen());

            for (IRoom room : combinacionHabitaciones.getAsignacion().values()) {

                OfertasPorHabitacion rph = rpr.getOfertasPorHabitacion().get(room);
                if (rph == null) rpr.getOfertasPorHabitacion().put(room, rph = new OfertasPorHabitacion());

                rph.getOfertas().add(oferta);

            }
        }
    }


    private List<DesglosePrecios> valorar() {
        List<DesglosePrecios> resultados = new ArrayList<>();

        boolean onRequest = false;
        String onRequestText = null;

        boolean cupoOk = true;
        Map<String, Integer> cupoNecesario = new HashMap<>();
        for (LineaReserva l : lineasReserva) {
            IRoom r = combinacionHabitaciones.getAsignacion().get(l.getFirmaOcupacion());
            String code = r.getCode();
            if (r.getInventoryPropietaryRoomCode() != null) code = r.getInventoryPropietaryRoomCode();
            Integer x = cupoNecesario.get(code);
            cupoNecesario.put(code, ((x != null)?x:0) + 1);
        }
        for (String code : cupoNecesario.keySet()) {

            Integer disponible = cupo.getResumen().getDisponible().get(code);
            if (disponible == null || disponible < cupoNecesario.get(code)) cupoOk = false;

            if (!cupoOk) break;
        }

        if (!cupoOk) {
            onRequest = true;
            onRequestText = "Allotment";
        }

        for (IBoard board : hotel.getBoards()) {
            RestriccionesPorRegimen rpr = restriccionesPorRegimen.get(board);

            List<Supplement> suplementosAplicados = new ArrayList<>();

            Map<String, ValoracionLineaReserva> cache = new HashMap<>();

            if (true) {

                // comprobamos que hay precio todos los días
                boolean ok = true;

                for (IRoom room : combinacionHabitaciones.getAsignacion().values()) {
                    if (condiciones != null) {
                        for (CondicionesPorDia vpd : condiciones.getDias())
                            if (vpd.getFarePerRoomAndBoard().get(room.getCode() + "-" + board.getCode()) == null) {
                                ok = false;
                                break;
                            } else {
                                if (vpd.getFarePerRoomAndBoard().get(room.getCode() + "-" + board.getCode()) == null) {
                                    ok = false;
                                    break;
                                }
                            }
                    } else {
                        ok = false;
                        break;
                    }
                }


                //todo: guardar las valoraciones por firma de ocupación para reutilizarlas

                if (ok) { // hay precio para todos los días

                    boolean restriccionesOk = true;

                    boolean tarifaOk = true;

                    DesglosePrecios desglose = new DesglosePrecios(board);
                    
                    for (LineaReserva lineaReserva : lineasReserva) {
                        IRoom room = combinacionHabitaciones.getAsignacion().get(lineaReserva.getFirmaOcupacion());

                        String firmaLineaReserva = "" + lineaReserva.getFirmaOcupacion() + "-" + room.getCode();

                        ValoracionLineaReserva vlr = cache.get(firmaLineaReserva);
                        if (vlr != null) {

                        } else {
                            vlr = new ValoracionLineaReserva(lineaReserva, totalNoches);

                            int noche = 0;
                            for (CondicionesPorDia cpd : condiciones.getDias()) {
                                LinearFareLine rf = cpd.getFarePerRoomAndBoard().get(room.getCode() + "-" + board.getCode());

                                ValoracionPorDia vpd = vlr.getDias().get(noche);

                                aplicarTarifa(vpd, rf, lineaReserva, room);

                        /*
                        i.setDesayuno(bf.getPaxPrice().getValue());
                        i.setAlmuerzo(bf.getPaxPrice().getValue());
                        i.setCena(bf.getPaxPrice().getValue());
                        i.setExtrasRegimen(bf.getPaxPrice().getValue());
                        i.setExtrasAlojamiento(bf.getPaxPrice().getValue());
                        */
                                noche++;
                            }

                            // comprobamos restricciones


                            long release = Helper.noches(LocalDate.now(), entrada);
                            {
                                RestriccionesPorHabitacion rph = rpr.getRestriccionesPorHabitacion().get(room);
                                for (RestriccionesPorDia rpd : rph.getDias()) {
                                    for (ReleaseRule r : rpd.getReleases()) if (r.getRelease() > release) {
                                        if (r.getRooms().size() == 0 || r.getRooms().contains(room.getId())) {
                                            onRequest = true;
                                            onRequestText = "Release";
                                            break;
                                        }
                                    }

                                    for (MinimumStayRule r : rpd.getMinimumStays()) if (r.getNights() > totalNoches) {
                                        if (r.getRooms().size() == 0 || r.getRooms().contains(room.getId())) {
                                            if (r.getBoards().size() == 0 || r.getBoards().contains(board.getCode())) {
                                                onRequest = r.isOnRequest();
                                                restriccionesOk &= !r.isOnRequest();
                                                //todo: aplicarOferta suplementos
                                                onRequestText = "Minimum stay";
                                                break;
                                            }
                                        }
                                    }

                                    int totalNoches = (int) DAYS.between(entrada, salida);

                                    int weekDayIn = entrada.getDayOfWeek().getValue();
                                    int weekDayOut = salida.getDayOfWeek().getValue();

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
                                                    numeroPresentes += (r.getWeekDays()[entrada.plusDays(i).getDayOfWeek().getValue()])?1:0;
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
                        }

                        // aplicarOferta suplementos
                        SuplementosPorRegimen spr = suplementosPorRegimen.get(board);
                        if (spr != null)  {

                            int noche = 0;
                            for (SuplementosPorDia spd : spr.getSuplementosPorHabitacion().get(room).getDias()) {
                                for (Supplement s : spd.getSuplementos()) {
                                    double importeSuplemento = ((SupplementPer.PAX.equals(s.getPer()))?lineaReserva.getPax():1) * s.getValue();
                                    if (s.getPercent() != 0) {
                                        double baseSuplemento = 0;
                                        if (s.isOnStay()) baseSuplemento += vlr.getDias().get(noche).getTotalAlojamiento();
                                        if (s.isOnMealplan()) baseSuplemento += vlr.getDias().get(noche).getTotalRegimen();
                                        if (s.isOnAccumulated()) baseSuplemento = vlr.getDias().get(noche).getTotalAcumulado();
                                        importeSuplemento += s.getPercent() * baseSuplemento / 100d;
                                    }

                                    if (SupplementScope.NIGHT.equals(s.getScope()) || !suplementosAplicados.contains(s)) {
                                        suplementosAplicados.add(s);
                                        vlr.getDias().get(noche).getSuplementos().put(s, importeSuplemento);
                                    }

                                    vlr.getDias().get(noche).setTotalAcumulado(vlr.getDias().get(noche).getTotalAcumulado() + importeSuplemento);
                                }
                                noche++;
                            }

                        }

                        // aplicarOferta ofertas
                        OfertasPorRegimen opr = ofertasPorRegimen.get(board);
                        if (opr != null) {

                            for (IHotelOffer o : opr.getOfertasPorHabitacion().get(room).getOfertas()) {

                                double importeOferta = aplicarOferta(board, room, lineaReserva, vlr, o, condiciones);

                            }
                        }


                        desglose.getValoracionLineas().put(lineaReserva, vlr);

                    } // fin for linea reserva


                    if (restriccionesOk) {
                        resultados.add(desglose);
                    }

                } // fin si hay precio todos los días


            } // fin si hay condiciones (precio)

        } // fin bucle regimen


        return resultados;
    }

    public static void aplicarTarifa(ValoracionPorDia vpd, LinearFareLine rf, LineaReserva lineaReserva, IRoom room) {

        vpd.setImporteHabitacion(rf.getLodgingPrice());

        int posjunior = 0;
        int posnino = 0;
        int posbebe = 0;

        double totalAlojamiento = 0;
        double totalRegimen = 0;

        for (int i = 0; i < lineaReserva.getPax(); i++) {
            ImportePorDia ipd = vpd.getImportesPax().get(i);
            ipd.setAlojamiento(rf.getAdultPrice());
            ipd.setDesayuno(rf.getMealAdultPrice());
            double precioEstanciaYRegimen = ipd.getAlojamiento() + ipd.getDesayuno();

            totalAlojamiento += ipd.getAlojamiento();
            totalRegimen += ipd.getDesayuno();

            FareValue tarifaPax = null;

            //if ((i == 0 && lineaReserva.getPax() == 1) || (i > 1)) tarifaPax = bf.getPaxDiscounts().get(i);
            if (i == 0 && lineaReserva.getPax() == 1) tarifaPax = rf.getSingleUsePrice();
            if (i > 1) tarifaPax = rf.getExtraAdultPrice();
            if (i >= lineaReserva.getAdultos() && i >= room.getMinAdultsForChildDiscount()) {
                // es un junior
                if (i >= (lineaReserva.getAdultos() + lineaReserva.getJuniors())) {
                    // es un niño
                    if (i >= (lineaReserva.getAdultos() + lineaReserva.getJuniors() + lineaReserva.getNinos())) {
                        // es un bebe
                        tarifaPax = rf.getExtraInfantPrice();
                        if (tarifaPax == null || posbebe == 0) tarifaPax = rf.getInfantPrice();
                        posbebe++;
                    } else {
                        tarifaPax = rf.getExtraChildPrice();
                        if (tarifaPax == null || posnino == 0) tarifaPax = rf.getChildPrice();
                        posnino++;
                    }

                } else {
                    tarifaPax = rf.getExtraJuniorPrice();
                    if (tarifaPax == null || posjunior == 0) tarifaPax = rf.getJuniorPrice();
                    posjunior++;
                }
            }

            if (tarifaPax != null) {
                double precioPax = tarifaPax.applicarA(precioEstanciaYRegimen);
                ipd.setDescuentoPax(precioPax - (ipd.getAlojamiento() + ipd.getDesayuno()));
                totalRegimen -= ipd.getDescuentoPax();
            }

        }

        vpd.setTotalAlojamiento(totalAlojamiento);
        vpd.setTotalRegimen(totalRegimen);
        vpd.setTotalAcumulado(totalAlojamiento + totalRegimen);

    }

    /**
     * aplicamos condiciones y devolvemos una lista de board prices.
     * @return
     */
    public List<BoardPrice> toBoardPrices() {

        List<BoardPrice> resultados = new ArrayList<>();
        desgloses = new ArrayList<>();

        for (DesglosePrecios d : valorar()) {
            desgloses.add(d);
            resultados.add(d.toBoardPrice());
        }

        if (DispoLogger.isTraceEnabled()) DispoLogger.trace(toString());

        return resultados;
    }

    private double aplicarOferta(IBoard board, IRoom room, LineaReserva lineaReserva, ValoracionLineaReserva vlr, IHotelOffer o, Condiciones cpr) {

        double importeOferta = o.aplicar(board, room, lineaReserva, vlr, o, cpr);

        return importeOferta;
    }

    @Override
    public String toString() {
        String json = "{}";

        try {

            Map<String, Object> data = new HashMap<>();

            data.put("hotel", getHotel().getName());
            data.put("entrada", "" + getEntrada());
            data.put("salida", "" + getSalida());
            data.put("noches", getTotalNoches());
            List<Map<String, Object>> datalineas = new ArrayList<>();
            for (LineaReserva l : getLineasReserva()) {
                Map<String, Object> d;
                datalineas.add(d = new HashMap<>());
                d.put("habitacion", combinacionHabitaciones.getAsignacion().get(l.getFirmaOcupacion()).getCode());
                d.put("pax", l.getPax());
                d.put("adultos", l.getAdultos());
                d.put("ninos", l.getNinos());
                d.put("bebes", l.getBebes());
                d.put("juniors", l.getJuniors());
                if (l.getEdades() != null) d.put("edades", Arrays.toString(l.getEdades()));
                d.put("firma", l.getFirmaOcupacion());
            }
            data.put("lineas", datalineas);
            data.put("ignoreMINLOS", isIgnoreMINLOS());

            List<Map<String, Object>> datadesgloses = new ArrayList<>();
            for (DesglosePrecios l : getDesgloses()) {
                Map<String, Object> d;
                datadesgloses.add(d = new HashMap<>());
                d.put("regimen", l.getBoard().getCode());
                d.put("textoonrequest", l.getOnRequestText());
                d.put("onrequest", l.isOnRequest());
                d.put("total", l.getTotal());

                l.getValoracionLineas();

                List<Map<String, Object>> datavaloracionlineas = new ArrayList<>();
                for (LineaReserva lx : getLineasReserva()) {
                    Map<String, Object> dx;
                    datavaloracionlineas.add(dx = new HashMap<>());
                    ValoracionLineaReserva v = l.getValoracionLineas().get(lx);
                    dx.put("total", v.getTotal());

                    LocalDate dia = LocalDate.of(entrada.getYear(), entrada.getMonth(), entrada.getDayOfMonth());
                    List<Map<String, Object>> dias = new ArrayList<>();
                    for (ValoracionPorDia vpd : v.getDias()) {
                        Map<String, Object> ddx;
                        dias.add(ddx = new HashMap<>());
                        ddx.put("habitacion", vpd.getImporteHabitacion());
                        ddx.put("dia", "" + dia);

                        List<Map<String, Object>> pax = new ArrayList<>();
                        for (ImportePorDia ipd : vpd.getImportesPax()) {
                            Map<String, Object> ddxp;
                            pax.add(ddxp = new HashMap<>());
                            ddxp.put("habitacion", ipd.getHabitacion());
                            ddxp.put("alojamiento", ipd.getAlojamiento());
                            ddxp.put("desayuno", ipd.getDesayuno());
                            ddxp.put("almuerzo", ipd.getAlmuerzo());
                            ddxp.put("cena", ipd.getCena());
                            ddxp.put("extrasalojamiento", ipd.getExtrasAlojamiento());
                            ddxp.put("extrasregimen", ipd.getExtrasRegimen());
                            ddxp.put("descuentopax", ipd.getDescuentoPax());

                            ddxp.put("total", ipd.getTotal());
                        }
                        ddx.put("pax", pax);


                        List<Map<String, Object>> sup = new ArrayList<>();
                        for (Supplement s : vpd.getSuplementos().keySet()) {
                            Map<String, Object> ddxp;
                            sup.add(ddxp = new HashMap<>());
                            ddxp.put("suplemento", s.getExtra());
                            ddxp.put("total", vpd.getSuplementos().get(s));
                        }
                        ddx.put("suplementos", sup);

                        List<Map<String, Object>> of = new ArrayList<>();
                        for (IHotelOffer s : vpd.getOfertas().keySet()) {
                            Map<String, Object> ddxp;
                            of.add(ddxp = new HashMap<>());
                            ddxp.put("oferta", s.getName());
                            ddxp.put("total", vpd.getOfertas().get(s));
                        }
                        ddx.put("ofertas", of);

                        ddx.put("total", vpd.getTotal());

                        dia = dia.plusDays(1);
                    }
                    dx.put("dias", dias);

                }
                d.put("valoracionlineas", datavaloracionlineas);
            }
            data.put("desgloses", datadesgloses);

            /*
    private final CombinacionContratosOfertas combinacionContratosOfertas;
    private final Cupo cupo;
    private Map<IBoard, Condiciones> condicionesPorRegimen = new HashMap<>();

    private Map<IBoard, RestriccionesPorRegimen> restriccionesPorRegimen = new HashMap<>();
    private Map<IBoard, SuplementosPorRegimen> suplementosPorRegimen = new HashMap<>();
    private Map<IBoard, OfertasPorRegimen> ofertasPorRegimen = new HashMap<>();

    private List<IBoard> validBoards = new ArrayList<>();

             */



            json = Helper.toJson(data);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return json;
    }
}
