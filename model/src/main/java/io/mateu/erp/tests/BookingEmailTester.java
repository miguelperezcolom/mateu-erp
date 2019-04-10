package io.mateu.erp.tests;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.workflow.WorkflowEngine;

import java.io.File;
import java.time.LocalDateTime;

public class BookingEmailTester {

    public static void main(String[] args) {

        System.setProperty("appconf", "/home/miguel/work/erp.properties");
        System.setProperty("appconf", "/home/miguel/work/quotravel.properties");

        try {
            //testBooked(2618l); // traslado confirmada
            //testBooked(2617l); // traslado cancelada
            testVouchers(2616l); // traslado confirmada
            //testVouchers(2617l); // traslado cancelada
            //testVouchers(2630l); // texto libre
            //testVouchers(2626l); // genérico
        } catch (Throwable e) {
            e.printStackTrace();
        }


        WorkflowEngine.exit(0);

    }

    private static void testBooked(long id) throws Throwable {

        Helper.transact(em -> {
            AppConfig.get(em).setBookedEmailTemplate(Helper.leerFichero(Helper.class.getResourceAsStream("/io/mateu/erp/freemarker/bookingemail.ftl")));
        });


        Helper.transact(em -> {

            Booking b = em.find(Booking.class, id);

            b.sendBooked(em, "miguelperezcolom@gmail.com", "");


        });

    }

    private static void testVouchers(long id) throws Throwable {

        Helper.transact(em -> {
            AppConfig.get(em).setXslfoForVoucher(Helper.leerFichero(Helper.class.getResourceAsStream("/io/mateu/erp/xsl/voucher.xsl")));
        });


        Helper.transact(em -> {

            Booking b = em.find(Booking.class, id);

            b.writeVouchers(em, new File("/home/miguel/Descargas/test_voucher.pdf"));


        });

    }

}
