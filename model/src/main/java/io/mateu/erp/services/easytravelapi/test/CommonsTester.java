package io.mateu.erp.services.easytravelapi.test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import io.mateu.erp.model.authentication.ERPUser;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.PurchaseOrder;
import io.mateu.erp.model.booking.QuotationRequest;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.booking.parts.TransferBooking;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.invoicing.BookingCharge;
import io.mateu.erp.model.invoicing.IssuedInvoice;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.organization.PointOfSaleSettlementForm;
import io.mateu.erp.model.population.Populator;
import io.mateu.erp.model.workflow.SendPurchaseOrdersByEmailTask;
import io.mateu.erp.model.workflow.SendPurchaseOrdersTask;
import io.mateu.erp.services.easytravelapi.*;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.workflow.WorkflowEngine;
import org.easytravelapi.generic.BookGenericRQ;
import org.easytravelapi.hotel.BookHotelRQ;
import org.easytravelapi.hotel.BookingKey;
import org.easytravelapi.hotel.GetHotelPriceDetailsRQ;
import org.easytravelapi.hotel.GetHotelRatesRQ;
import org.easytravelapi.transfer.BookTransferRQ;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CommonsTester {


    public static void main(String[] args) {
        System.setProperty("appconf", "/home/miguel/work/quotravel.properties");

        String token = "eyAiY3JlYXRlZCI6ICJGcmkgTWFyIDIyIDEwOjIyOjI5IENFVCAyMDE5IiwgInVzZXJJZCI6ICJhZG1pbiIsICJhZ2VuY3lJZCI6ICIzIn0=";


        //testxx();


        //testPortfolio(token);

        //testAvailHotels(token);

        //testHotelRates(token);

        //testHotelDetails(token);

        //testHotelConfirm(token);

        //testAvailTransfers(token);

        //testAvailTransfers(token);

        //testTransferDetails(token);

        //testTransferConfirm(token);
        
        //testGenericAvail(token);

        //testGenericRates(token);

        //testGenericCheck(token);

        //testGenericPriceDetail(token);

        //testGenericConfirm(token);

        //testTourAvail(token);

        //testExcursionAvail(token);

        //testExcursionRates(token);

        //testCircuitAvail(token);

        //testCircuitRates(token);

        //testGroup();

        //testInvoice();

        testLiquidacion();

        WorkflowEngine.exit(0);
    }

    private static void testLiquidacion() {
        try {

            Helper.transact(em -> {

                AppConfig.get(em).setXslfoForPOSSettlement(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/xsl/liquidacion_pos.xsl"), Charsets.UTF_8));

            });


            Helper.transact(em -> {

                PointOfSale pos = em.find(PointOfSale.class, 1l);

                PointOfSaleSettlementForm liq = new PointOfSaleSettlementForm(pos);
                liq.search();

                liq.crearPdf(em, new File("/home/miguel/Descargas/liquidacion.pdf"));


            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }


    private static void testInvoice() {

        try {
            Helper.transact(em -> {

                AppConfig.get(em).setXslfoForIssuedInvoice(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/xsl/factura.xsl"), Charsets.UTF_8));

            });

            Helper.transact(em -> {

                io.mateu.erp.model.booking.File file = em.find(io.mateu.erp.model.booking.File.class, 320l);

                Document xml = new Document(new Element("invoices"));

                List<BookingCharge> charges = new ArrayList<>();
                for (Booking b : file.getBookings()) charges.addAll(b.getCharges());

                Booking firstBooking = file.getBookings().size() > 0?file.getBookings().get(0):null;


                if (firstBooking != null) xml.getRootElement().addContent(new IssuedInvoice(MDD.getCurrentUser(), charges, true, firstBooking.getAgency().getCompany().getFinancialAgent(), firstBooking.getAgency().getFinancialAgent(), null).toXml(em));

                System.out.println(Helper.toString(xml.getRootElement()));


                File temp = new File("/home/miguel/Descargas/factura.pdf");

                FileOutputStream fileOut = new FileOutputStream(temp);
                //String sxslfo = Resources.toString(Resources.getResource(Contract.class, xslfo), Charsets.UTF_8);
                String sxml = new XMLOutputter(Format.getPrettyFormat()).outputString(xml);
                System.out.println("xml=" + sxml);
                fileOut.write(Helper.fop(new StreamSource(new StringReader(AppConfig.get(em).getXslfoForIssuedInvoice())), new StreamSource(new StringReader(sxml))));
                fileOut.close();

            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    private static void testGroup() {

        try {
            Helper.transact(em -> {

                //AppConfig.get(em).setXslfoForQuotationRequest(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/xsl/grupo.xsl"), Charsets.UTF_8));
                AppConfig.get(em).setPurchaseOrderTemplate(Resources.toString(Resources.getResource(Populator.class, "/io/mateu/erp/freemarker/purchaseorder.ftl"), Charsets.UTF_8));

            });

            Helper.transact(em -> {

                QuotationRequest r = em.find(QuotationRequest.class, 1l);

                //r.createProforma(em, new File("/home/miguel/Descargas/testGrupo.pdf"));
                //r.confirm();

                List<PurchaseOrder> pos = new ArrayList<>();

                for (Booking b : r.getFile().getBookings()) for (Service s : b.getServices()) pos.addAll(s.getPurchaseOrders());

                for (PurchaseOrder po : pos) {
                    for (SendPurchaseOrdersTask t : po.getSendingTasks()) {
                        Map<String, Object> data = t.getData();
                        System.out.println("data=" + Helper.toJson(data));
                        Helper.escribirFichero("/home/miguel/Descargas/po_" + t.getId() + ".html", Helper.freemark(AppConfig.get(em).getPurchaseOrderTemplate(), data));
                    }
                }

            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    private static void testxx() {

        try {
            Helper.transact(em -> {

                TransferBooking b = em.find(TransferBooking.class, 12525l);
                b.setSpecialRequests(b.getSpecialRequests() + "x");

            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    private static void testCircuitRates(String token) {
        try {
            System.out.println(Helper.toJson(new CircuitBookingServiceImpl().getCircuitRates(token, "cir-153", 20190501, "es")));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void testCircuitAvail(String token) {
        try {
            System.out.println(Helper.toJson(new CircuitBookingServiceImpl().getAvailableCircuits(token, "es")));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void testExcursionRates(String token) {
        try {
            System.out.println(Helper.toJson(new ActivityBookingServiceImpl().getActivityRates(token, "exc-155", 20190501, "es")));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void testExcursionAvail(String token) {
        try {
            System.out.println(Helper.toJson(new ActivityBookingServiceImpl().getAvailableActivities(token, 20190325, "cou-ES", "es")));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void testTourAvail(String token) {
    }

    private static void testGenericConfirm(String token) {
        try {
            BookGenericRQ rq = new BookGenericRQ();
            rq.setKey("ewogICJwcm9kdWN0IiA6ICIxIiwKICAiY2hpbGRyZW4iIDogMCwKICAiYWR1bHRzIiA6IDAsCiAgInZhcmlhbnQiIDogbnVsbCwKICAic3RhcnQiIDogMjAxOTA2MDEsCiAgImVuZCIgOiAyMDE5MDYwNywKICAibGFuZ3VhZ2UiIDogImVzIiwKICAidW5pdHMiIDogMSwKICAidG9rZW4iIDogImV5QWlZM0psWVhSbFpDSTZJQ0pYWldRZ1JtVmlJREl3SURFMk9qSTNPakUxSUVORlZDQXlNREU1SWl3Z0luVnpaWEpKWkNJNklDSjNaV0lpTENBaWNHRnlkRzVsY2tsa0lqb2dJakVpZlE9PSIKfQ==");
            rq.setBookingReference("Test " + LocalDateTime.now());
            rq.setCommentsToProvider("Test");
            rq.setEmail("miguelperezcolom@gmail.com");
            rq.setLeadName("Mr test");

            System.out.println(Helper.toJson(new GenericBookingServiceImpl().bookGeneric(token, rq)));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void testGenericPriceDetail(String token) {
        try {
            System.out.println(Helper.toJson(new GenericBookingServiceImpl().getGenericPriceDetails(token, "ewogICJwcm9kdWN0IiA6ICIzIiwKICAiY2hpbGRyZW4iIDogMCwKICAiYWR1bHRzIiA6IDAsCiAgInZhcmlhbnQiIDogIjIiLAogICJzdGFydCIgOiAyMDE5MDYwMSwKICAiZW5kIiA6IDIwMTkwNjA3LAogICJsYW5ndWFnZSIgOiAiZXMiLAogICJ1bml0cyIgOiAxLAogICJ0b2tlbiIgOiAiZXlBaVkzSmxZWFJsWkNJNklDSk5iMjRnUm1WaUlERTRJREUxT2pJd09qUXhJRU5GVkNBeU1ERTVJaXdnSW5WelpYSkpaQ0k2SUNKaFpHMXBiaUlzSUNKd1lYSjBibVZ5U1dRaU9pQWlNeUo5Igp9", "es", null, null)));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void testGenericCheck(String token) {
        try {
            System.out.println(Helper.toJson(new GenericBookingServiceImpl().check(token, "gen-1", 0, 0, 1, 20190601, 20190607, "es")));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void testGenericRates(String token) {
        try {
            System.out.println(Helper.toJson(new GenericBookingServiceImpl().getGenericRates(token, "gen-3", "es")));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void testGenericAvail(String token) {
        try {
            System.out.println(Helper.toJson(new GenericBookingServiceImpl().getAvailableGenerics(token, "cou-ES", "es")));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void testTransferConfirm(String token) {
        String key = "ewogICJmcm9tVHJhbnNmZXJQb2ludElkIiA6ICJ0cC0xMTQiLAogICJiaWtlcyIgOiAwLAogICJwYXgiIDogMSwKICAidmFsb3IiIDogNjcuODIsCiAgInRva2VuIiA6ICJleUFpWTNKbFlYUmxaQ0k2SUNKR2Nta2dUV0Z5SURJeUlERXdPakl5T2pJNUlFTkZWQ0F5TURFNUlpd2dJblZ6WlhKSlpDSTZJQ0poWkcxcGJpSXNJQ0poWjJWdVkzbEpaQ0k2SUNJekluMD0iLAogICJ3aGVlbENoYWlycyIgOiAwLAogICJ0b1RyYW5zZmVyUG9pbnRJZCIgOiAidHAtMTE1IiwKICAib3V0Z29pbmdEYXRlIiA6IDIwMTkwNTE3LAogICJpbmNvbWluZ0RhdGUiIDogMjAxOTA1MTMsCiAgImFnZXMiIDogWyBdLAogICJnb2xmQmFnZ2FnZXMiIDogMCwKICAicHJpY2VJZCIgOiA1LAogICJiaWdMdWdnYWdlcyIgOiAwCn0=";
        try {
            BookTransferRQ rq = new BookTransferRQ();
            rq.setBookingReference("Test " + LocalDateTime.now());
            rq.setCommentsToProvider("Test");
            rq.setEmail("miguelperezcolom@gmail.com");
            rq.setLeadName("Mr test");
            rq.setPrivateComments("Test");
            rq.setKey(key);
            rq.setIncomingFlightNumber("IB1234");
            rq.setIncomingFlightOrigin("MAD");
            rq.setIncomingFlightTime(1650);

            rq.setOutgoingFlightNumber("AE4587");
            rq.setOutgoingFlightDestination("CDG");
            rq.setOutgoingFlightTime(2030);

            System.out.println(Helper.toJson(new TransferBookingServiceImpl().bookTransfer(token, rq)));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    private static void testTransferDetails(String token) {
        String key = "ewogICJmcm9tVHJhbnNmZXJQb2ludElkIiA6ICJ0cC0xIiwKICAiYmlrZXMiIDogMCwKICAicGF4IiA6IDIsCiAgInZhbG9yIiA6IDE2MS4xLAogICJ0b2tlbiIgOiAiZXlBaVkzSmxZWFJsWkNJNklDSlVhSFVnUkdWaklESTNJREUxT2pFNU9qUTBJRU5GVkNBeU1ERTRJaXdnSW5WelpYSkpaQ0k2SUNKM1pXSjRJaXdnSW5CaGNuUnVaWEpKWkNJNklDSTBJbjA9IiwKICAid2hlZWxDaGFpcnMiIDogMCwKICAidG9UcmFuc2ZlclBvaW50SWQiIDogInRwLTIiLAogICJvdXRnb2luZ0RhdGUiIDogMjAxOTA1MTMsCiAgImluY29taW5nRGF0ZSIgOiAyMDE5MDUwMSwKICAiYWdlcyIgOiBbIF0sCiAgImdvbGZCYWdnYWdlcyIgOiAwLAogICJwcmljZUlkIiA6IDIsCiAgImJpZ0x1Z2dhZ2VzIiA6IDAKfQ==";
        try {
            System.out.println(Helper.toJson(new TransferBookingServiceImpl().getTransferPriceDetails(token, key, "")));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void testAvailTransfers(String token) {
        try {
            System.out.println(Helper.toJson(new TransferBookingServiceImpl().getAvailabeTransfers(token, "tp-1", "tp-49", 1, 0, 0, 0, 0, 0, 20190501, 20190513)));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void testHotelConfirm(String token) {
        try {
            BookHotelRQ rq = new BookHotelRQ();
            rq.setBookingReference("Test " + LocalDateTime.now());
            rq.setCommentsToProvider("Test");
            rq.setEmail("miguelperezcolom@gmail.com");
            rq.setLeadName("Mr test");
            rq.setPrivateComments("Test");
            BookingKey k;
            rq.getRateKeys().add(k = new BookingKey());
            k.setRateKey("NC0xMC0yMDE5MDYwMS0yMDE5MDYwOC00Mi0xMS00MC0xMS0xLTItMC0=");

            // k.setOccupancy(); //todo: sobra
            //k.setRequestPaymentData(); //todo: sobra
            //k.setRoomId(); //todo: sobra
            //k.setRoomName(); //todo: sobra
            //rq.setServices(); //todo: esto sobra?

            System.out.println(Helper.toJson(new HotelBookingServiceImpl().bookHotel(token, rq)));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }


    private static void testHotelDetails(String token) {
        try {
            GetHotelPriceDetailsRQ rq = new GetHotelPriceDetailsRQ();
            rq.setLanguage("es");
            rq.setRatekeys("NC0xMC0yMDE5MDYwMS0yMDE5MDYwOC00Mi0xMS00MC0xMS0xLTItMC0=");
            rq.setCoupon("");
            System.out.println(Helper.toJson(new HotelBookingServiceImpl().getHotelPriceDetails(token, rq)));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void testHotelRates(String token) {
        try {
            GetHotelRatesRQ rq = new GetHotelRatesRQ();
            rq.setCheckin(20190601);
            rq.setCheckout(20190608);
            rq.setHotelId("hot-6");
            rq.setLanguage("es");
            rq.setOccupancies("1x2");
            System.out.println(Helper.toJson(new HotelBookingServiceImpl().getRates(token, rq)));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void testPortfolio(String token) {

        try {
            System.out.println(Helper.toJson(new CommonsServiceImpl().getPortfolio(token)));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    private static void testAvailHotels(String token) {

        try {
            System.out.println(Helper.toJson(new HotelBookingServiceImpl().getAvailableHotels(token, "es", "des-1", 20190513, 20190517, "1x2", true)));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

}
