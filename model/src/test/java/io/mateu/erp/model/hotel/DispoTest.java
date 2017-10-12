package io.mateu.erp.model.hotel;


import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import org.junit.Test;

import javax.persistence.EntityManager;

public class DispoTest {

    @Test
    public void test1() throws Throwable {

        /*
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

//                System.out.println("" + em.createQuery("select h from " + Hotel.class.getName() + " h").getResultList().size() + " hoteles encontrados");

            }
        });
        */

    }

}
