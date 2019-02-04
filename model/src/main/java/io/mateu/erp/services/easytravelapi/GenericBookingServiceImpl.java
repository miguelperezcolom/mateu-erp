package io.mateu.erp.services.easytravelapi;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;
import io.mateu.erp.model.authentication.AuthToken;
import io.mateu.erp.model.booking.CancellationTerm;
import io.mateu.erp.model.booking.parts.ExcursionBooking;
import io.mateu.erp.model.booking.parts.GenericBooking;
import io.mateu.erp.model.invoicing.Charge;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.payments.BookingDueDate;
import io.mateu.erp.model.product.generic.GenericProduct;
import io.mateu.erp.model.product.generic.Price;
import io.mateu.erp.model.product.tour.*;
import io.mateu.erp.model.world.Country;
import io.mateu.erp.model.world.Destination;
import io.mateu.erp.model.world.Zone;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.User;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;
import org.easytravelapi.GenericBookingService;
import org.easytravelapi.activity.*;
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

                List<GenericProduct> excursions = new ArrayList<>();

                for (String s : Splitter.on(',')
                        .trimResults()
                        .omitEmptyStrings()
                        .split(resorts)) {
                    if (s.startsWith("cou")) {
                        em.find(Country.class, s.substring(4)).getDestinations().forEach(d -> d.getZones().forEach(z -> z.getProducts().stream().filter(p -> p instanceof GenericProduct).forEach(p -> excursions.add((GenericProduct) p))));
                    } else if (s.startsWith("des")) {
                        em.find(Destination.class, Long.parseLong(s.substring(4))).getZones().forEach(z -> z.getProducts().stream().filter(p -> p instanceof GenericProduct).forEach(p -> excursions.add((GenericProduct) p)));
                    } else if (s.startsWith("zon")) {
                        em.find(Zone.class, Long.parseLong(s.substring(4))).getProducts().stream().filter(p -> p instanceof GenericProduct).forEach(p -> excursions.add((GenericProduct) p));
                    } else if (s.startsWith("gen")) {
                        excursions.add(em.find(GenericProduct.class, Long.parseLong(s.substring(4))));
                    }
                };


                excursions.forEach(e -> {
                    {
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
    public GetGenericRatesRS getGenericRates(String token, String productId, int adults, int children, int units, int start, int end, String language) throws Throwable {
        GetGenericRatesRS rs = new GetGenericRatesRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("Done");

        Helper.notransact(em -> {

            GenericProduct e = em.find(GenericProduct.class, Long.parseLong(productId.split("-")[1]));

            rs.setDateDependant(e.isDateDependant());
            rs.setDatesRangeDependant(e.isDatesRangeDependant());
            rs.setAdultsDependant(e.isAdultsDependant());
            rs.setChildrenDependant(e.isChildrenDependant());
            rs.setUnitsDependant(e.isUnitsDependant());
            rs.setKey(getKey(token, "" + e.getId(), start, end, language, units, adults, children));
            rs.setPrice(new BestDeal());
            rs.getPrice().setRetailPrice(new Amount("EUR", Helper.roundEuros(200.34 * (adults + children))));


        });

        return rs;
    }

    @Override
    public CheckGenericRS check(String token, String key, String language) throws Throwable {
        CheckGenericRS rs = new CheckGenericRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("Done");

        Helper.notransact(em -> {

            GenericBooking b = buildBookingFromKey(em, key);

            rs.setAvailable(true);
            rs.setKey(key);
            rs.setValue(new Amount("EUR", Helper.roundEuros(b.getTotalValue())));


        });

        return rs;
    }

    public String getKey(String token, String key, int start, int end, String language, int units, int adults, int children) throws IOException {
        return Base64.getEncoder().encodeToString(buildKey(token, key, start, end, language, units, adults, children).getBytes());
    }

    private String buildKey(String token, String key, int start, int end, String language, int units, int adults, int children) throws IOException {
        String s = "";

        Map<String, Object> data = new HashMap<>();

        data.put("token", token);
        data.put("product", key);
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


        try {
            Helper.notransact(em -> {

                GenericBooking b = buildBookingFromKey(em, key);

                // todo: meter todo esto en el priceFromServices de TransferBooking

                //b.price(em);

                List<io.mateu.erp.model.product.generic.Contract> contratos = em.createQuery("select s from " + io.mateu.erp.model.product.generic.Contract.class.getName() + " s").getResultList();

                int encontrados = 0;

                for (io.mateu.erp.model.product.generic.Contract c : contratos) {

                    boolean contratoOk = true;

                    contratoOk = contratoOk && !c.getValidFrom().isAfter(b.getStart());
                    contratoOk = contratoOk && !c.getValidTo().isBefore(b.getEnd());

                    //todo: comprobar file window y demás condiciones

                    if (contratoOk) {

                        for (Price p : c.getPrices()) {

                            boolean precioOk = true;

                            //todo: aplicar políticas precios correctamente
                            /*

                            boolean precioOk = p.getOrigin().getPoints().contains(b.getOrigin()) || p.getOrigin().getCities().contains(b.getOrigin().getZone());

                            precioOk = precioOk && (p.getDestination().getPoints().contains(b.getDestination()) || p.getDestination().getCities().contains(b.getDestination().getZone()));

                            precioOk = precioOk && p.getVehicle().getMinPax() <= b.getAdults() && p.getVehicle().getMaxPax() >= b.getAdults();

                            */

                            if (precioOk) {

                                double valor = Helper.roundEuros(p.getPricePerUnit() * b.getUnits() + p.getPricePerAdult() * b.getAdults() + p.getPricePerChild() * b.getChildren());
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


    private GenericBooking buildBookingFromKey(EntityManager em, String key) throws IOException {
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
        data.put("variant", variant);
        data.put("shift", shift);
        data.put("pickup", pickup);
        data.put("activityLanguage", activityLanguage);
         */

        GenericBooking b = new GenericBooking();
        User user = em.find(User.class, login);
        b.setAudit(new Audit(user));
        b.setAgency(em.find(Partner.class, idAgencia));
        b.setCurrency(b.getAgency().getCurrency());

        b.setProduct(em.find(GenericProduct.class, new Long(String.valueOf(data.get("product")))));
        b.setUnits((Integer) data.get("units"));
        b.setAdults((Integer) data.get("adults"));
        b.setChildren((Integer) data.get("children"));

        //Price p = em.find(Price.class, new Long(String.valueOf(data.get("priceId"))));


        int date = (Integer) data.get("start");
        LocalDate fecha = LocalDate.of((date - date % 10000) / 10000, ((date - date % 100) / 100) % 100, date % 100);

        b.setStart(fecha);
        date = (Integer) data.get("end");
        fecha = LocalDate.of((date - date % 10000) / 10000, ((date - date % 100) / 100) % 100, date % 100);
        b.setEnd(fecha);



        return b;
    }



    @Override
    public BookGenericRS bookGeneric(String token, BookGenericRQ rq) throws Throwable {
        System.out.println("rq=" + rq);

        BookGenericRS rs = new BookGenericRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("Booking confirmed ok");

        GenericBooking[] bs = {null};

        try {
            Helper.transact(new JPATransaction() {
                @Override
                public void run(EntityManager em) throws Throwable {

                    GenericBooking b = buildBookingFromKey(em, rq.getKey());

                    b.setConfirmed(true);
                    b.setAgencyReference(rq.getBookingReference());
                    if (b.getAgencyReference() == null) b.setAgencyReference("");
                    b.setSpecialRequests(rq.getCommentsToProvider());
                    b.setEmail(rq.getEmail());
                    b.setLeadName(rq.getLeadName());
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
