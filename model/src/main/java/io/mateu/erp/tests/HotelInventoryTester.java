package io.mateu.erp.tests;

import io.mateu.erp.model.product.hotel.Inventory;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.workflow.WorkflowEngine;

import java.time.LocalDateTime;

public class HotelInventoryTester {

    public static void main(String[] args) throws Throwable {

        System.setProperty("appconf", "/home/miguel/work/erp.properties");


        testBuildCupo(5, "/home/miguel/work");

    }

    private static void testModifContrato(long idContrato, String donde) throws Throwable {


        Helper.transact(em -> {


            HotelContract c = em.find(HotelContract.class, idContrato);

            long t0 = System.currentTimeMillis();


            try {

                c.setPrivateComments("" + LocalDateTime.now());


            } catch (Exception e1) {
                e1.printStackTrace();
            }

            System.out.println("hecho en " + (System.currentTimeMillis() - t0) + "ms.");


        });


        WorkflowEngine.exit(0);

    }


    private static void testBuildCupo(long idContrato, String donde) throws Throwable {


        Helper.transact(em -> {


            Inventory c = em.find(Inventory.class, idContrato);

            long t0 = System.currentTimeMillis();


            try {

                c.build(em);
                //c.rebuild();


            } catch (Exception e1) {
                e1.printStackTrace();
            }

            System.out.println("hecho en " + (System.currentTimeMillis() - t0) + "ms.");


        });


        WorkflowEngine.exit(0);

    }

}
