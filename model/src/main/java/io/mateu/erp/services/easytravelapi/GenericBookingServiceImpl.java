package io.mateu.erp.services.easytravelapi;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;
import io.mateu.erp.model.authentication.AuthToken;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.CancellationTerm;
import io.mateu.erp.model.booking.parts.GenericBooking;
import io.mateu.erp.model.invoicing.Charge;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.partners.Agency;
import io.mateu.erp.model.payments.BookingDueDate;
import io.mateu.erp.model.product.Tag;
import io.mateu.erp.model.product.Variant;
import io.mateu.erp.model.product.generic.GenericProduct;
import io.mateu.erp.model.world.Country;
import io.mateu.erp.model.world.Destination;
import io.mateu.erp.model.world.Resort;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.User;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;
import org.easytravelapi.GenericBookingService;
import org.easytravelapi.circuit.Label;
import org.easytravelapi.common.*;
import org.easytravelapi.generic.*;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class GenericBookingServiceImpl implements GenericBookingService {
    @Override
    public GetAvailableGenericsRS getAvailableGenerics(String token, String resorts, String language) throws Throwable {
        GetAvailableGenericsRS rs = new GetAvailableGenericsRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("Done");

        System.out.println("available activities. token = " + token);

        LocalDate formalizationDate = LocalDate.now();

        long t0 = System.currentTimeMillis();

        try {

            Helper.notransact(em -> {

                AuthToken t = em.find(AuthToken.class, token);

                Map<Long, Tag> labels = new HashMap<>();
                List<GenericProduct> excursions = new ArrayList<>();

                for (String s : Splitter.on(',')
                        .trimResults()
                        .omitEmptyStrings()
                        .split(resorts)) {
                    if (s.startsWith("cou")) {
                        em.find(Country.class, s.substring(4)).getDestinations().forEach(d -> d.getResorts().forEach(z -> z.getProducts().stream().filter(p -> p instanceof GenericProduct).forEach(p -> excursions.add((GenericProduct) p))));
                    } else if (s.startsWith("des")) {
                        em.find(Destination.class, Long.parseLong(s.substring(4))).getResorts().forEach(z -> z.getProducts().stream().filter(p -> p instanceof GenericProduct).forEach(p -> excursions.add((GenericProduct) p)));
                    } else if (s.startsWith("zon")) {
                        em.find(Resort.class, Long.parseLong(s.substring(4))).getProducts().stream().filter(p -> p instanceof GenericProduct).forEach(p -> excursions.add((GenericProduct) p));
                    } else if (s.startsWith("gen")) {
                        excursions.add(em.find(GenericProduct.class, Long.parseLong(s.substring(4))));
                    }
                };

                GenericBooking b = new GenericBooking();
                b.setAgency(t.getUser().getAgency());


                excursions.forEach(e -> {
                    {

                        b.setProduct(e);
                        b.setUnits(1);
                        b.setAdults(1);
                        try {
                            double min = 0;

                            for (Variant v : e.getVariants().size() > 0?e.getVariants():Lists.newArrayList((Variant) null)) {
                                b.setVariant(v);
                                b.price(em);

                                if (min == 0 || min > b.getTotalValue()) min = Helper.roundEuros(b.getTotalValue());

                            }

                            if (min != 0) {

                                AvailableGeneric a;
                                rs.getAvailableGenerics().add(a = new AvailableGeneric());

                                a.setGenericId("gen-" + e.getId());
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
                                bd.setRetailPrice(new Amount("EUR", min));

                                e.getDataSheet().getTags().forEach(x -> {
                                    labels.putIfAbsent(x.getId(), x);
                                    Label l;
                                    a.getLabels().add(l = new Label());
                                    l.setId("" + x.getId());
                                    l.setName(x.getName());
                                });


                                double v = min;

                                if (v > 0 && (rs.getMinPrice() == 0 || rs.getMinPrice() > v)) rs.setMinPrice(v);
                                if (v > 0 && (rs.getMaxPrice() == 0 || rs.getMaxPrice() < v)) rs.setMaxPrice(v);
                            }

                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
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
    public GetGenericRatesRS getGenericRates(String token, String productId, String language) throws Throwable {
        GetGenericRatesRS rs = new GetGenericRatesRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("Done");

        System.out.println("generic rates: productId = " + productId + ", language = " + language);


        Helper.notransact(em -> {

            AuthToken t = em.find(AuthToken.class, token);

            GenericProduct e = em.find(GenericProduct.class, Long.parseLong(productId.split("-")[1]));

            rs.setDateDependant(e.isDateDependant());
            rs.setDatesRangeDependant(e.isDatesRangeDependant());
            rs.setAdultsDependant(e.isAdultsDependant());
            rs.setChildrenDependant(e.isChildrenDependant());
            rs.setUnitsDependant(e.isUnitsDependant());
            rs.setVariantDependant(e.getVariants().size() > 0);




            GenericBooking b = new GenericBooking();
            b.setAgency(t.getUser().getAgency());
            b.setProduct(e);
            b.setUnits(1);
            b.setAdults(1);

            e.getVariants().forEach(v -> {

                b.setVariant(v);
                try {
                    b.price(em);

                    if (b.getTotalValue() != 0) {
                        org.easytravelapi.generic.GenericVariant xv;
                        rs.getVariants().add(xv = new org.easytravelapi.generic.GenericVariant());



                        BestDeal bd;
                        xv.setBestDeal(bd = new BestDeal());
                        bd.setRetailPrice(new Amount("EUR", Helper.roundEuros(b.getTotalValue())));
                        xv.setDescription(v.getDescription().get(language));
                        xv.setKey("gen-" + e.getId() + "-" + v.getId());
                        xv.setName(v.getName().get(language));
                        xv.setPricePer("--");
                    }

                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

            });

        });

        return rs;
    }

    @Override
    public CheckGenericRS check(String token, String productId, int adults, int children, int units, int start, int end, String language, String variant) throws Throwable {
        CheckGenericRS rs = new CheckGenericRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("Done");

        System.out.println("generic check: productId = " + productId + ", adults = " + adults + ", children = " + children + ", units = " + units + ", start = " + start + ", end = " + end + ", language = " + language);

        Helper.notransact(em -> {

            GenericProduct e = em.find(GenericProduct.class, Long.parseLong(productId.split("-")[1]));
            Variant v = null;
            if (productId.split("-").length > 2) {
                v = em.find(Variant.class, Long.parseLong(productId.split("-")[2]));
            }
            if (!Strings.isNullOrEmpty(variant)) {
                v = em.find(Variant.class, Long.parseLong(variant.contains("-")?variant.substring(variant.lastIndexOf("-") + 1):variant));
            }
            String key = getKey(token, "" + e.getId(), v != null?"" + v.getId():null, start, end, language, units, adults, children);

            GenericBooking b = buildBookingFromKey(em, key);

            b.price(em);

            rs.setAvailable(true);
            rs.setKey(key);
            rs.setValue(new Amount("EUR", Helper.roundEuros(b.getTotalValue())));

            if (b.getTotalValue() != 0) {
                BestDeal bd;
                rs.setValue(new Amount(b.getCurrency().getIsoCode(), b.getTotalValue()));
            }
        });

        return rs;
    }

    public String getKey(String token, String productId, String variantId, int start, int end, String language, int units, int adults, int children) throws IOException {
        return Base64.getEncoder().encodeToString(buildKey(token, productId, variantId, start, end, language, units, adults, children).getBytes());
    }

    private String buildKey(String token, String productId, String variantId, int start, int end, String language, int units, int adults, int children) throws IOException {
        String s = "";

        Map<String, Object> data = new HashMap<>();

        data.put("token", token);
        data.put("product", productId);
        data.put("variant", variantId);
        data.put("start", start);
        data.put("end", end);
        data.put("language", language);
        data.put("units", units);
        data.put("adults", adults);
        data.put("children", children);

        s = Helper.toJson(data);

        return s;
    }


    @Override
    public GetGenericPriceDetailsRS getGenericPriceDetails(String token, String key, String language, String supplements, String coupon) throws Throwable {
        GetGenericPriceDetailsRS rs = new GetGenericPriceDetailsRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("Price details");

        System.out.println("generic price detais: key = " + key + ", language = " + language + ", supplements = " + supplements + ", coupon = " + coupon);


        try {
            Helper.notransact(em -> {

                GenericBooking b = buildBookingFromKey(em, key);

                // todo: meter todo esto en el priceFromServices de TransferBooking

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


                rs.setKey(key);
                rs.setTotal(new BestDeal());
                rs.getTotal().setRetailPrice(new Amount(b.getCurrency().getIsoCode(), b.getTotalValue()));

            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            rs.setStatusCode(500);
            rs.setMsg(throwable.getClass().getSimpleName() + ": " + throwable.getMessage());
        }


        return rs;
    }


    private GenericBooking buildBookingFromKey(EntityManager em, String key) throws IOException {
        Map<String, Object> data = Helper.fromJson(new String((Base64.getDecoder().decode(key))));

        System.out.println(Helper.toJson(data));

        AuthToken t = em.find(AuthToken.class, data.get("token"));

        GenericBooking b = new GenericBooking();
        b.setAudit(new Audit(t.getUser()));
        b.setAgency(t.getUser().getAgency());
        b.setCurrency(b.getAgency().getCurrency());
        b.setPos(t.getPos());
        b.setTariff(b.getPos().getTariff());

        b.setProduct(em.find(GenericProduct.class, new Long(String.valueOf(data.get("product")))));
        if (data.containsKey("variant") && !Strings.isNullOrEmpty((String) data.get("variant"))) {
            b.setVariant(em.find(Variant.class, new Long(String.valueOf(data.get("variant")))));
        }

        b.setUnits((Integer) data.get("units"));
        b.setAdults((Integer) data.get("adults"));
        b.setChildren((Integer) data.get("children"));

        //Price p = em.find(Price.class, new Long(String.valueOf(data.get("priceId"))));


        int date = (Integer) data.get("start");
        LocalDate fecha = date == 0?LocalDate.now():LocalDate.of((date - date % 10000) / 10000, ((date - date % 100) / 100) % 100, date % 100);

        b.setStart(fecha);
        date = (Integer) data.get("end");
        fecha = date == 0?LocalDate.now():LocalDate.of((date - date % 10000) / 10000, ((date - date % 100) / 100) % 100, date % 100);
        b.setEnd(fecha);

        b.setOffice(b.getProduct().getOffice());


        return b;
    }



    @Override
    public BookGenericRS bookGeneric(String token, BookGenericRQ rq) throws Throwable {
        System.out.println("rq=" + rq);

        BookGenericRS rs = new BookGenericRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("Booking confirmed ok");

        io.mateu.erp.model.booking.Booking[] bx = new io.mateu.erp.model.booking.Booking[1];

        try {
            Helper.transact(new JPATransaction() {
                @Override
                public void run(EntityManager em) throws Throwable {

                    GenericBooking b = buildBookingFromKey(em, rq.getKey());

                    b.setAgencyReference(rq.getBookingReference());
                    if (b.getAgencyReference() == null) b.setAgencyReference("");
                    b.setSpecialRequests(rq.getCommentsToProvider());
                    b.setEmail(rq.getEmail());
                    b.setLeadName(rq.getLeadName());
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
            Helper.notransact(em -> {
                io.mateu.erp.model.booking.Booking bz = em.find(Booking.class, bx[0].getId());
                if (bz.getTPVTransactions().size() > 0) rs.setPaymentUrl(bz.getTPVTransactions().get(0).getBoton(em));
                else rs.setPaymentUrl("");
            });
            //rs.setAvailableServices(""); // todo: añadir servicios adicionales que podemos reservar

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            rs.setStatusCode(500);
            rs.setMsg(throwable.getClass().getSimpleName() + ": " + throwable.getMessage());
        }



        return rs;
    }

    @Override
    public GetAvailableGenericsRS getFilteredGeneric(String token, String destination, String language, String labels, double minPrice, double maxPrice) throws Throwable {
        GetAvailableGenericsRS rs = getAvailableGenerics(token, destination, language);


        if (!Strings.isNullOrEmpty(labels)) {
            List<String> catIds = Lists.newArrayList(labels.split(","));
            rs.setAvailableGenerics(rs.getAvailableGenerics().stream().filter(h -> {
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
            rs.setAvailableGenerics(rs.getAvailableGenerics().stream().filter(h -> h.getBestDeal() != null && h.getBestDeal().getRetailPrice() != null && h.getBestDeal().getRetailPrice().getValue() >= minPrice).collect(Collectors.toList()));
        }
        if (maxPrice != 0) {
            rs.setAvailableGenerics(rs.getAvailableGenerics().stream().filter(h -> h.getBestDeal() != null && h.getBestDeal().getRetailPrice() != null && h.getBestDeal().getRetailPrice().getValue() <= maxPrice).collect(Collectors.toList()));
        }
        return rs;

    }
}
