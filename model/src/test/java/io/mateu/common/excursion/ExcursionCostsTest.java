package io.mateu.common.excursion;

import io.mateu.erp.model.authentication.ERPUser;
import io.mateu.erp.model.population.Populator;
import io.mateu.mdd.core.model.authentication.AdminUser;
import io.mateu.mdd.core.model.util.EmailHelper;
import io.mateu.mdd.core.util.Helper;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExcursionCostsTest {

    @BeforeClass
    public static void setUpClass() throws Throwable {
        Helper.closeEMFs();
        EmailHelper.setTesting(true);
        Populator.main();
        Populator.populateBaseForTests();
        Populator.populateExcursionProduct();
    }

    @Test
    public void test() throws Throwable {

        assertTrue(EmailHelper.isTesting());

        assertNotNull(Helper.find(AdminUser.class, "admin"));

    }

    @Test
    public void testAll() {



        assertEquals(10, 10);

    }

}
