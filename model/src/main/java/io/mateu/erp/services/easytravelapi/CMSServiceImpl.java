package io.mateu.erp.services.easytravelapi;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;
import io.mateu.erp.dispo.Occupancy;
import io.mateu.erp.model.authentication.ERPUser;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.ManagedEvent;
import io.mateu.erp.model.booking.parts.HotelBookingLine;
import io.mateu.erp.model.booking.parts.TourBooking;
import io.mateu.erp.model.partners.Agency;
import io.mateu.erp.model.product.generic.GenericProduct;
import io.mateu.erp.model.product.hotel.*;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.erp.model.product.tour.Circuit;
import io.mateu.erp.model.product.tour.Excursion;
import io.mateu.erp.model.product.tour.ExcursionShift;
import io.mateu.erp.model.world.Country;
import io.mateu.erp.model.world.Destination;
import io.mateu.erp.model.world.Resort;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;
import org.easytravelapi.CMSService;
import org.easytravelapi.cms.*;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;

public class CMSServiceImpl implements CMSService {
    @Override
    public GetHotelAvailabilityCalendarRS getHotelAvailabilityCalendar(String token, String resorts, int checkIn, int checkOut, String occupancies) throws Throwable {
        final GetHotelAvailabilityCalendarRS rs = new GetHotelAvailabilityCalendarRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);

        long idPos = Long.parseLong(System.getProperty("idpos", "1"));

        long idAgencia = 0;
        long idHotel = 0;
        String login = "";
        try {
            Credenciales creds = new Credenciales(new String(BaseEncoding.base64().decode(token)));
            if (!Strings.isNullOrEmpty(creds.getAgentId())) idAgencia = Long.parseLong(creds.getAgentId());
            if (!Strings.isNullOrEmpty(creds.getHotelId())) idHotel = Long.parseLong(creds.getHotelId());
            //rq.setLanguage(creds.getLan());
            login = creds.getLogin();
            //rq.setPassword(creds.getPass());
        } catch (IOException e) {
            e.printStackTrace();
        }

        long t0 = System.currentTimeMillis();

        List<Long> idsHoteles = new ArrayList<>();

        if (idHotel > 0) idsHoteles.add(idHotel);
        else {

            Helper.transact(new JPATransaction() {
                @Override
                public void run(EntityManager em) throws Throwable {

                    List<Destination> l = new ArrayList<>();

                    List<Hotel> hoteles = new ArrayList<>();

                    for (String s : Splitter.on(',')
                            .trimResults()
                            .omitEmptyStrings()
                            .split(resorts)) {
                        if (s.startsWith("cou")) {
                            em.find(Country.class, s.substring(4)).getDestinations().forEach(d -> d.getResorts().forEach(z -> z.getProducts().stream().filter(p -> p instanceof Hotel).forEach(p -> hoteles.add((Hotel) p))));
                        } else if (s.startsWith("des")) {
                            em.find(Destination.class, Long.parseLong(s.substring(4))).getResorts().forEach(z -> z.getProducts().stream().filter(p -> p instanceof Hotel).forEach(p -> hoteles.add((Hotel) p)));
                        } else if (s.startsWith("zon")) {
                            em.find(Resort.class, Long.parseLong(s.substring(4))).getProducts().stream().filter(p -> p instanceof Hotel).forEach(p -> hoteles.add((Hotel) p));
                        } else if (s.startsWith("hot")) {
                            hoteles.add(em.find(Hotel.class, Long.parseLong(s.substring(4))));
                        }
                    };


                    int numContratos = 0;
                    for (Hotel h : hoteles) {
                        numContratos += h.getContracts().size();
                    }

                    System.out.println("" + numContratos + " contratos encontrados");

                    for (Hotel h : hoteles) idsHoteles.add(h.getId());
                }
            });

        }

        if (idsHoteles.size() > 1) throw new Exception("You can only ask for the availability calendar for 1 hotel.");

        final int[] diasConDIsponibilidad = {0};

        long finalIdAgencia = idAgencia;
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                List<Hotel> hoteles = new ArrayList<>();
                for (long idHotel : idsHoteles) {
                    hoteles.add(em.find(Hotel.class, idHotel));
                }

                Hotel h = hoteles.get(0);

                Agency a = em.find(Agency.class, finalIdAgencia);

                System.out.println("checkIn=" + checkIn);
                System.out.println("checkOut=" + checkOut);

                LocalDate desde = LocalDate.now(); //LocalDate.parse("" + checkIn, DateTimeFormatter.ofPattern("yyyyMMdd"));
                desde = LocalDate.parse(desde.format(DateTimeFormatter.ofPattern("yyyyMM")) + "01", DateTimeFormatter.ofPattern("yyyyMMdd"));
                //System.out.println("" + hoteles.size() + " hoteles encontrados");

                List<StopSalesOperation> ops = StopSalesCalendar.getOperations(h, desde, desde.plusMonths(20));
                Map<LocalDate, Boolean>[] cuboParos = StopSalesCalendar.construirCubo(ops, desde, desde.plusMonths(20).minusDays(1), null, null, a);


                List<? extends Occupancy> ocs = HotelBookingServiceImpl.getOccupancies(occupancies);


                LocalDate checkInLocalDate = io.mateu.erp.dispo.Helper.toDate(checkIn);

                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                LocalDate hoy = LocalDate.now();

                { // solo hay 1 hotel


                    Map<RoomType, int[]> cupos = new HashMap<>();

                    List<InventoryCalendarCube> cubosCupo = new ArrayList<>();
                    for (Inventory i : h.getInventories()) cubosCupo.add(new InventoryCalendarCube(i));


                    LocalDate fechaInicioCupo = LocalDate.of(checkInLocalDate.getYear(), checkInLocalDate.getMonthValue(), 1);
                    int[] cupo = getCupo(h, fechaInicioCupo);

                    for (int posmes = 0; posmes < 20; posmes++) {
                        LocalDate d = desde.plusMonths(posmes);
                        int mesActual = d.getMonthValue();

                        HotelAvailabilityCalendarMonth cm;
                        rs.getMonths().add(cm = new HotelAvailabilityCalendarMonth(d.getMonth().toString() + " " + d.getYear(), d.getYear(), d.getMonthValue()));

                        HotelAvailabilityCalendarWeek cw = null;

                        for (int i = 1; i < d.getDayOfWeek().getValue(); i++) { // 1 a 7
                            if (cw == null || d.getDayOfWeek() == DayOfWeek.MONDAY) {
                                cm.getWeeks().add(cw = new HotelAvailabilityCalendarWeek());
                            }
                            HotelAvailabilityCalendarDay cd;
                            cw.getDays().add(cd = new HotelAvailabilityCalendarDay());
                            cd.setBlank(true);
                        }



                        while (d.getMonthValue() == mesActual) {

                            if (cw == null || d.getDayOfWeek() == DayOfWeek.MONDAY) {
                                cm.getWeeks().add(cw = new HotelAvailabilityCalendarWeek());
                            }

                            int nd = io.mateu.erp.dispo.Helper.toInt(d);
                            int ndx = io.mateu.erp.dispo.Helper.toInt(d.plusDays(1));

                            HotelAvailabilityCalendarDay cd;

                            cw.getDays().add(cd = new HotelAvailabilityCalendarDay(nd, d.getDayOfWeek().getValue(), d.getDayOfMonth(), d.format(df), "na"));


                            if (!cuboParos[0].getOrDefault(d, false)) cd.setStyleName("av");


                            int posFecha = new Long(DAYS.between(fechaInicioCupo, d)).intValue();
                            if (posFecha >= 0 && posFecha < cupo.length) {
                                if (cupo[posFecha] <= 0) cd.setStyleName("or");
                            } else cd.setStyleName("or");

                            /*
                            DispoRQ rq = new DispoRQ(hoy, nd, ndx, ocs, false);
                            AvailableHotel ah = new HotelAvailabilityRunner().check(a, h, finalIdAgencia, idPos, modelo, rq, true, hoy);
                            if (ah != null) {
                                cd.setStyleName("or");
                                boolean or = true;
                                for (Option o : ah.getOptions()) {
                                    for (BoardPrice p : o.getPrices()) {
                                        if (!p.isOnRequest()) {
                                            or = false;
                                            break;
                                        }
                                        if (!or) break;
                                    }
                                    if (!or) break;
                                }
                                if (!or) cd.setStyleName("av");
                                diasConDIsponibilidad[0]++;
                            }
                            */

                            d = d.plusDays(1);
                        }
                    }
                }


                //System.out.println(Helper.toJson(dispo));

            }
        });

        long t = System.currentTimeMillis();

        String msg = "Calendar shows " + diasConDIsponibilidad[0] + " available days. It consumed " + (t - t0) + " ms in the server.";

        System.out.println(msg);

        rs.setMsg(msg);

        return rs;
    }

    @Override
    public GetActivityAvailabilityCalendarRS getActivityAvailabilityCalendar(String token, String activityId) throws Throwable {
        GetActivityAvailabilityCalendarRS rs = new GetActivityAvailabilityCalendarRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);

        long idPos = Long.parseLong(System.getProperty("idpos", "1"));

        long idAgencia = 0;
        long idHotel = 0;
        String login = "";
        try {
            Credenciales creds = new Credenciales(new String(BaseEncoding.base64().decode(token)));
            if (!Strings.isNullOrEmpty(creds.getAgentId())) idAgencia = Long.parseLong(creds.getAgentId());
            if (!Strings.isNullOrEmpty(creds.getHotelId())) idHotel = Long.parseLong(creds.getHotelId());
            //rq.setLanguage(creds.getLan());
            login = creds.getLogin();
            //rq.setPassword(creds.getPass());
        } catch (IOException e) {
            e.printStackTrace();
        }

        long t0 = System.currentTimeMillis();

        Map<LocalDate, Boolean> dispo = new HashMap<>();


        Helper.notransact(em -> {

            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            LocalDate hoy = LocalDate.now();



            LocalDate fechaInicioCalendario = LocalDate.of(hoy.getYear(), hoy.getMonthValue(), 1);
            LocalDate fechaFinCalendario = fechaInicioCalendario.plusMonths(12);
            Excursion e = em.find(Excursion.class, Long.parseLong(activityId.split("-")[1]));

            System.out.println("hay " + e.getShifts().size() + " turnos para la excursión " + e.getName());
            e.getShifts().forEach(s -> {
                LocalDate d = fechaInicioCalendario.plusDays(0);
                while (!d.isAfter(fechaFinCalendario)) {

                    if (!d.isBefore(hoy)) {
                        if ((s.getStart() == null || !s.getStart().isAfter(d))) {
                            if ((s.getEnd() == null || !s.getEnd().isBefore(d))) {
                                if ((s.getWeekdays() == null || s.getWeekdays()[d.getDayOfWeek().getValue() - 1])) {
                                    dispo.put(d, true);
                                }
                            }
                        }
                    }

                    d = d.plusDays(1);
                }
            });

            System.out.println("dispo.size() = " + dispo.size());

            for (int posmes = 0; posmes < 20; posmes++) {
                LocalDate d = fechaInicioCalendario.plusMonths(posmes);
                int mesActual = d.getMonthValue();

                ActivityAvailabilityCalendarMonth cm;
                rs.getMonths().add(cm = new ActivityAvailabilityCalendarMonth(d.getMonth().toString() + " " + d.getYear(), d.getYear(), d.getMonthValue()));

                ActivityAvailabilityCalendarWeek cw = null;

                for (int i = 1; i < d.getDayOfWeek().getValue(); i++) { // 1 a 7
                    if (cw == null || d.getDayOfWeek() == DayOfWeek.MONDAY) {
                        cm.getWeeks().add(cw = new ActivityAvailabilityCalendarWeek());
                    }
                    ActivityAvailabilityCalendarDay cd;
                    cw.getDays().add(cd = new ActivityAvailabilityCalendarDay());
                    cd.setBlank(true);
                }


                while (d.getMonthValue() == mesActual) {

                    if (cw == null || d.getDayOfWeek() == DayOfWeek.MONDAY) {
                        cm.getWeeks().add(cw = new ActivityAvailabilityCalendarWeek());
                    }

                    int nd = io.mateu.erp.dispo.Helper.toInt(d);
                    int ndx = io.mateu.erp.dispo.Helper.toInt(d.plusDays(1));

                    ActivityAvailabilityCalendarDay cd;

                    cw.getDays().add(cd = new ActivityAvailabilityCalendarDay(nd, d.getDayOfWeek().getValue(), d.getDayOfMonth(), d.format(df), "na"));


                    if (dispo.getOrDefault(d, false)) cd.setStyleName("av");

                    d = d.plusDays(1);
                }
            }
        });

        long t = System.currentTimeMillis();

        String msg = "Calendar shows " + dispo.size() + " available days. It consumed " + (t - t0) + " ms in the server.";

        System.out.println(msg);

        rs.setMsg(msg);



        return rs;
    }

    @Override
    public GetActivityAvailabilityCalendarRS getCircuitAvailabilityCalendar(String token, String circuitId) throws Throwable {
        GetActivityAvailabilityCalendarRS rs = new GetActivityAvailabilityCalendarRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);

        long idPos = Long.parseLong(System.getProperty("idpos", "1"));

        long idAgencia = 0;
        long idHotel = 0;
        String login = "";
        try {
            Credenciales creds = new Credenciales(new String(BaseEncoding.base64().decode(token)));
            if (!Strings.isNullOrEmpty(creds.getAgentId())) idAgencia = Long.parseLong(creds.getAgentId());
            if (!Strings.isNullOrEmpty(creds.getHotelId())) idHotel = Long.parseLong(creds.getHotelId());
            //rq.setLanguage(creds.getLan());
            login = creds.getLogin();
            //rq.setPassword(creds.getPass());
        } catch (IOException e) {
            e.printStackTrace();
        }

        long t0 = System.currentTimeMillis();

        Map<LocalDate, Boolean> dispo = new HashMap<>();


        Helper.notransact(em -> {

            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            LocalDate hoy = LocalDate.now();



            LocalDate fechaInicioCalendario = LocalDate.of(hoy.getYear(), hoy.getMonthValue(), 1);
            LocalDate fechaFinCalendario = fechaInicioCalendario.plusMonths(12);
            Circuit e = em.find(Circuit.class, Long.parseLong(circuitId.split("-")[1]));

            System.out.println("hay " + e.getSchedule().size() + " turnos para la excursión " + e.getName());
            e.getSchedule().forEach(s -> {
                LocalDate d = fechaInicioCalendario.plusDays(0);
                while (!d.isAfter(fechaFinCalendario)) {

                    if (!d.isBefore(hoy)) {
                        if ((s.getStart() == null || !s.getStart().isAfter(d))) {
                            if ((s.getEnd() == null || !s.getEnd().isBefore(d))) {
                                if ((s.getWeekdays() == null || s.getWeekdays()[d.getDayOfWeek().getValue() - 1])) {
                                    dispo.put(d, true);
                                }
                            }
                        }
                    }

                    d = d.plusDays(1);
                }
            });

            System.out.println("dispo.size() = " + dispo.size());

            for (int posmes = 0; posmes < 20; posmes++) {
                LocalDate d = fechaInicioCalendario.plusMonths(posmes);
                int mesActual = d.getMonthValue();

                ActivityAvailabilityCalendarMonth cm;
                rs.getMonths().add(cm = new ActivityAvailabilityCalendarMonth(d.getMonth().toString() + " " + d.getYear(), d.getYear(), d.getMonthValue()));

                ActivityAvailabilityCalendarWeek cw = null;

                for (int i = 1; i < d.getDayOfWeek().getValue(); i++) { // 1 a 7
                    if (cw == null || d.getDayOfWeek() == DayOfWeek.MONDAY) {
                        cm.getWeeks().add(cw = new ActivityAvailabilityCalendarWeek());
                    }
                    ActivityAvailabilityCalendarDay cd;
                    cw.getDays().add(cd = new ActivityAvailabilityCalendarDay());
                    cd.setBlank(true);
                }


                while (d.getMonthValue() == mesActual) {

                    if (cw == null || d.getDayOfWeek() == DayOfWeek.MONDAY) {
                        cm.getWeeks().add(cw = new ActivityAvailabilityCalendarWeek());
                    }

                    int nd = io.mateu.erp.dispo.Helper.toInt(d);
                    int ndx = io.mateu.erp.dispo.Helper.toInt(d.plusDays(1));

                    ActivityAvailabilityCalendarDay cd;

                    cw.getDays().add(cd = new ActivityAvailabilityCalendarDay(nd, d.getDayOfWeek().getValue(), d.getDayOfMonth(), d.format(df), "na"));


                    if (dispo.getOrDefault(d, false)) cd.setStyleName("av");

                    d = d.plusDays(1);
                }
            }
        });

        long t = System.currentTimeMillis();

        String msg = "Calendar shows " + dispo.size() + " available days. It consumed " + (t - t0) + " ms in the server.";

        System.out.println(msg);

        rs.setMsg(msg);



        return rs;
    }

    @Override
    public GetActivityAvailabilityCalendarRS getGenericAvailabilityCalendar(String token, String productId) throws Throwable {
        GetActivityAvailabilityCalendarRS rs = new GetActivityAvailabilityCalendarRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);

        long idPos = Long.parseLong(System.getProperty("idpos", "1"));

        long idAgencia = 0;
        long idHotel = 0;
        String login = "";
        try {
            Credenciales creds = new Credenciales(new String(BaseEncoding.base64().decode(token)));
            if (!Strings.isNullOrEmpty(creds.getAgentId())) idAgencia = Long.parseLong(creds.getAgentId());
            if (!Strings.isNullOrEmpty(creds.getHotelId())) idHotel = Long.parseLong(creds.getHotelId());
            //rq.setLanguage(creds.getLan());
            login = creds.getLogin();
            //rq.setPassword(creds.getPass());
        } catch (IOException e) {
            e.printStackTrace();
        }

        long t0 = System.currentTimeMillis();

        Map<LocalDate, Boolean> dispo = new HashMap<>();


        Helper.notransact(em -> {

            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            LocalDate hoy = LocalDate.now();



            LocalDate fechaInicioCalendario = LocalDate.of(hoy.getYear(), hoy.getMonthValue(), 1);
            LocalDate fechaFinCalendario = fechaInicioCalendario.plusMonths(12);
            GenericProduct e = em.find(GenericProduct.class, Long.parseLong(productId.split("-")[1]));

            System.out.println("hay " + e.getAllotment().size() + " turnos para la excursión " + e.getName());
            e.getAllotment().forEach(s -> {
                LocalDate d = fechaInicioCalendario.plusDays(0);
                while (!d.isAfter(fechaFinCalendario)) {

                    if (!d.isBefore(hoy)) {
                        if ((s.getStart() == null || !s.getStart().isAfter(d))) {
                            if ((s.getEnd() == null || !s.getEnd().isBefore(d))) {
                                dispo.put(d, true);
                            }
                        }
                    }

                    d = d.plusDays(1);
                }
            });

            System.out.println("dispo.size() = " + dispo.size());

            for (int posmes = 0; posmes < 20; posmes++) {
                LocalDate d = fechaInicioCalendario.plusMonths(posmes);
                int mesActual = d.getMonthValue();

                ActivityAvailabilityCalendarMonth cm;
                rs.getMonths().add(cm = new ActivityAvailabilityCalendarMonth(d.getMonth().toString() + " " + d.getYear(), d.getYear(), d.getMonthValue()));

                ActivityAvailabilityCalendarWeek cw = null;

                for (int i = 1; i < d.getDayOfWeek().getValue(); i++) { // 1 a 7
                    if (cw == null || d.getDayOfWeek() == DayOfWeek.MONDAY) {
                        cm.getWeeks().add(cw = new ActivityAvailabilityCalendarWeek());
                    }
                    ActivityAvailabilityCalendarDay cd;
                    cw.getDays().add(cd = new ActivityAvailabilityCalendarDay());
                    cd.setBlank(true);
                }


                while (d.getMonthValue() == mesActual) {

                    if (cw == null || d.getDayOfWeek() == DayOfWeek.MONDAY) {
                        cm.getWeeks().add(cw = new ActivityAvailabilityCalendarWeek());
                    }

                    int nd = io.mateu.erp.dispo.Helper.toInt(d);
                    int ndx = io.mateu.erp.dispo.Helper.toInt(d.plusDays(1));

                    ActivityAvailabilityCalendarDay cd;

                    cw.getDays().add(cd = new ActivityAvailabilityCalendarDay(nd, d.getDayOfWeek().getValue(), d.getDayOfMonth(), d.format(df), "na"));


                    if (dispo.getOrDefault(d, false)) cd.setStyleName("av");

                    d = d.plusDays(1);
                }
            }
        });

        long t = System.currentTimeMillis();

        String msg = "Calendar shows " + dispo.size() + " available days. It consumed " + (t - t0) + " ms in the server.";

        System.out.println(msg);

        rs.setMsg(msg);



        return rs;
    }

    @Override
    public GetActivityCheckListRS getActivityCheckList(String token, int date) throws Throwable {
        GetActivityCheckListRS rs = new GetActivityCheckListRS();

        rs.setStatusCode(200);
        rs.setMsg("");

        LocalDate d = LocalDate.parse("" + date, DateTimeFormatter.ofPattern("yyyyMMdd"));

        Helper.notransact(em -> {

            ((List<Excursion>) em.createQuery("select x from " + Excursion.class.getName() + " x order by x.name").getResultList()).forEach(e -> {

                if (e.isActive()) {

                    boolean happens = false;

                    for (ExcursionShift s : e.getShifts()) {
                        if ((s.getStart() == null || !d.isBefore(s.getStart()))
                                && (s.getEnd() == null || !d.isAfter(s.getEnd()))
                                && s.getWeekdays()[d.getDayOfWeek().getValue()]
                                ) {
                            happens = true;
                            break;
                        }
                    }


                    if (happens) {

                        ActivityCheckItem i;
                        rs.getActivity().add(i = new ActivityCheckItem());

                        i.setActivityId("" + e.getId());
                        i.setDate(date);
                        if (e.getDataSheet() != null) {
                            if (e.getDataSheet().getDescription() != null) i.setDescription(e.getDataSheet().getDescription().toString());
                            if (e.getDataSheet().getMainImage() != null) {
                                try {
                                    i.setImage(e.getDataSheet().getMainImage().toFileLocator().getUrl());
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        i.setName(e.getName());

                    }

                }

            });

        });

        return rs;
    }

    @Override
    public GetEventCheckListRS getEventCheckList(String token, int date, String activityId) throws Throwable {
        GetEventCheckListRS rs = new GetEventCheckListRS();

        rs.setStatusCode(200);
        rs.setMsg("");

        LocalDate d = LocalDate.parse("" + date, DateTimeFormatter.ofPattern("yyyyMMdd"));

        Helper.notransact(em -> {

            Excursion e = em.find(Excursion.class, Long.parseLong(activityId));

            e.getEvents().stream().filter(ev -> ev.isActive() && ev.getDate().equals(d)).forEach(ev -> {

                EventCheckItem i;
                rs.getEvent().add(i = new EventCheckItem());

                i.setActivityId(activityId);
                i.setId("" + ev.getId());
                i.setName("" + ev.getDate().format(DateTimeFormatter.ISO_DATE) + " " + ev.getShift().getName());

            });


        });

        return rs;
    }

    @Override
    public GetTicketCheckListRS getTicketCheckList(String token, String eventId) throws Throwable {
        GetTicketCheckListRS rs = new GetTicketCheckListRS();

        rs.setStatusCode(200);
        rs.setMsg("");

        Helper.notransact(em -> {

            ManagedEvent e = em.find(ManagedEvent.class, Long.parseLong(eventId));

            TicketListItem i;
            rs.setTicket(i = new TicketListItem());

            i.setEventId("" + e.getId());

            int totalPax = 0;
            int totalBookings = 0;
            int checkedPax = 0;
            int checkedBookings = 0;
            int remPax = 0;
            int remBookings = 0;
            for (TourBooking b : e.getBookings()) {

                TicketCheckItem t;
                i.getTicket().add(t = new TicketCheckItem());

                t.setComments(b.getSpecialRequests());
                t.setId("" + b.getId());
                t.setLeadname(b.getLeadName());
                t.setPax(b.getAdults() + b.getChildren());
                t.setQrcode(Strings.isNullOrEmpty(b.getQrCode())?"" + b.getId():b.getQrCode());

                totalPax += t.getPax();
                totalBookings ++;

                if (b.getCheckTime() != null) {
                    t.setChecked(true);
                    t.setCheckedDate(b.getCheckTime().getYear() * 10000 + b.getCheckTime().getMonthValue() * 100 + b.getCheckTime().getDayOfMonth());
                    t.setCheckedTime(b.getCheckTime().getHour() * 100 + b.getCheckTime().getMinute());
                    checkedPax += t.getPax();
                    checkedBookings ++;
                } else {
                    t.setChecked(false);
                    remPax += t.getPax();
                    remBookings ++;
                }
            }


            i.setCheckedPax(checkedPax);
            i.setCheckedTickets(checkedBookings);
            i.setRemainingPax(remPax);
            i.setRemainingTickets(remBookings);
            i.setTotalPax(totalPax);
            i.setTotalTickets(totalBookings);

        });

        return rs;
    }

    @Override
    public CheckTicketRS checkTicket(String token, String eventId, String qrcode) throws Throwable {
        CheckTicketRS rs = new CheckTicketRS();

        rs.setStatusCode(200);
        rs.setMsg("");

        try {

            Helper.transact(em -> {

                TourBooking b = null;
                ManagedEvent e = em.find(ManagedEvent.class, Long.parseLong(eventId));
                for (Booking bx : e.getBookings()) {
                    if (qrcode.equals(bx.getQrCode())) b = (TourBooking) bx;
                }

                if (b == null) {
                    rs.setStatusCode(404);
                    rs.setMsg("Booking " + qrcode + " not found");
                    rs.setValidationMessage(rs.getMsg());
                } else if (e == null) {
                    rs.setStatusCode(404);
                    rs.setMsg("Event " + eventId + " not found");
                    rs.setValidationMessage(rs.getMsg());
                } else if (!b.getManagedEvent().equals(e)) {
                    rs.setStatusCode(503);
                    rs.setMsg("Booking " + qrcode + " not valid for event " + eventId + "");
                    rs.setValidationMessage(rs.getMsg());
                } else if (b.getCheckTime() != null) {
                    rs.setStatusCode(503);
                    rs.setMsg("Booking " + qrcode + " already checked at " + b.getCheckTime().format(DateTimeFormatter.ISO_DATE_TIME) + "");
                    rs.setValidationMessage(rs.getMsg());
                } else {
                    TicketCheckItem t;
                    rs.setTicket(t = new TicketCheckItem());

                    b.setCheckTime(LocalDateTime.now());

                    t.setComments(b.getSpecialRequests());
                    t.setId("" + b.getId());
                    t.setLeadname(b.getLeadName());
                    t.setPax(b.getAdults() + b.getChildren());
                    t.setQrcode(Strings.isNullOrEmpty(b.getQrCode())?"" + b.getId():b.getQrCode());

                    rs.setValid(true);
                    rs.setValidationMessage("Ok");

                }

            });

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            rs.setStatusCode(500);
            rs.setMsg("ERROR " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage());
            rs.setValidationMessage(rs.getMsg());
        }

        return rs;
    }

    @Override
    public GetLoginRS login(String token, GetLoginRQ rq) throws Throwable {
        GetLoginRS rs = new GetLoginRS();

        rs.setStatusCode(200);
        rs.setMsg("");


        if (Strings.isNullOrEmpty(rq.getUser()) || Strings.isNullOrEmpty(rq.getPassword())) {
            rs.setStatusCode(500);
            rs.setMsg("User and password are required. Please fill");
        } else {
            Helper.transact(em -> {

                ERPUser u = em.find(ERPUser.class, rq.getUser().trim().toLowerCase());

                if (u == null) {
                    rs.setStatusCode(404);
                    rs.setMsg("User " + rq.getUser() + " not found");
                } else {
                    if (u.checkPassword(rq.getPassword())) {
                        rs.setAuthUser(u.getLogin());
                        rs.setLogged(true);
                        rs.setMessage("Hello " + u.getName());
                    } else {
                        rs.setStatusCode(503);
                        rs.setMsg("Wrong password for user " + rq.getUser() + ".");
                    }
                }


            });
        }

        return rs;
    }

    @Override
    public GetOfflineCheckListRS getOfflineCheckList(String token) throws Throwable {
        GetOfflineCheckListRS rs = new GetOfflineCheckListRS();

        rs.setStatusCode(200);
        rs.setMsg("");


        Helper.notransact(em -> {

            ((List<Excursion>) em.createQuery("select x from " + Excursion.class.getName() + " x order by x.name").getResultList()).forEach(e -> {

                if (e.isActive()) {

                    LocalDate d = LocalDate.now();

                    boolean happens = false;

                    for (int deltaDias = 0; deltaDias < 7; deltaDias++) {


                        for (ExcursionShift s : e.getShifts()) {
                            if ((s.getStart() == null || !d.isBefore(s.getStart()))
                                    && (s.getEnd() == null || !d.isAfter(s.getEnd()))
                                    && s.getWeekdays()[d.getDayOfWeek().getValue()]
                                    ) {
                                happens = true;
                                break;
                            }
                        }
                    }


                        if (happens) {

                            ActivityCheckItem i;
                            rs.getActivity().add(i = new ActivityCheckItem());

                            i.setActivityId("" + e.getId());
                            i.setDate(d.getYear() * 10000 + d.getMonthValue() * 100 + d.getDayOfMonth());
                            if (e.getDataSheet() != null) {
                                if (e.getDataSheet().getDescription() != null) i.setDescription(e.getDataSheet().getDescription().toString());
                                if (e.getDataSheet().getMainImage() != null) {
                                    try {
                                        i.setImage(e.getDataSheet().getMainImage().toFileLocator().getUrl());
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }
                            i.setName(e.getName());



                            for (ManagedEvent me : e.getEvents()) if (me.getDate().equals(d)) {

                                EventCheckItem ei;
                                rs.getEvent().add(ei = new EventCheckItem());

                                ei.setActivityId("" + e.getId());
                                ei.setId("" + me.getId());
                                ei.setName(me.getShift().getName());


                                TicketListItem tli;
                                rs.getTicketList().add(tli = new TicketListItem());

                                tli.setEventId("" + me.getId());

                                int totalPax = 0;
                                int totalBookings = 0;
                                int checkedPax = 0;
                                int checkedBookings = 0;
                                int remPax = 0;
                                int remBookings = 0;
                                for (TourBooking b : me.getBookings()) {

                                    TicketCheckItem t;
                                    tli.getTicket().add(t = new TicketCheckItem());

                                    t.setComments(b.getSpecialRequests());
                                    t.setId("" + b.getId());
                                    t.setLeadname(b.getLeadName());
                                    t.setPax(b.getAdults() + b.getChildren());
                                    t.setQrcode("" + b.getId());

                                    totalPax += t.getPax();
                                    totalBookings ++;

                                    if (b.getCheckTime() != null) {
                                        t.setChecked(true);
                                        t.setCheckedDate(b.getCheckTime().getYear() * 10000 + b.getCheckTime().getMonthValue() * 100 + b.getCheckTime().getDayOfMonth());
                                        t.setCheckedTime(b.getCheckTime().getHour() * 100 + b.getCheckTime().getMinute());
                                        checkedPax += t.getPax();
                                        checkedBookings ++;
                                    } else {
                                        t.setChecked(false);
                                        remPax += t.getPax();
                                        remBookings ++;
                                    }
                                }


                                tli.setCheckedPax(checkedPax);
                                tli.setCheckedTickets(checkedBookings);
                                tli.setRemainingPax(remPax);
                                tli.setRemainingTickets(remBookings);
                                tli.setTotalPax(totalPax);
                                tli.setTotalTickets(totalBookings);

                            }



                        }


                }

            });



        });

        return rs;
    }

    @Override
    public GeUpdatedTicketsRS updateTickets(String token, GetUpdatedTicketsRQ rq) throws Throwable {
        GeUpdatedTicketsRS rs = new GeUpdatedTicketsRS();

        rs.setStatusCode(200);
        rs.setMsg("");

        Helper.transact(em -> {


            rq.getTickets().forEach(t -> {

                TourBooking b = em.find(TourBooking.class, Long.parseLong(t.getId()));
                if (b.getCheckTime() == null) b.setCheckTime(LocalDateTime.parse("" + (t.getCheckedDate() * 10000 + t.getCheckedTime()), DateTimeFormatter.ofPattern("yyyyMMddHHmm")));

            });

        });

        return rs;
    }


    private int[] getCupo(Hotel h, LocalDate start) throws Throwable {

        int noches = 1200;
        LocalDate effectiveEnd = start.plusDays(noches);


        int[] cupo = new int[noches];

        for (Inventory inventory : h.getInventories()) {

            for (Room room : h.getRooms()) {

                for (HotelContract c : inventory.getContracts()) {
                    if (c.getTerms() != null) for (Allotment a : c.getTerms().getAllotment()) if (a.getRoom().equals(room.getType())) {
                        if (!a.getStart().isAfter(effectiveEnd) && !a.getEnd().isBefore(start)) {
                            int desde = new Long(DAYS.between(start, a.getStart())).intValue();
                            if (desde < 0) desde = 0;
                            int hasta = new Long(DAYS.between(start, a.getEnd())).intValue();
                            if (hasta > noches) hasta = noches;
                            for (int i = desde; i < hasta; i++) {
                                cupo[i] += a.getQuantity();
                            }
                        }
                    }
                }

                for (Inventory dependant : inventory.getDependantInventories()) {
                    for (HotelContract c : dependant.getContracts()) if (!inventory.getContracts().contains(c)) {
                        if (c.getTerms() != null) for (Allotment a : c.getTerms().getAllotment()) if (a.getRoom().equals(room.getType())) {
                            if (!a.getStart().isAfter(effectiveEnd) && !a.getEnd().isBefore(start)) {
                                int desde = new Long(DAYS.between(start, a.getStart())).intValue();
                                if (desde < 0) desde = 0;
                                int hasta = new Long(DAYS.between(start, a.getEnd())).intValue();
                                if (hasta > noches) hasta = noches;
                                for (int i = desde; i < hasta; i++) {
                                    cupo[i] += a.getQuantity();
                                }
                            }
                        }
                    }
                }

                for (InventoryOperation a : inventory.getOperations()) {
                    if (a.getRoom().equals(room.getType())) {
                        if (!a.getStart().isAfter(effectiveEnd) && !a.getEnd().isBefore(start)) {
                            int desde = new Long(DAYS.between(start, a.getStart())).intValue();
                            if (desde < 0) desde = 0;
                            int hasta = new Long(DAYS.between(start, a.getEnd())).intValue();
                            if (hasta > noches) hasta = noches;
                            for (int i = desde; i < hasta; i++) {
                                if (InventoryAction.ADD.equals(a.getAction())) cupo[i] += a.getQuantity();
                                else if (InventoryAction.SUBSTRACT.equals(a.getAction())) cupo[i] -= a.getQuantity();
                                if (InventoryAction.SET.equals(a.getAction())) cupo[i] = a.getQuantity();
                            }
                        }
                    }
                }

                for (HotelBookingLine a : inventory.getBookings()) {
                    if (a.getBooking().isActive() && a.isActive()) {
                        if (a.getRoom().getType().equals(room.getType())) {
                            if (!a.getStart().isAfter(effectiveEnd) && !a.getEnd().isBefore(start)) {
                                int desde = new Long(DAYS.between(start, a.getStart())).intValue();
                                if (desde < 0) desde = 0;
                                int hasta = new Long(DAYS.between(start, a.getEnd())).intValue();
                                if (hasta > noches) hasta = noches;
                                for (int i = desde; i < hasta; i++) {
                                    cupo[i] -= a.getRoomsBefore();
                                }
                            }
                        }
                    }
                }

            }

        }

        return cupo;
    }
}
