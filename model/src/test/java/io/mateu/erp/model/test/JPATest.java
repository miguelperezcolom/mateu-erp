package io.mateu.erp.model.test;

import io.mateu.erp.model.authentication.Permission;
import io.mateu.erp.model.util.Helper;
import io.mateu.erp.model.util.JPATransaction;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import javax.persistence.EntityManager;

/**
 * Unit test for simple App.
 */
public class JPATest
        extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public JPATest(String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( JPATest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() throws Exception {



        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {

                /*


                Permission p = new Permission();
                p.setId(2);
                p.setName("Prueba 2");
                em.persist(p);
        */

            }
        });



        assertTrue( true );
    }
}
