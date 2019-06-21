package tests;

import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.erp.model.product.hotel.Inventory;
import io.mateu.erp.tests.TestPopulator;
import io.mateu.mdd.core.model.util.EmailHelper;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;
import io.mateu.mdd.core.workflow.WorkflowEngine;

import java.util.List;

public class Tester {

    public static void main(String... args) throws Throwable {

        System.setProperty("appconf", "/home/miguel/work/quotravel.properties");

        EmailHelper.setTesting(true);


        //populate();

        testVuelos();

        testSplit();

        //tesCacheQuery();

        //testCuboParos();

        //testCuboCupo();

        WorkflowEngine.exit(0);
    }

    private static void testVuelos() {

        try {
            Helper.transact(em -> {




            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    private static void testSplit() {

        for (String s : "a@b.c, b@c.d:x@y.z\nx@y.z1;".split("[,;:\n \t]+")) {
            System.out.println("-->" + s);
        }
    }

    private static void populate() throws Throwable {

        TestPopulator.populateEverything();
    }

    private static void testCuboCupo() throws Throwable {

        Helper.transact((JPATransaction) (em) -> {


            if (true) {
                List<Hotel> hs = em.createQuery("select h from " + Hotel.class.getName() + " h").getResultList();

                for (Hotel h : hs) {
                    for (Inventory i : h.getInventories()) i.build(em);
                }
            }


        });


    }

    private static void testCuboParos() throws Throwable {

        Helper.transact((JPATransaction) (em) -> {


            if (true) {
                List<Hotel> hs = em.createQuery("select h from " + Hotel.class.getName() + " h").getResultList();

                for (Hotel h : hs) {
                    h.getStopSales().build(em);
                }
            }


        });


    }

    private static void tesCacheQuery() throws Throwable {
//
//        TestPopulator.populateAll();
//
//        Helper.transact((JPATransaction) (em) -> {
//
//
//            if (true) {
//                List<Hotel> hs = em.createQuery("select h from Hotel h").getResultList();
//
//                int desde = 435;
//                for (Hotel h : hs) {
//                    h.setQuoonId("" + desde++);
//                }
//            }
//
//
//        });
//
//

        for (int i = 0; i < 10; i++) {

            int finalI = i;
            Helper.transact((JPATransaction) (em) -> {

                for (int j = 0; j < 10; j++) {

                    //Hotel h = Hotel.getByQuoonId(em, "435");

                    //System.out.println("" + finalI + "," + j + ".h = " + h);

                }

            });

        }

    }

}
