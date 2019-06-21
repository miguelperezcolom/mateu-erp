package io.mateu.erp.services.easytravelapi;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;
import io.mateu.erp.model.authentication.AuthToken;
import io.mateu.erp.model.booking.CancellationTerm;
import io.mateu.erp.model.booking.parts.CircuitBooking;
import io.mateu.erp.model.invoicing.Charge;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.partners.Agency;
import io.mateu.erp.model.payments.BookingDueDate;
import io.mateu.erp.model.product.Tag;
import io.mateu.erp.model.product.Variant;
import io.mateu.erp.model.product.tour.*;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.User;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;
import org.easytravelapi.CircuitBookingService;
import org.easytravelapi.activity.*;
import org.easytravelapi.circuit.*;
import org.easytravelapi.common.*;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class CircuitBookingServiceImpl implements CircuitBookingService {
    @Override
    public GetAvailableCircuitsRS getAvailableCircuits(String token, String language) throws Throwable {
        GetAvailableCircuitsRS rs = new GetAvailableCircuitsRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("Done");

        System.out.println("available circuits. token = " + token);

        long t0 = System.currentTimeMillis();

        try {

            Helper.notransact(em -> {


                AuthToken t = em.find(AuthToken.class, token);

                Map<Long, Tag> labels = new HashMap<>();

                List<Circuit> excursions = em.createQuery("select x from " + Circuit.class.getName() + " x order by x.name").getResultList();


                excursions.forEach(e -> {
                    {

                        CircuitBooking b = new CircuitBooking();
                        b.setAgency(t.getUser().getAgency());

                        b.setCircuit(e);
                        b.setStart(LocalDate.now());
                        b.setEnd(LocalDate.now());
                        b.setAdults(1);


                        double min = 0;

                        for (Variant var : e.getVariants().size() > 0?e.getVariants():Lists.newArrayList((Variant) null)) {
                            b.setVariant(var);
                            b.priceServices(em, new ArrayList<>());

                            if (min == 0 || min > b.getTotalValue()) min = Helper.roundEuros(b.getTotalValue());

                        }

                        if (min != 0) {

                            AvailableCircuit a;
                            rs.getAvailableCircuits().add(a = new AvailableCircuit());

                            a.setCircuitId("cir-" + e.getId());
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

                                e.getDataSheet().getTags().forEach(x -> {
                                    labels.putIfAbsent(x.getId(), x);
                                });

                            }


                            e.getDataSheet().getTags().forEach(x -> {
                                labels.putIfAbsent(x.getId(), x);
                                Label l;
                                a.getLabels().add(l = new Label());
                                l.setId("" + x.getId());
                                l.setName(x.getName());
                            });


                            BestDeal bd;
                            a.setBestDeal(bd = new BestDeal());
                            double v = min;
                            bd.setRetailPrice(new Amount("EUR", v));

                            if (v > 0 && (rs.getMinPrice() == 0 || rs.getMinPrice() > v)) rs.setMinPrice(v);
                            if (v > 0 && (rs.getMaxPrice() == 0 || rs.getMaxPrice() < v)) rs.setMaxPrice(v);

                        }

                    }

                });


                labels.values().forEach(pl -> {
                    Label l;
                    rs.getLabels().add(l = new Label());
                    l.setId("" + pl.getId());
                    l.setName(pl.getName());
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
    public GetCircuitRatesRS getCircuitRates(String token, String key, int date, String language) throws Throwable {
        GetCircuitRatesRS rs = new GetCircuitRatesRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("Done");

        System.out.println("activity rates. token = " + token);


        long t0 = System.currentTimeMillis();

        try {

            Helper.notransact(em -> {

                AuthToken t = em.find(AuthToken.class, token);

                Circuit e = em.find(Circuit.class, Long.parseLong(key.split("-")[1]));

                CircuitBooking b = new CircuitBooking();
                b.setAgency(t.getUser().getAgency());

                b.setCircuit(e);
                b.setStart(io.mateu.erp.dispo.Helper.toDate(date));
                b.setEnd(io.mateu.erp.dispo.Helper.toDate(date));
                b.setAdults(1);


                rs.setVariants(new ArrayList<>());
                e.getVariants().forEach(v -> {

                    b.setVariant(v);
                    b.priceServices(em, new ArrayList<>());

                    ActivityVariant av;
                    rs.getVariants().add(av = new ActivityVariant());
                    av.setKey("" + v.getId());
                    if (v.getName() != null) av.setName(v.getName().get(language));
                    if (v.getDescription() != null) av.setDescription(v.getDescription().get(language));
                    av.setBestDeal(new BestDeal());
                    av.getBestDeal().setRetailPrice(new Amount("EUR", Helper.roundEuros(b.getTotalValue())));

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
    public CheckCircuitRS check(String token, String key, int date, String language, int adults, int children, String variant) throws Throwable {
        CheckCircuitRS rs = new CheckCircuitRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("Done");

        System.out.println("activity rates. token = " + token);


        long t0 = System.currentTimeMillis();


        Helper.notransact(em -> {

            AuthToken t = em.find(AuthToken.class, token);

            Circuit e = em.find(Circuit.class, Long.parseLong(key.split("-")[1]));

            CircuitBooking b = new CircuitBooking();
            b.setAgency(t.getUser().getAgency());

            b.setCircuit(e);
            b.setStart(io.mateu.erp.dispo.Helper.toDate(date));
            b.setEnd(io.mateu.erp.dispo.Helper.toDate(date));
            b.setAdults(adults);
            b.setChildren(children);

            b.setVariant(em.find(Variant.class, Long.parseLong(variant)));
            b.priceServices(em, new ArrayList<>());

            rs.setAvailable(b.getTotalValue() > 0);
            rs.setKey(getKey(token, "" + e.getId(), date, language, adults, children, variant));
            rs.setValue(new Amount("EUR", Helper.roundEuros(b.getTotalValue())));


        });

        return rs;
    }


    public String getKey(String token, String key, int date, String language, int adults, int children, String variant) throws IOException {
        return Base64.getEncoder().encodeToString(buildKey(token, key, date, language, adults, children, variant).getBytes());
    }

    private String buildKey(String token, String key, int date, String language, int adults, int children, String variant) throws IOException {
        String s = "";

        Map<String, Object> data = new HashMap<>();

        data.put("token", token);
        data.put("activity", key);
        data.put("date", date);
        data.put("language", language);
        data.put("adults", adults);
        data.put("children", children);
        data.put("variant", variant);

        s = Helper.toJson(data);

        return s;
    }



    @Override
    public GetCircuitPriceDetailsRS getCircuitPriceDetails(String token, String key, String language, String supplements, String coupon) throws Throwable {
        GetCircuitPriceDetailsRS rs = new GetCircuitPriceDetailsRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("Price details");


        try {
            Helper.notransact(em -> {

                CircuitBooking b = buildBookingFromKey(em, key);

                b.price(em);

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
                    pl.setRetailPrice(new Amount(l.getCurrency().getIsoCode(), l.getTotal()));
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

    private CircuitBooking buildBookingFromKey(EntityManager em, String key) throws IOException {
        Map<String, Object> data = Helper.fromJson(new String((Base64.getDecoder().decode(key))));

        System.out.println(Helper.toJson(data));

        AuthToken t = em.find(AuthToken.class, data.get("token"));

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

        CircuitBooking b = new CircuitBooking();
        b.setAudit(new Audit(t.getUser()));
        b.setAgency(t.getUser().getAgency());
        b.setCurrency(b.getAgency().getCurrency());
        b.setPos(t.getPos());
        b.setTariff(b.getPos().getTariff());

        b.setCircuit(em.find(Circuit.class, new Long(String.valueOf(data.get("activity")))));
        b.setAdults((Integer) data.get("adults"));
        b.setChildren((Integer) data.get("children"));

        //Price p = em.find(Price.class, new Long(String.valueOf(data.get("priceId"))));

        if (data.get("variant") != null && !"x".equals(data.get("variant")) && !"null".equals(data.get("variant"))) b.setVariant(em.find(Variant.class, new Long(String.valueOf(data.get("variant")))));
        //b.setLanguage(em.find(Excursion.class, new Long(String.valueOf(data.get("activity"))))); //todo: añadir idioma excursión
        //b.setPickup(em.find(Excursion.class, new Long(String.valueOf(data.get("activity"))))); //todo: añadir pickup a la excursión


        int date = (Integer) data.get("date");
        LocalDate fecha = LocalDate.of((date - date % 10000) / 10000, ((date - date % 100) / 100) % 100, date % 100);

        b.setStart(fecha);
        b.setEnd(fecha);



        return b;
    }


    @Override
    public BookCircuitRS bookCircuit(String token, BookCircuitRQ rq) throws Throwable {
        BookCircuitRS rs = new BookCircuitRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("Booking confirmed ok");

        io.mateu.erp.model.booking.Booking[] bx = new io.mateu.erp.model.booking.Booking[1];

        try {
            Helper.transact(new JPATransaction() {
                @Override
                public void run(EntityManager em) throws Throwable {

                    CircuitBooking b = buildBookingFromKey(em, rq.getKey());

                    b.setAgencyReference(rq.getBookingReference());
                    if (b.getAgencyReference() == null) b.setAgencyReference("");
                    b.setSpecialRequests(rq.getCommentsToProvider());
                    b.setEmail(rq.getEmail());
                    b.setLeadName(rq.getLeadName());
                    b.setPrivateComments(rq.getPrivateComments());
                    b.setPos(em.find(AuthToken.class, token).getPos());
                    b.setTelephone(rq.getPhoneNumber());
                    b.setConfirmed(b.getAgency() != null && !b.getAgency().getFinancialAgent().isDirectSale());
                    b.setConfirmNow(false);

                    b.setExpiryDate(LocalDateTime.now().plusHours(2)); // por defecto caduca a las 2 horas


                    em.persist(b);

                    bx[0] = b;
                }
            });

            io.mateu.erp.model.booking.Booking b = bx[0];
            rs.setBookingId("" + bx[0].getId());
            if (b.getTPVTransactions().size() > 0) Helper.notransact(em -> {
                rs.setPaymentUrl(b.getTPVTransactions().get(0).getBoton(em));
            });
            else rs.setPaymentUrl("");
            //rs.setAvailableServices(""); // todo: añadir servicios adicionales que podemos reservar

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            rs.setStatusCode(500);
            rs.setMsg(throwable.getClass().getSimpleName() + ": " + throwable.getMessage());
        }

        return rs;
    }

    @Override
    public GetAvailableCircuitsRS getFilteredCircuits(String token, String labels, String language, double minPrice, double maxPrice) throws Throwable {
        GetAvailableCircuitsRS rs = getAvailableCircuits(token, language);

        if (!Strings.isNullOrEmpty(labels)) {
            List<String> catIds = Lists.newArrayList(labels.split(","));
            rs.setAvailableCircuits(rs.getAvailableCircuits().stream().filter(h -> {
                boolean ok = false;
                for (Label l : h.getLabels()) {
                    if (catIds.contains(l.getId())) {
                        ok = true;
                        break;
                    }
                }
                return ok;
            }).collect(Collectors.toList()));
        }
        if (minPrice != 0) {
            rs.setAvailableCircuits(rs.getAvailableCircuits().stream().filter(h -> h.getBestDeal() != null && h.getBestDeal().getRetailPrice() != null && h.getBestDeal().getRetailPrice().getValue() >= minPrice).collect(Collectors.toList()));
        }
        if (maxPrice != 0) {
            rs.setAvailableCircuits(rs.getAvailableCircuits().stream().filter(h -> h.getBestDeal() != null && h.getBestDeal().getRetailPrice() != null && h.getBestDeal().getRetailPrice().getValue() <= maxPrice).collect(Collectors.toList()));
        }
        return rs;
    }
}
