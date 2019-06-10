package io.mateu.common.booking;

import com.google.common.collect.Lists;
import io.mateu.erp.model.authentication.ERPUser;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.QuotationRequest;
import io.mateu.erp.model.booking.QuotationRequestLine;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.payments.BookingPaymentAllocation;
import io.mateu.erp.model.payments.Payment;
import io.mateu.erp.model.payments.PaymentLine;
import io.mateu.erp.model.population.Populator;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.model.authentication.AdminUser;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.util.EmailHelper;
import io.mateu.mdd.core.model.util.EmailMock;
import io.mateu.mdd.core.util.Helper;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class PaymentTest {

    @BeforeClass
    public static void setUpClass() throws Throwable {
        Helper.closeEMFs();
        EmailHelper.setMock(new EmailMock());
        Populator.main();
        Populator.populateBaseForTests();
    }

    @Test
    public void test() throws Throwable {

        assertTrue(EmailHelper.isTesting());

        assertNotNull(Helper.find(AdminUser.class, "admin"));

    }


    @Test
    public void testCrear() throws Throwable {

        EmailHelper.getMock().reset();

        Payment p = crearPago();

        Helper.notransact(em -> {

            Payment px = em.find(Payment.class, p.getId());

            assertEquals(475, px.getValueInNucs(), 0);

            assertEquals(475, px.getBalance(), 0);

            assertEquals(475, px.getAgent().getBalance(), 0);

        });

        // comprobamos que no se ha enviado un email
        assertEquals(0, EmailHelper.getMock().getSent().size());

    }

    @Test
    public void testRepartoReserva() throws Throwable {

        EmailHelper.getMock().reset();

        Payment p = crearPago();
        Booking b = FreeTextBookingTest.crearReserva(300, Populator.usd, 150, Populator.gbp);

        Helper.transact(em -> {

            Payment px = em.find(Payment.class, p.getId());

            Booking bx = em.find(Booking.class, b.getId());

            BookingPaymentAllocation a;
            px.setBreakdown(Lists.newArrayList(a = new BookingPaymentAllocation()));
            a.setPayment(px);
            a.setBooking(bx);
            bx.getPayments().add(a);
            a.setValue(100); // valor en nucs

        });

        Helper.transact(em -> {

            Payment px = em.find(Payment.class, p.getId());

            Booking bx = em.find(Booking.class, b.getId());

            assertEquals(475, px.getValueInNucs(), 0);

            assertEquals(-200, bx.getBalance(), 0);

            assertEquals(-200, bx.getAgency().getBalance(), 0);

        });

    }

    private Payment crearPago() throws Throwable {
        Payment p = new Payment();

        Helper.transact(em -> {

            p.setAgent(Populator.agencia.getFinancialAgent());
            p.setDate(LocalDate.now());
            p.setAccount(Populator.banco);
            //p.setBreakdown();
            {
                PaymentLine l;
                p.setLines(Lists.newArrayList(l = new PaymentLine()));
                l.setPayment(p);
                l.setCurrency(em.find(Currency.class, "EUR"));
                l.setValue(300);
                l.setMethodOfPayment(Populator.visa);
            }
            {
                PaymentLine l;
                p.setLines(Helper.extend(p.getLines(), l = new PaymentLine()));
                l.setPayment(p);
                l.setCurrency(Populator.usd);
                l.setValue(200);
                l.setMethodOfPayment(Populator.visa);
            }


            em.persist(p);
        });

        return p;
    }



}
