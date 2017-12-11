package io.mateu.erp.dispo.test;

import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class Tester {

    public static void main(String... args) throws Throwable {

        Map<String, Object> properties = new HashMap<String, Object>();

        Persistence.generateSchema("dispo", null);
        /*

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                em.createQuery("select x from " + World.class.getName() + " x").getResultList();

            }
        });
        */
    }

}
