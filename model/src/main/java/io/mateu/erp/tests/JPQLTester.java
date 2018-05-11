package io.mateu.erp.tests;

import io.mateu.erp.model.product.transfer.TransferType;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.IOException;
import java.util.List;

public class JPQLTester {

    public static void main(String[] args) throws Throwable {

        System.setProperty("appconf", "/home/miguel/quonext/mateu.properties");


        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                String jpql = "select count(x)  from TransferService x left join x.effectivePickup epu  " +
                        " where 1 = 1 and x.direction = io.mateu.erp.model.booking.transfer.TransferDirection.OUTBOUND  " +
                        " and x.start >= {d '2018-05-11'}  " +
                        " and x.effectivePickup.id = 84";

                jpql = "select x.id, x.booking.leadName, " +

                        " x.transferType, case when " +
                        " ap.id != null and x.transferType != " + TransferType.class.getTypeName() + ".EXECUTIVE and (" +
                        " x.transferType = " + TransferType.class.getTypeName() + ".SHUTTLE " +
                        " or epu.alternatePointForNonExecutive = true " +
                        ") then ap.name else epu.name end " +

                        //" x.transferType, case when x.id = 23 then 'A' else 'B' end, epu.name, ap.name " +

                        " from TransferService x " +
                        " left join x.effectivePickup epu " +
                        " left join epu.alternatePointForShuttle ap " +
                        " where 1 = 1 and x.direction = io.mateu.erp.model.booking.transfer.TransferDirection.OUTBOUND  " +
                        " and x.start >= {d '2018-05-11'}  "
                        // + " and x.effectivePickup.id = 84"
                ;

                System.out.println(jpql);

                Query q = em.createQuery(jpql);

                List l = q.getResultList();


                l.forEach(x -> {
                    try {
                        System.out.println(Helper.toJson(x));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                System.out.println("numero resultados: " + l.size());

            }
        });

    }

}
