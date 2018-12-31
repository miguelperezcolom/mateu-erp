package io.mateu.erp.tests;

import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.workflow.SendPurchaseOrdersByEmailTask;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.workflow.WorkflowEngine;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PleaseBookHtmlTester {

    public static void main(String[] args) throws Throwable {

        System.setProperty("appconf", "/home/miguel/work/erp.properties");


        //testHtmlPreview(16);

        test(95, "/home/miguel/work");


        WorkflowEngine.exit(0);
    }


    private static void test(long sendPurchaseOrderId, String donde) throws Throwable {

        Helper.transact(em -> {
            AppConfig.get(em).setPurchaseOrderTemplate(Helper.leerFichero(Helper.class.getResourceAsStream("/io/mateu/erp/freemarker/pleasebook.ftl")));
        });


        Helper.notransact(em -> {


            SendPurchaseOrdersByEmailTask po = em.find(SendPurchaseOrdersByEmailTask.class, sendPurchaseOrderId);

            long t0 = System.currentTimeMillis();


            try {

                try {
                    String archivo = UUID.randomUUID().toString();
                    archivo = "testpleasebook";

                    File temp = new File(new File(donde), archivo + ".html");

                    System.out.println("Temp file : " + temp.getAbsolutePath());


                    Helper.escribirFichero(temp.getAbsolutePath(), po.getMessage(AppConfig.get(em)));


                } catch (IOException e) {
                    e.printStackTrace();
                }


            } catch (Exception e1) {
                e1.printStackTrace();
            }

            System.out.println("hecho en " + (System.currentTimeMillis() - t0) + "ms.!");


        });


    }
}
