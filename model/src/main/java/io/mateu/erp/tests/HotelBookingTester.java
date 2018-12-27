package io.mateu.erp.tests;

import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.PurchaseOrder;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.booking.parts.HotelBooking;
import io.mateu.erp.model.booking.parts.HotelBookingLine;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.product.ContractType;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.erp.model.workflow.SendPurchaseOrdersByEmailTask;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.workflow.WorkflowEngine;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class HotelBookingTester {

    public static void main(String[] args) {

        System.setProperty("appconf", "/home/miguel/work/erp.properties");
        

        //testHtmlPreview(16);

        try {
            //test(41, "/home/miguel/work");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }


        try {
            //test2(41, "/home/miguel/work");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        try {
            testCrear("/home/miguel/work");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        WorkflowEngine.exit(0);
    }

    private static void testCrear(String s) throws Throwable {

        Helper.transact(em -> {

            HotelBooking b = new HotelBooking();
            b.setAudit(new Audit(MDD.getCurrentUser()));
            b.setConfirmed(true);
            b.setActive(true);
            b.setPos(em.find(PointOfSale.class, 1l));
            b.setAgency(em.find(Partner.class, 4l)); // der its
            b.setAgencyReference("TEST " + LocalDateTime.now());
            b.setLeadName("MR TEST");
            b.setHotel(em.find(Hotel.class, 5l)); // casin vt
            HotelBookingLine l;
            b.getLines().add(l = new HotelBookingLine());
            l.setActive(true);
            l.setBooking(b);
            l.setRoom(b.getHotel().getRooms().get(0));
            l.setBoard(b.getHotel().getBoards().get(0));
            l.setRooms(1);
            l.setStart(LocalDate.of(2019, 6, 1));
            l.setEnd(LocalDate.of(2019, 6, 5));
            l.setAdultsPerRoon(2);
            l.setChildrenPerRoom(0);
            l.setAges(null);
            l.setContract(b.getHotel().getContracts().stream().filter(c -> ContractType.SALE.equals(c.getType())).findFirst().get());
            l.setInventory(l.getContract().getInventory());
            l.check();
            l.price();

            em.persist(b);
        });

    }


    private static void test2(long bookingId, String donde) throws Throwable {

        Helper.transact(em -> {

            User u = em.find(User.class, "admin");

            Booking b = em.find(Booking.class, bookingId);

            long t0 = System.currentTimeMillis();


            try {

                for (Service s : b.getServices()) {
                    for (PurchaseOrder po : s.getPurchaseOrders()) {
                        po.send(em, u);
                    }
                }


            } catch (Exception e1) {
                e1.printStackTrace();
            }

            System.out.println("hecho en " + (System.currentTimeMillis() - t0) + "ms.!");


        });


        Helper.transact(em -> {


            Booking b = em.find(Booking.class, bookingId);

            System.out.println("hola");


        });

    }

    private static void test(long bookingId, String donde) throws Throwable {

        Helper.transact(em -> {

            User u = em.find(User.class, "admin");

            Booking b = em.find(Booking.class, bookingId);

            long t0 = System.currentTimeMillis();


            try {

                b.generateServices(em);

                for (Service s : b.getServices()) {
                    s.checkPurchase(em, u);
                }


            } catch (Exception e1) {
                e1.printStackTrace();
            }

            System.out.println("hecho en " + (System.currentTimeMillis() - t0) + "ms.!");


        });


        Helper.transact(em -> {


            Booking b = em.find(Booking.class, bookingId);

            System.out.println("hola");


        });

    }
}
