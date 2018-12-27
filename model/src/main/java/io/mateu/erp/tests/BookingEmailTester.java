package io.mateu.erp.tests;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.workflow.WorkflowEngine;

import java.time.LocalDateTime;

public class BookingEmailTester {

    public static void main(String[] args) {

        System.setProperty("appconf", "/home/miguel/work/erp.properties");


        try {
            testBooked(41l);
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

            b.sendBooked("miguelperezcolom@gmail.com", "test " + LocalDateTime.now());


        });

    }

}
