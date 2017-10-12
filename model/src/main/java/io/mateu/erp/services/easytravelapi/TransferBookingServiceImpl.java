package io.mateu.erp.services.easytravelapi;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.product.transfer.Contract;
import io.mateu.erp.model.product.transfer.Price;
import io.mateu.erp.model.product.transfer.PricePer;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import org.easytravelapi.TransferBookingService;
import org.easytravelapi.common.Amount;
import org.easytravelapi.common.CancellationCost;
import org.easytravelapi.common.Remark;
import org.easytravelapi.transfer.*;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by miguel on 27/7/17.
 */
public class TransferBookingServiceImpl implements TransferBookingService {

    @Override
    public GetAvailableTransfersRS getAvailabeTransfers(String token, String fromTransferPointId, String toTransferPointId, int pax, List<Integer> ages, int bikes, int golfBaggages, int bigLuggages, int wheelChairs, int incomingDate, int outgoingDate) throws Throwable {
        GetAvailableTransfersRS rs = new GetAvailableTransfersRS();

        //todo: validar auth token

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);

        long t0 = System.currentTimeMillis();

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                TransferPoint tp0 = em.find(TransferPoint.class, Long.parseLong(fromTransferPointId.substring("tp_".length())));
                TransferPoint tp1 = em.find(TransferPoint.class, Long.parseLong(toTransferPointId.substring("tp_".length())));

                List<Contract> contratos = em.createQuery("select s from " + Contract.class.getName() + " s").getResultList();

                LocalDate llegada = LocalDate.of((incomingDate - incomingDate % 10000) / 10000, ((incomingDate - incomingDate % 100) / 100) % 100, incomingDate % 100);
                LocalDate salida = LocalDate.of((outgoingDate - outgoingDate % 10000) / 10000, ((outgoingDate - outgoingDate % 100) / 100) % 100, outgoingDate % 100);


                int encontrados = 0;

                for (Contract c : contratos) {

                    boolean contratoOk = true;

                    contratoOk = contratoOk && c.getValidFrom().isAfter(llegada);
                    contratoOk = contratoOk && c.getValidTo().isBefore(salida);

                    //todo: comprobar booking window y demás condiciones

                    if (contratoOk) {

                        for (Price p : c.getPrices()) {

                            boolean precioOk = p.getOrigin().getPoints().contains(tp0) || p.getOrigin().getCities().contains(tp0.getCity());

                            precioOk = precioOk && (p.getDestination().getPoints().contains(tp1) || p.getDestination().getCities().contains(tp1.getCity()));

                            precioOk = precioOk && p.getVehicle().getMinPax() <= pax && p.getVehicle().getMaxPax() >= pax;

                            if (precioOk) {

                                AvailableTransfer t;
                                rs.getAvailableTransfers().add(t = new AvailableTransfer());
                                t.setType("" + c.getTransferType());
                                t.setDescription(p.getVehicle().getName() + " " + p.getVehicle().getMaxPax() + " - " + p.getVehicle().getMaxPax());
                                t.setVehicle(p.getVehicle().getName());
                                Amount a;
                                t.setNetPrice(a = new Amount());
                                a.setCurrencyIsoCode("EUR");
                                double valor = p.getPrice();
                                if (PricePer.PAX.equals(p.getPricePer())) valor = valor * pax;
                                valor = Helper.roundEuros(valor);
                                a.setValue(valor);

                                t.setKey(buildKey(token, fromTransferPointId, toTransferPointId, pax, ages, bikes, golfBaggages, bigLuggages, wheelChairs, incomingDate, outgoingDate, p, valor));


                                encontrados++;

                            }

                        }

                    }

                }

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
    public GetTransferPriceDetailsRS getTransferPriceDetails(String token, String key) {

        //todo: validar auth token

        GetTransferPriceDetailsRS rs = new GetTransferPriceDetailsRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("Price details");

        /*

        {
            CancellationCost c;
            rs.getCancellationCosts().add(c = new CancellationCost());
            c.setGMTtime(LocalDateTime.of(2018, 06, 05, 12, 00).format(DateTimeFormatter.ISO_DATE_TIME));
            Amount a;
            c.setNet(a = new Amount());
            a.setCurrencyIsoCode("EUR");
            a.setValue(250.32);
        }

        {
            CancellationCost c;
            rs.getCancellationCosts().add(c = new CancellationCost());
            c.setGMTtime(LocalDateTime.of(2018, 07, 01, 12, 00).format(DateTimeFormatter.ISO_DATE_TIME));
            Amount a;
            c.setNet(a = new Amount());
            a.setCurrencyIsoCode("EUR");
            a.setValue(400);
        }

        {
            Remark r;
            rs.getRemarks().add(r = new Remark());
            r.setType("IMPORTANT");
            r.setText("This service must be paid in 24 hors. Otherwise it will be automatically cancelled.");
        }
        {
            Remark r;
            rs.getRemarks().add(r = new Remark());
            r.setType("WARNING");
            r.setText("You must present the voucher that you will receive by email, after payment.");
        }        {
            Remark r;
            rs.getRemarks().add(r = new Remark());
            r.setType("INFO");
            r.setText("Have a nice day");
        }
        */


        return rs;
    }

    @Override
    public BookTransferRS bookTransfer(String token, BookTransferRQ rq) throws Throwable {

        //todo: validar auth token

        BookTransferRS rs = new BookTransferRS();

        rs.setSystemTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        rs.setStatusCode(200);
        rs.setMsg("Booking confirmed ok");


        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                /*
                Booking b = new Booking();
                b.setAgency();
                b.setAgencyReference();
                b.setAudit();
                b.setConfirmed();
                b.setCurrency();
                b.setEmail();
                b.setFinish();
                b.setLeadName();
                b.setStart();
                b.setTelephone();
                b.setTotal();

                em.persist(b);

                TransferService s = new TransferService();
                s.setAirport();
                s.setDirection();
                s.setDropoff();
                s.setFlightNumber();
                s.setFlightOriginOrDestination();
                s.setFlightTime();
                s.setPax();
                s.setPreferredVehicle();
                s.setTransferType();
                s.setAudit();
                s.setBooking();
                s.setFinish();
                s.setOverridedValue();
                s.setComment();
                s.setStart();

                em.persist(s);
                */

            }
        });


        rs.setBookingId("5643135431");

        return rs;
    }


}
