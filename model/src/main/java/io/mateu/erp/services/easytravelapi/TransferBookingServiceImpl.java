package io.mateu.erp.services.easytravelapi;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;
import io.mateu.erp.model.authentication.AuthToken;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.CancellationTerm;
import io.mateu.erp.model.booking.parts.TransferBooking;
import io.mateu.erp.model.invoicing.Charge;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.payments.BookingDueDate;
import io.mateu.erp.model.product.transfer.*;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.User;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;
import org.easytravelapi.TransferBookingService;
import org.easytravelapi.common.*;
import org.easytravelapi.transfer.*;

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
public class TransferBookingServiceImpl implements TransferBookingService {

    @Override
    public GetAvailableTransfersRS getAvailabeTransfers(String token, String fromTransferPointId, String toTransferPointId, int pax, int bikes, int golfBaggages, int skis, int bigLuggages, int wheelChairs, int incomingDate, int outgoingDate) throws Throwable {
        GetAvailableTransfersRS rs = new GetAvailableTransfersRS();

        //todo: validar auth token

        List<Integer> ages = new ArrayList<>();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);

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


        long finalIdAgencia = idAgencia;
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                TransferBooking b = new TransferBooking();
                b.setAgency(em.find(Partner.class, finalIdAgencia));
                b.setAdults(pax);

                b.setOrigin(em.find(TransferPoint.class, Long.parseLong(fromTransferPointId.substring("tp-".length()))));
                b.setDestination(em.find(TransferPoint.class, Long.parseLong(toTransferPointId.substring("tp-".length()))));

                if (incomingDate > 0) b.setArrivalFlightTime(LocalDate.of((incomingDate - incomingDate % 10000) / 10000, ((incomingDate - incomingDate % 100) / 100) % 100, incomingDate % 100).atTime(0, 0));
                if (outgoingDate > 0) b.setDepartureFlightTime(LocalDate.of((outgoingDate - outgoingDate % 10000) / 10000, ((outgoingDate - outgoingDate % 100) / 100) % 100, outgoingDate % 100).atTime(0, 0));

                int encontrados = 0;

                Lists.newArrayList(TransferType.SHUTTLE, TransferType.PRIVATE, TransferType.EXECUTIVE).forEach(tipo -> {
                    b.setTransferType(tipo);
                    b.priceServices(em);
                    if (b.getTotalValue() > 0) {
                        AvailableTransfer t = new AvailableTransfer();
                        t.setType("" + b.getTransferType());
                        t.setDescription(b.getPriceForVehicle().getName() + " " + b.getPriceForVehicle().getMinPax() + " - " + b.getPriceForVehicle().getMaxPax());
                        t.setVehicle(b.getPriceForVehicle().getName());
                        Amount a;
                        t.setTotal(new BestDeal());
                        t.getTotal().setRetailPrice(a = new Amount());
                        a.setCurrencyIsoCode("EUR");
                        a.setValue(b.getTotalValue());

                        try {
                            t.setKey(getKey(token, fromTransferPointId, toTransferPointId, pax, ages, bikes, golfBaggages, bigLuggages, wheelChairs, incomingDate, outgoingDate, b.getPriceFromPriceLine(), b.getTotalValue()));
                            rs.getAvailableTransfers().add(t);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                rs.setMsg("" + encontrados + " transfers found. It consumed 24 ms in the server.");

            }
        });

        long t = System.currentTimeMillis();

        rs.setMsg(rs.getMsg() + " It consumed " + (t - t0) + " ms in the server.");

        return rs;
    }

    public String getKey(String token, String fromTransferPointId, String toTransferPointId, int pax, List<Integer> ages, int bikes, int golfBaggages, int bigLuggages, int wheelChairs, int incomingDate, int outgoingDate, Price p, double valor) throws IOException {
        return Base64.getEncoder().encodeToString(buildKey(token, fromTransferPointId, toTransferPointId, pax, ages, bikes, golfBaggages, bigLuggages, wheelChairs, incomingDate, outgoingDate, p, valor).getBytes());
    }

    private String buildKey(String token, String fromTransferPointId, String toTransferPointId, int pax, List<Integer> ages, int bikes, int golfBaggages, int bigLuggages, int wheelChairs, int incomingDate, int outgoingDate, Price p, double valor) throws IOException {
        String s = "";

        Map<String, Object> data = new HashMap<>();

        data.put("token", token);
        data.put("fromTransferPointId", fromTransferPointId);
        data.put("toTransferPointId", toTransferPointId);
        data.put("pax", pax);
        data.put("ages", ages);
        data.put("bikes", bikes);
        data.put("golfBaggages", golfBaggages);
        data.put("bigLuggages", bigLuggages);
        data.put("wheelChairs", wheelChairs);
        data.put("incomingDate", incomingDate);
        data.put("outgoingDate", outgoingDate);
        data.put("priceId", p.getId());
        data.put("valor", valor);

        s = Helper.toJson(data);

        return s;
    }

    @Override
    public GetTransferPriceDetailsRS getTransferPriceDetails(String token, String key, String coupon) {

        //todo: validar auth token

        GetTransferPriceDetailsRS rs = new GetTransferPriceDetailsRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("Price details");

        try {
            Helper.notransact(em -> {

                TransferBooking b = buildBookingFromKey(em, key);

                b.priceServices(em);

                b.createCharges(em);
                b.summarize(em);

                if (b.getBikes() != 0) {
                    Remark r;
                    rs.getRemarks().add(r = new Remark());
                    r.setType("INFO");
                    r.setText("Bikes are on request and an extra charge will be added.");
                }
                if (b.getBigLuggages() != 0) {
                    Remark r;
                    rs.getRemarks().add(r = new Remark());
                    r.setType("INFO");
                    r.setText("Big luggages are on request and an extra charge will be added.");
                }
                if (b.getGolf() != 0) {
                    Remark r;
                    rs.getRemarks().add(r = new Remark());
                    r.setType("INFO");
                    r.setText("Golf baggages are on request and an extra charge will be added.");
                }
                if (b.getWheelChairs() != 0) {
                    Remark r;
                    rs.getRemarks().add(r = new Remark());
                    r.setType("INFO");
                    r.setText("Wheel chairs are on request and an extra charge will be added.");
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

    private TransferBooking buildBookingFromKey(EntityManager em, String key) throws IOException {
        Map<String, Object> data = Helper.fromJson(new String((Base64.getDecoder().decode(key))));

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
        data.put("fromTransferPointId", fromTransferPointId);
        data.put("toTransferPointId", toTransferPointId);
        data.put("pax", pax);
        data.put("ages", ages);
        data.put("bikes", bikes);
        data.put("golfBaggages", golfBaggages);
        data.put("bigLuggages", bigLuggages);
        data.put("wheelChairs", wheelChairs);
        data.put("incomingDate", incomingDate);
        data.put("outgoingDate", outgoingDate);
        data.put("priceId", p.getId());
        data.put("valor", valor);
         */

        int incomingDate = (int) data.get("incomingDate");
        int outgoingDate = (int) data.get("outgoingDate");

        TransferBooking b = new TransferBooking();
        User user = em.find(User.class, login);
        b.setAudit(new Audit(user));
        b.setAgency(em.find(Partner.class, idAgencia));
        b.setCurrency(b.getAgency().getCurrency());

        b.setOrigin(em.find(TransferPoint.class, new Long(String.valueOf(data.get("fromTransferPointId")).split("-")[1])));
        b.setDestination(em.find(TransferPoint.class, new Long(String.valueOf(data.get("toTransferPointId")).split("-")[1])));
        b.setAdults((Integer) data.get("pax"));

        Price p = em.find(Price.class, new Long(String.valueOf(data.get("priceId"))));

        b.setTransferType(p.getTransferType());

        LocalDate llegada = LocalDate.of((incomingDate - incomingDate % 10000) / 10000, ((incomingDate - incomingDate % 100) / 100) % 100, incomingDate % 100);
        LocalDate salida = LocalDate.of((outgoingDate - outgoingDate % 10000) / 10000, ((outgoingDate - outgoingDate % 100) / 100) % 100, outgoingDate % 100);

        if (incomingDate != 0) {
            b.setArrivalFlightTime(llegada.atTime(0, 0));
        }
        if (outgoingDate != 0) {
            b.setDepartureFlightTime(salida.atTime(0, 0));
        }


        if (data.containsKey("bikes")) b.setBikes((Integer) data.get("bikes"));
        if (data.containsKey("golfBaggages")) b.setGolf((Integer) data.get("golfBaggages"));
        if (data.containsKey("bigLuggages")) b.setBigLuggages((Integer) data.get("bigLuggages"));
        if (data.containsKey("wheelChairs")) b.setWheelChairs((Integer) data.get("wheelChairs"));

        return b;
    }

    @Override
    public BookTransferRS bookTransfer(String token, BookTransferRQ rq) throws Throwable {

        //todo: validar auth token

        BookTransferRS rs = new BookTransferRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("Booking confirmed ok");


        TransferBooking[] bs = {null};

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                TransferBooking b = buildBookingFromKey(em, rq.getKey());

                b.setConfirmed(true);
                b.setAgencyReference(rq.getBookingReference());
                if (b.getAgencyReference() == null) b.setAgencyReference("");
                b.setSpecialRequests(rq.getCommentsToProvider());
                b.setEmail(rq.getEmail());
                b.setLeadName(rq.getLeadName());
                b.setPrivateComments(rq.getPrivateComments());
                b.setPos(em.find(AuthToken.class, token).getPos());
                b.setTelephone(rq.getContactPhone());

                b.setExpiryDate(LocalDateTime.now().plusHours(2)); // por defecto caduca a las 2 horas


                if (b.getArrivalFlightTime() != null) {
                    b.setArrivalFlightTime(b.getArrivalFlightTime().toLocalDate().atTime((rq.getIncomingFlightTime() - rq.getIncomingFlightTime() % 100) / 100, rq.getIncomingFlightTime() % 100));
                }
                if (b.getDepartureFlightTime() != null) {
                    b.setDepartureFlightTime(b.getDepartureFlightTime().toLocalDate().atTime((rq.getOutgoingFlightTime() - rq.getOutgoingFlightTime() % 100) / 100, rq.getOutgoingFlightTime() % 100));
                }


                em.persist(b);

                bs[0] = b;

            }
        });


        rs.setBookingId("" + bs[0].getId());


        return rs;
    }







    @Override
    public GetAirportsRS getAirports(String token) throws Throwable {
        GetAirportsRS rs = new GetAirportsRS();
        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("Airports returned");

        ((List<TransferPoint>) Helper.selectObjects("select x from " + TransferPoint.class.getName() + " x order by x.order")).stream().filter(p -> TransferPointType.AIRPORT.equals(p.getType()) || TransferPointType.PORT.equals(p.getType())).forEach(p -> {
            Resource r;
            rs.getAirports().add(r = new Resource());
            r.setResourceId("tp-" + p.getId());
            r.setName(new MultilingualText("es", p.getName(), "en", p.getName()));
            r.setType("transferpoint");
            r.setDescription(new MultilingualText("es", p.getInstructions(), "en", p.getInstructions()));
        });

        return rs;
    }


    @Override
    public GetDestinationRS getDestinationsForAirport(String token, String originId) throws Throwable {
        GetDestinationRS rs = new GetDestinationRS();
        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("Possible destinations filtered");

        Helper.notransact(em -> {
            TransferPoint origin = em.find(TransferPoint.class, Long.parseLong(originId.split("-")[1]));

            origin.getZone().getDestination().getZones().forEach(z -> z.getTransferPoints().forEach(p -> {
                if (!p.equals(origin)) {
                    Resource r;
                    rs.getDestination().add(r = new Resource());
                    r.setResourceId("tp-" + p.getId());
                    r.setName(new MultilingualText("es", p.getName(), "en", p.getName()));
                    r.setType("transferpoint");
                    r.setDescription(new MultilingualText("es", p.getInstructions(), "en", p.getInstructions()));
                }
            }));

        });


        return rs;
    }

    @Override
    public GetAvailableTransfersRS getFilteredTransfers(String token, String fromTransferPointId, String toTransferPointId, int pax,
            int bikes, int golfBaggages, int skis, int bigLuggages, int wheelChairs, int incomingDate, int outgoingDate, String transfertypes, double minPrice, double maxPrice
    ) throws Throwable {
        GetAvailableTransfersRS rs = getAvailabeTransfers(token, fromTransferPointId, toTransferPointId, pax, bikes, golfBaggages, skis, bigLuggages, wheelChairs, incomingDate, outgoingDate);

        if (!Strings.isNullOrEmpty(transfertypes)) {
            List<String> types = Lists.newArrayList(transfertypes.split(","));
            rs.setAvailableTransfers(rs.getAvailableTransfers().stream().filter(h -> types.contains(h.getType())).collect(Collectors.toList()));
        }
        if (minPrice != 0) {
            rs.setAvailableTransfers(rs.getAvailableTransfers().stream().filter(h -> h.getTotal() != null && h.getTotal().getRetailPrice() != null && h.getTotal().getRetailPrice().getValue() >= minPrice).collect(Collectors.toList()));
        }
        if (maxPrice != 0) {
            rs.setAvailableTransfers(rs.getAvailableTransfers().stream().filter(h -> h.getTotal() != null && h.getTotal().getRetailPrice() != null && h.getTotal().getRetailPrice().getValue() <= maxPrice).collect(Collectors.toList()));
        }


        return rs;
    }


}
