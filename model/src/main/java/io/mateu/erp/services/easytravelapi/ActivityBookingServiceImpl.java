package io.mateu.erp.services.easytravelapi;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;
import io.mateu.erp.model.authentication.AuthToken;
import io.mateu.erp.model.booking.CancellationTerm;
import io.mateu.erp.model.booking.parts.ExcursionBooking;
import io.mateu.erp.model.invoicing.Charge;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.payments.BookingDueDate;
import io.mateu.erp.model.product.Variant;
import io.mateu.erp.model.product.tour.*;
import io.mateu.erp.model.world.Country;
import io.mateu.erp.model.world.Destination;
import io.mateu.erp.model.world.Resort;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.User;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;
import org.easytravelapi.ActivityBookingService;
import org.easytravelapi.activity.*;
import org.easytravelapi.common.*;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by miguel on 27/7/17.
 */
public class ActivityBookingServiceImpl implements ActivityBookingService {


    @Override
    public GetAvailableActivitiesRS getAvailableActivities(String token, int start, String resorts, String language) {
        GetAvailableActivitiesRS rs = new GetAvailableActivitiesRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("Done");

        System.out.println("available activities. token = " + token);

        LocalDate formalizationDate = LocalDate.now();

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

        try {

            Helper.notransact(em -> {

                List<Excursion> excursions = new ArrayList<>();

                for (String s : Splitter.on(',')
                        .trimResults()
                        .omitEmptyStrings()
                        .split(resorts)) {
                    if (s.startsWith("cou")) {
                        em.find(Country.class, s.substring(4)).getDestinations().forEach(d -> d.getResorts().forEach(z -> z.getProducts().stream().filter(p -> p instanceof Excursion).forEach(p -> excursions.add((Excursion) p))));
                    } else if (s.startsWith("des")) {
                        em.find(Destination.class, Long.parseLong(s.substring(4))).getResorts().forEach(z -> z.getProducts().stream().filter(p -> p instanceof Excursion).forEach(p -> excursions.add((Excursion) p)));
                    } else if (s.startsWith("zon")) {
                        em.find(Resort.class, Long.parseLong(s.substring(4))).getProducts().stream().filter(p -> p instanceof Excursion).forEach(p -> excursions.add((Excursion) p));
                    } else if (s.startsWith("exc")) {
                        excursions.add(em.find(Excursion.class, Long.parseLong(s.substring(4))));
                    }
                };


                excursions.forEach(e -> {
                    {
                        AvailableActivity a;
                        rs.getAvailableActivities().add(a = new AvailableActivity());

                        a.setActivityId("exc-" + e.getId());
                        a.setName(e.getName());
                        if (e.getDataSheet() != null) {
                            if (e.getDataSheet().getDescription() != null) a.setDescription(e.getDataSheet().getDescription().get(language));
                            if (e.getDataSheet().getMainImage() != null) {
                                try {
                                    a.setImage(e.getDataSheet().getMainImage().toFileLocator().getUrl());
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        BestDeal bd;
                        a.setBestDeal(bd = new BestDeal());
                        bd.setRetailPrice(new Amount("EUR", 200.34));

                    }

                });

            });

        } catch (Throwable throwable) {
            rs.setStatusCode(500);
            rs.setMsg("" + throwable.getClass().getSimpleName() + ": " + throwable.getMessage());
            throwable.printStackTrace();
        }


        return rs;
    }

    @Override
    public GetActivityRatesRS getActivityRates(String token, String activityId, int date, String language) throws Throwable {

        System.out.println("activity rates (" + activityId + ", " + date + ", " + language + ")");

        GetActivityRatesRS rs = new GetActivityRatesRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("Done");

        System.out.println("activity rates. token = " + token);


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

        try {

            Helper.notransact(em -> {

                Excursion e = em.find(Excursion.class, Long.parseLong(activityId.split("-")[1]));

                rs.setVariants(new ArrayList<>());
                e.getVariants().forEach(v -> {
                    ActivityVariant av;
                    rs.getVariants().add(av = new ActivityVariant());
                    av.setKey("" + v.getId());
                    if (v.getName() != null) av.setName(v.getName().get(language));
                    if (v.getDescription() != null) av.setDescription(v.getDescription().get(language));
                    av.setBestDeal(new BestDeal());
                    av.getBestDeal().setRetailPrice(new Amount("EUR", 200.34));

                });

                LocalDate d = LocalDate.of((date - date % 10000) / 10000, ((date - date % 100) / 100) % 100, date % 100);


                System.out.println("d = " + d);
                System.out.println("turnos " + e.getName() + " = " + e.getShifts().size());

                rs.setShifts(new ArrayList<>());
                e.getShifts().forEach(x -> {

                    if ((x.getStart() == null || !x.getStart().isBefore(d)) && (x.getEnd() == null || !x.getEnd().isAfter(d)) && (x.getWeekdays() == null || x.getWeekdays()[d.getDayOfWeek().getValue() - 1])) {

                        System.out.println("turno " + x.getId() + " es válido");

                        ActivityShift s;
                        rs.getShifts().add(s = new ActivityShift());
                        s.setId("" + x.getId());
                        s.setName(x.getName());

                        if (x.getLanguages() != null) {
                            if (x.getLanguages().contains("es")) {
                                ActivityLanguage l;
                                s.getLanguages().add(l = new ActivityLanguage());
                                l.setId("es");
                                l.setName("Español");
                            }
                            if (x.getLanguages().contains("en")) {
                                ActivityLanguage l;
                                s.getLanguages().add(l = new ActivityLanguage());
                                l.setId("en");
                                l.setName("English");
                            }
                            if (x.getLanguages().contains("it")) {
                                ActivityLanguage l;
                                s.getLanguages().add(l = new ActivityLanguage());
                                l.setId("it");
                                l.setName("Italiano");
                            }
                            if (x.getLanguages().contains("de")) {
                                ActivityLanguage l;
                                s.getLanguages().add(l = new ActivityLanguage());
                                l.setId("de");
                                l.setName("Deutsch");
                            }
                        };

                        if (s.getLanguages().size() == 0) {
                            ActivityLanguage l;
                            s.getLanguages().add(l = new ActivityLanguage());
                            l.setId("es");
                            l.setName("Español");
                        }

                        s.setPickups(new ArrayList<>());
                        x.getPickupTimes().forEach(z -> {
                            ActivityPickupPoint p;
                            s.getPickups().add(p = new ActivityPickupPoint());
                            p.setId("" + z.getId());
                            if (z.getPoint() != null) p.setName("" + z.getPoint().getName() + " at " + z.getTime());
                        });

                        if (s.getPickups().size() == 0) {
                            ActivityPickupPoint p;
                            s.getPickups().add(p = new ActivityPickupPoint());
                            p.setId("x");
                            p.setName("Sin recogida");
                        }

                    } else {
                        System.out.println("turno " + x.getId() + " NO es válido");
                    }

                });



            });


        } catch (Throwable e) {
            rs.setStatusCode(500);
            rs.setMsg("" + e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
        }

        return rs;
    }

    @Override
    public CheckActivityRS check(String token, String key, int date, String language, int adults, int children, String variant, String shift, String pickup, String activityLanguage) throws Throwable {
        CheckActivityRS rs = new CheckActivityRS();
        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("Done");

        Helper.notransact(em -> {

            Excursion e = em.find(Excursion.class, Long.parseLong(key.split("-")[1]));

            rs.setAvailable(true);
            rs.setKey(getKey(token, "" + e.getId(), date, language, adults, children, variant, shift, pickup, activityLanguage));
            rs.setValue(new Amount("EUR", Helper.roundEuros(200.34 * (adults + children))));


        });


        return rs;
    }

    public String getKey(String token, String key, int date, String language, int adults, int children, String variant, String shift, String pickup, String activityLanguage) throws IOException {
        return Base64.getEncoder().encodeToString(buildKey(token, key, date, language, adults, children, variant, shift, pickup, activityLanguage).getBytes());
    }

    private String buildKey(String token, String key, int date, String language, int adults, int children, String variant, String shift, String pickup, String activityLanguage) throws IOException {
        String s = "";

        Map<String, Object> data = new HashMap<>();

        data.put("token", token);
        data.put("activity", key);
        data.put("date", date);
        data.put("language", language);
        data.put("adults", adults);
        data.put("children", children);
        data.put("variant", variant);
        data.put("shift", shift);
        data.put("pickup", pickup);
        data.put("activityLanguage", activityLanguage);

        s = Helper.toJson(data);

        return s;
    }


    @Override
    public GetActivityPriceDetailsRS getActivityPriceDetails(String token, String key, String language, String supplements, String coupon) throws Throwable {
        GetActivityPriceDetailsRS rs = new GetActivityPriceDetailsRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("Price details");


        try {
            Helper.notransact(em -> {

                ExcursionBooking b = buildBookingFromKey(em, key);

                // todo: meter todo esto en el priceFromServices de TransferBooking

                //b.price(em);

                List<Contract> contratos = em.createQuery("select s from " + Contract.class.getName() + " s").getResultList();

                int encontrados = 0;

                for (Contract c : contratos) {

                    boolean contratoOk = true;

                    contratoOk = contratoOk && !c.getValidFrom().isAfter(b.getStart());
                    contratoOk = contratoOk && !c.getValidTo().isBefore(b.getEnd());

                    //todo: comprobar file window y demás condiciones

                    if (contratoOk) {

                        for (TourPrice p : c.getPrices()) {

                            boolean precioOk = true;

                            //todo: aplicar políticas precios correctamente
                            /*

                            boolean precioOk = p.getOrigin().getPoints().contains(b.getOrigin()) || p.getOrigin().getResorts().contains(b.getOrigin().getResort());

                            precioOk = precioOk && (p.getDestination().getPoints().contains(b.getDestination()) || p.getDestination().getResorts().contains(b.getDestination().getResort()));

                            precioOk = precioOk && p.getVehicle().getMinPax() <= b.getAdults() && p.getVehicle().getMaxPax() >= b.getAdults();

                            */

                            if (precioOk) {

                                double valor = Helper.roundEuros(p.getPricePerAdult() * b.getAdults() + p.getPricePerChild() * b.getChildren());
                                if (valor != 0) {
                                    rs.setTotal(new BestDeal());
                                    rs.getTotal().setRetailPrice(new Amount(b.getCurrency().getIsoCode(), valor));
                                }
                                //todo: añadir contrato a la reserva
                                //b.setContract(c);

                            }
                        }

                    }

                }

                b.createCharges(em);
                b.summarize(em);

                if (true) {
                    Remark r;
                    rs.getRemarks().add(r = new Remark());
                    r.setType("INFO");
                    r.setText("This is a test booking. IT IS NOT VALID.");
                }

                int pos = 1;
                for (Charge l : b.getCharges()) {
                    PriceLine pl;
                    rs.getPriceLines().add(pl = new PriceLine());
                    pl.setId("" + pos++);
                    pl.setType(l.getBillingConcept().getCode());
                    pl.setDescription(l.getText());
                    pl.setRetailPrice(new Amount(l.getTotal().getCurrency().getIsoCode(), l.getTotal().getValue()));
                }

                for (BookingDueDate dd : b.getDueDates()) {
                    if (!dd.isPaid()) {
                        PaymentLine pl;
                        rs.getPaymentLines().add(pl = new PaymentLine());
                        pl.setDate(Integer.parseInt(dd.getDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"))));
                        pl.setPaymentMethod("WEB");
                        pl.setAmount(new Amount(dd.getCurrency().getIsoCode(), dd.getAmount()));
                    }
                }

                for (CancellationTerm t : b.getCancellationTerms()) {
                    CancellationCost cc;
                    rs.getCancellationCosts().add(cc = new CancellationCost());
                    cc.setRetail(new Amount(b.getAgency().getCurrency().getIsoCode(), t.getAmount()));
                    cc.setGMTtime(t.getDate().toString());
                }

            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            rs.setStatusCode(500);
            rs.setMsg(throwable.getClass().getSimpleName() + ": " + throwable.getMessage());
        }

        return rs;
    }

    private ExcursionBooking buildBookingFromKey(EntityManager em, String key) throws IOException {
        Map<String, Object> data = Helper.fromJson(new String((Base64.getDecoder().decode(key))));

        System.out.println(Helper.toJson(data));

        long idAgencia = 0;
        long idHotel = 0;
        String login = "";
        try {
            Credenciales creds = new Credenciales(new String(BaseEncoding.base64().decode((String) data.get("token"))));
            if (!Strings.isNullOrEmpty(creds.getAgentId())) idAgencia = Long.parseLong(creds.getAgentId());
            if (!Strings.isNullOrEmpty(creds.getHotelId())) idHotel = Long.parseLong(creds.getHotelId());
            //rq.setLanguage(creds.getLan());
            login = creds.getLogin();
            //rq.setPassword(creds.getPass());
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        data.put("token", token);
        data.put("activity", key);
        data.put("language", language);
        data.put("adults", adults);
        data.put("children", children);
        data.put("excursioVariant", excursioVariant);
        data.put("shift", shift);
        data.put("pickup", pickup);
        data.put("activityLanguage", activityLanguage);
         */

        ExcursionBooking b = new ExcursionBooking();
        User user = em.find(User.class, login);
        b.setAudit(new Audit(user));
        b.setAgency(em.find(Partner.class, idAgencia));
        b.setCurrency(b.getAgency().getCurrency());

        b.setExcursion(em.find(Excursion.class, new Long(String.valueOf(data.get("activity")))));
        b.setAdults((Integer) data.get("adults"));
        b.setChildren((Integer) data.get("children"));

        //Price p = em.find(Price.class, new Long(String.valueOf(data.get("priceId"))));

        if (data.get("variant") != null && !"x".equals(data.get("variant")) && !"null".equals(data.get("variant"))) b.setVariant(em.find(Variant.class, new Long(String.valueOf(data.get("variant")))));
        if (data.get("shift") != null && !"x".equals(data.get("shift")) && !"null".equals(data.get("shift"))) b.setShift(em.find(TourShift.class, new Long(String.valueOf(data.get("shift")))));
        //b.setLanguage(em.find(Excursion.class, new Long(String.valueOf(data.get("activity"))))); //todo: añadir idioma excursión
        //b.setPickup(em.find(Excursion.class, new Long(String.valueOf(data.get("activity"))))); //todo: añadir pickup a la excursión


        int date = (Integer) data.get("date");
        LocalDate fecha = LocalDate.of((date - date % 10000) / 10000, ((date - date % 100) / 100) % 100, date % 100);

        b.setStart(fecha);
        b.setEnd(fecha);



        return b;
    }

    @Override
    public BookActivityRS bookActivity(String token, BookActivityRQ rq) {
        System.out.println("rq=" + rq);

        BookActivityRS rs = new BookActivityRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("Booking confirmed ok");

        ExcursionBooking[] bs = {null};

        try {
            Helper.transact(new JPATransaction() {
                @Override
                public void run(EntityManager em) throws Throwable {

                    ExcursionBooking b = buildBookingFromKey(em, rq.getKey());

                    b.setConfirmed(true);
                    b.setAgencyReference(rq.getBookingReference());
                    if (b.getAgencyReference() == null) b.setAgencyReference("");
                    b.setSpecialRequests(rq.getCommentsToProvider());
                    b.setEmail(rq.getEmail());
                    b.setLeadName(rq.getLeadName());
                    b.setPrivateComments(rq.getPrivateComments());
                    b.setPos(em.find(AuthToken.class, token).getPos());
                    b.setTelephone(rq.getPhoneNumber());

                    b.setExpiryDate(LocalDateTime.now().plusHours(2)); // por defecto caduca a las 2 horas


                    em.persist(b);

                    bs[0] = b;

                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            rs.setStatusCode(500);
            rs.setMsg(throwable.getClass().getSimpleName() + ": " + throwable.getMessage());
        }


        rs.setBookingId("" + bs[0].getId());



        return rs;
    }

    @Override
    public GetAvailableActivitiesRS getFilteredActivities(String token, int start, String resourceId, String language, double minPrice, double maxPrice) throws Throwable {
        GetAvailableActivitiesRS rs = getAvailableActivities(token, start, resourceId, language);

        if (minPrice != 0) {
            rs.setAvailableActivities(rs.getAvailableActivities().stream().filter(h -> h.getBestDeal() != null && h.getBestDeal().getRetailPrice() != null && h.getBestDeal().getRetailPrice().getValue() >= minPrice).collect(Collectors.toList()));
        }
        if (maxPrice != 0) {
            rs.setAvailableActivities(rs.getAvailableActivities().stream().filter(h -> h.getBestDeal() != null && h.getBestDeal().getRetailPrice() != null && h.getBestDeal().getRetailPrice().getValue() <= maxPrice).collect(Collectors.toList()));
        }
        return rs;
    }


}
