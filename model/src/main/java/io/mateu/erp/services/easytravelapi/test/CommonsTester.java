package io.mateu.erp.services.easytravelapi.test;

import io.mateu.erp.services.easytravelapi.CommonsServiceImpl;
import io.mateu.erp.services.easytravelapi.HotelBookingServiceImpl;
import io.mateu.erp.services.easytravelapi.TransferBookingServiceImpl;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.workflow.WorkflowEngine;
import org.easytravelapi.hotel.BookHotelRQ;
import org.easytravelapi.hotel.BookingKey;
import org.easytravelapi.hotel.GetHotelPriceDetailsRQ;
import org.easytravelapi.hotel.GetHotelRatesRQ;
import org.easytravelapi.transfer.BookTransferRQ;

import java.time.LocalDateTime;

public class CommonsTester {


    public static void main(String[] args) {
        System.setProperty("appconf", "/home/miguel/work/demo.properties");

        String token = "eyAiY3JlYXRlZCI6ICJUaHUgRGVjIDI3IDE1OjE5OjQ0IENFVCAyMDE4IiwgInVzZXJJZCI6ICJ3ZWJ4IiwgInBhcnRuZXJJZCI6ICI0In0=";

        //testPortfolio(token);

        //testAvailHotels(token);

        //testHotelRates(token);

        //testHotelDetails(token);

        //testHotelConfirm(token);

        //testAvailTransfers(token);

        //testTransferDetails(token);

        testTransferConfirm(token);

        WorkflowEngine.exit(0);
    }

    private static void testTransferConfirm(String token) {
        String key = "ewogICJmcm9tVHJhbnNmZXJQb2ludElkIiA6ICJ0cC0xIiwKICAiYmlrZXMiIDogMCwKICAicGF4IiA6IDIsCiAgInZhbG9yIiA6IDE2MS4xLAogICJ0b2tlbiIgOiAiZXlBaVkzSmxZWFJsWkNJNklDSlVhSFVnUkdWaklESTNJREUxT2pFNU9qUTBJRU5GVkNBeU1ERTRJaXdnSW5WelpYSkpaQ0k2SUNKM1pXSjRJaXdnSW5CaGNuUnVaWEpKWkNJNklDSTBJbjA9IiwKICAid2hlZWxDaGFpcnMiIDogMCwKICAidG9UcmFuc2ZlclBvaW50SWQiIDogInRwLTIiLAogICJvdXRnb2luZ0RhdGUiIDogMjAxOTA1MTMsCiAgImluY29taW5nRGF0ZSIgOiAyMDE5MDUwMSwKICAiYWdlcyIgOiBbIF0sCiAgImdvbGZCYWdnYWdlcyIgOiAwLAogICJwcmljZUlkIiA6IDIsCiAgImJpZ0x1Z2dhZ2VzIiA6IDAKfQ==";
        try {
            BookTransferRQ rq = new BookTransferRQ();
            rq.setBookingReference("Test " + LocalDateTime.now());
            rq.setCommentsToProvider("Test");
            rq.setEmail("miguelperezcolom@gmail.com");
            rq.setLeadName("Mr test");
            rq.setPrivateComments("Test");
            rq.setKey(key);

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
            System.out.println(Helper.toJson(new TransferBookingServiceImpl().getAvailabeTransfers(token, "tp-1", "tp-2", 2, 0, 0, 0, 0, 0, 20190501, 20190513)));
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
            rq.setHotelId("hot-10");
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
            System.out.println(Helper.toJson(new HotelBookingServiceImpl().getAvailableHotels(token, "es", "des-2", 20190601, 20190608, "1x2", true)));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

}
