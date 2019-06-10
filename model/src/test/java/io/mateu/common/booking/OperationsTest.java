package io.mateu.common.booking;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.mateu.erp.model.authentication.ERPUser;
import io.mateu.erp.model.booking.ProcessingStatus;
import io.mateu.erp.model.booking.PurchaseOrderStatus;
import io.mateu.erp.model.booking.parts.FreeTextBooking;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.invoicing.Invoice;
import io.mateu.erp.model.payments.BookingPaymentAllocation;
import io.mateu.erp.model.payments.InvoicePaymentAllocation;
import io.mateu.erp.model.payments.Payment;
import io.mateu.erp.model.payments.PaymentLine;
import io.mateu.erp.model.population.Populator;
import io.mateu.erp.model.workflow.TaskStatus;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.model.authentication.AdminUser;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.util.EmailHelper;
import io.mateu.mdd.core.util.Helper;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;


public class OperationsTest {

    @BeforeClass
    public static void setUpClass() throws Throwable {
        Helper.closeEMFs();
        EmailHelper.setTesting(true);
        Populator.main();
        Populator.populateBaseForTests();
    }

    @Test
    public void test() throws Throwable {

        assertTrue(EmailHelper.isTesting());

        assertNotNull(Helper.find(AdminUser.class, "admin"));

    }


    @Test
    public void testBooking() throws Throwable {

        FreeTextBooking b = crearReserva(130.91, Populator.agencia.getCurrency(), 85.3, Populator.agencia.getCurrency());

        // comprobamos antes de confirmar
        Helper.notransact(em -> {

            FreeTextBooking xb = em.find(FreeTextBooking.class, b.getId());

            // no debe haber egenrado servicios, ya que no está confirmada
            assertEquals(0, xb.getServices().size());

            // debe haber generado exactamente 1 línea de cargo por el servicio
            assertEquals(1, xb.getServiceCharges().size());

            // la línea de cargo debe ser por el total
            assertEquals(130.91, xb.getServiceCharges().get(0).getTotal(), 0);

            // el total debe estar ok
            assertEquals(130.91, xb.getTotalValue(), 0);

        });


        // confirmamos el servicio

        Helper.transact(em -> {

            FreeTextBooking xb = em.find(FreeTextBooking.class, b.getId());

            xb.setConfirmed(true);

        });


        // comprobamos
        Helper.notransact(em -> {

            FreeTextBooking xb = em.find(FreeTextBooking.class, b.getId());

            // debe haber generado 1 servicio
            assertEquals(1, xb.getServices().size());

            // el coste del servicio estar ok
            assertEquals(85.3, xb.getServices().get(0).getOverridedCostValue(), 0);


            // el coste total debe estar ok
            assertEquals(85.3, xb.getTotalCost(), 0);

            // debemos haber creado la PO
            assertEquals(xb.getServices().get(0).getPurchaseOrders().size(), 1);

            // comprobamos el total de la línea de coste
            assertEquals(85.3, xb.getServices().get(0).getPurchaseOrders().get(0).getTotal(), 0);

            // el importe de la Po debe ser el coste
            assertEquals(85.3, xb.getServices().get(0).getPurchaseOrders().get(0).getTotal(), 0);

            // debe haber creado una línea de cargo para la PO
            assertEquals(1, xb.getServices().get(0).getPurchaseOrders().get(0).getCharges().size());

            // debemos haber creado el envío de la PO
            assertEquals(xb.getServices().get(0).getPurchaseOrders().get(0).getSendingTasks().size(), 1);

            // debemos haber enviado la PO
            assertEquals(xb.getServices().get(0).getPurchaseOrders().get(0).getSendingTasks().get(0).getStatus(), TaskStatus.FINISHED);

            // la po debe estar confirmada
            assertEquals(xb.getServices().get(0).getPurchaseOrders().get(0).getStatus(), PurchaseOrderStatus.CONFIRMED);

            // el servicio debe estar confirmado
            assertEquals(xb.getServices().get(0).getProcessingStatus(), ProcessingStatus.CONFIRMED);

            // la reserva debe estar disponible
            assertEquals(xb.isAvailable(), true);

            // debemos haber registrado que alguna vez se ha mandado al proveedor
            assertTrue(xb.getServices().get(0).isEverSentToProvider());


            // debe estar visible en el resumen
            assertTrue(xb.getServices().get(0).isVisibleInSummary());

        });


        // cambiamos el valor del servicio

        Helper.transact(em -> {

            FreeTextBooking xb = em.find(FreeTextBooking.class, b.getId());

            xb.setOverridedCost(70.01);

        });


        // comprobamos
        Helper.notransact(em -> {

            FreeTextBooking xb = em.find(FreeTextBooking.class, b.getId());

            // el coste total debe estar ok
            assertEquals(70.01, xb.getTotalCost(), 0);

            // el importe de la Po debe ser el coste
            assertEquals(70.01, xb.getServices().get(0).getPurchaseOrders().get(0).getTotal(), 0);
        });

        // cancelamos el servicio
        Helper.transact(em -> {

            FreeTextBooking xb = em.find(FreeTextBooking.class, b.getId());

            xb.setActive(false);

        });


        // comprobamos
        Helper.notransact(em -> {

            FreeTextBooking xb = em.find(FreeTextBooking.class, b.getId());

            assertEquals(false, xb.getServices().get(0).isActive());

            assertEquals(ProcessingStatus.CONFIRMED, xb.getServices().get(0).getProcessingStatus());

            // el coste total debe estar ok
            assertEquals(0, xb.getTotalCost(), 0);
        });

        // reactivamos el servicio
        Helper.transact(em -> {

            FreeTextBooking xb = em.find(FreeTextBooking.class, b.getId());

            xb.getProvider().setAutomaticOrderConfirmation(false);

            xb.setActive(true);

        });


        // comprobamos
        Helper.notransact(em -> {

            FreeTextBooking xb = em.find(FreeTextBooking.class, b.getId());

            assertEquals(true, xb.getServices().get(0).isActive());

            assertNotEquals(ProcessingStatus.CONFIRMED, xb.getServices().get(0).getProcessingStatus());

            // el coste total debe estar ok
            assertEquals(70.01, xb.getTotalCost(), 0);
        });


        // simulamos que el proveedor ha confirmado el servicio
        Helper.transact(em -> {

            FreeTextBooking xb = em.find(FreeTextBooking.class, b.getId());

            xb.getServices().get(0).getPurchaseOrders().get(0).setStatus(PurchaseOrderStatus.CONFIRMED);

        });


        // comprobamos
        Helper.notransact(em -> {

            FreeTextBooking xb = em.find(FreeTextBooking.class, b.getId());

            System.out.println("" + xb.getServices().get(0).getPurchaseOrders().get(0).getStatus().name());

            assertEquals(ProcessingStatus.CONFIRMED, xb.getServices().get(0).getProcessingStatus());

        });

        // cambiamos el titular de la reserva
        Helper.transact(em -> {

            FreeTextBooking xb = em.find(FreeTextBooking.class, b.getId());

            xb.setLeadName("Hola xxx");

        });


        // comprobamos que el servicio vuelve a estar pendiente
        Helper.notransact(em -> {

            FreeTextBooking xb = em.find(FreeTextBooking.class, b.getId());

            assertNotEquals(ProcessingStatus.CONFIRMED, xb.getServices().get(0).getProcessingStatus());

        });



        // simulamos que el proveedor ha rechazado el servicio
        Helper.transact(em -> {

            FreeTextBooking xb = em.find(FreeTextBooking.class, b.getId());

            xb.getServices().get(0).getPurchaseOrders().get(0).setStatus(PurchaseOrderStatus.REJECTED);

        });


        // comprobamos
        Helper.notransact(em -> {

            FreeTextBooking xb = em.find(FreeTextBooking.class, b.getId());

            System.out.println("" + xb.getServices().get(0).getPurchaseOrders().get(0).getStatus().name());

            assertEquals(ProcessingStatus.REJECTED, xb.getServices().get(0).getProcessingStatus());

        });


        // cambiamos el titular de la reserva
        Helper.transact(em -> {

            FreeTextBooking xb = em.find(FreeTextBooking.class, b.getId());

            xb.setLeadName("Hola caracola");

        });


        // comprobamos
        Helper.notransact(em -> {

            FreeTextBooking xb = em.find(FreeTextBooking.class, b.getId());

            assertTrue(xb.getChanges().size() > 0);

        });



        // cambiamos el titular de la reserva
        Helper.transact(em -> {

            FreeTextBooking xb = em.find(FreeTextBooking.class, b.getId());

            xb.setLeadName("Hola caracola");

        });


        // comprobamos
        Helper.notransact(em -> {

            FreeTextBooking xb = em.find(FreeTextBooking.class, b.getId());

            assertTrue(xb.getChanges().size() > 0);

        });




        // añadimos un cobro
        Helper.transact(em -> {

            FreeTextBooking xb = em.find(FreeTextBooking.class, b.getId());

            BookingPaymentAllocation pa;
            xb.getPayments().add(pa = new BookingPaymentAllocation());
            pa.setBooking(xb);
            pa.setValue(30.5);
            Payment p;
            pa.setPayment(p = new Payment());
            p.setBreakdown(Lists.newArrayList(pa));

            PaymentLine l;
            p.setLines(Lists.newArrayList(l = new PaymentLine()));
            l.setPayment(p);
            l.setMethodOfPayment(Populator.visa);
            l.setCurrency(xb.getCurrency());
            l.setValue(60);
            p.setAgent(xb.getAgency().getFinancialAgent());
            p.setDate(LocalDate.now());
            p.setAccount(Populator.banco);

            em.persist(p);

        });


        // comprobamos
        Helper.notransact(em -> {

            FreeTextBooking xb = em.find(FreeTextBooking.class, b.getId());

            assertEquals(30.5, xb.getTotalPaid(), 0);

            assertEquals(-100.41, xb.getBalance(), 0);


            assertEquals(29.5, xb.getPayments().get(0).getPayment().getBalance(), 0);

        });


        // añadimos otro cobro
        Helper.transact(em -> {

            FreeTextBooking xb = em.find(FreeTextBooking.class, b.getId());

            BookingPaymentAllocation pa;
            xb.getPayments().add(pa = new BookingPaymentAllocation());
            pa.setBooking(xb);
            pa.setValue(50);
            Payment p;
            pa.setPayment(p = new Payment());
            p.setBreakdown(Lists.newArrayList(pa));

            PaymentLine l;
            p.setLines(Lists.newArrayList(l = new PaymentLine()));
            l.setPayment(p);
            l.setMethodOfPayment(Populator.visa);
            l.setCurrency(xb.getCurrency());
            l.setValue(60);
            p.setAgent(xb.getAgency().getFinancialAgent());
            p.setDate(LocalDate.now());
            p.setAccount(Populator.banco);

            em.persist(p);

        });


        // comprobamos
        Helper.notransact(em -> {

            FreeTextBooking xb = em.find(FreeTextBooking.class, b.getId());

            assertEquals(80.5, xb.getTotalPaid(), 0);

            assertEquals(-50.41, xb.getBalance(), 0);

        });




        // facturamos
        Helper.notransact(em -> {

            FreeTextBooking xb = em.find(FreeTextBooking.class, b.getId());

            xb.invoice().createInvoice();

        });


        // comprobamos
        Helper.notransact(em -> {

            FreeTextBooking xb = em.find(FreeTextBooking.class, b.getId());

            assertNotNull(xb.getCharges().get(0).getInvoice());

            assertEquals(130.91, xb.getCharges().get(0).getInvoice().getTotal(), 0);


            // se han pasado los pagos a la factura?
            assertEquals(50.41, xb.getCharges().get(0).getInvoice().getBalance(), 0);

            // como hay pendiente, no debería figurar como pagada
            assertFalse(xb.getCharges().get(0).getInvoice().isPaid());

        });


        // terminamos de cobrar la factura
        Helper.transact(em -> {

            FreeTextBooking xb = em.find(FreeTextBooking.class, b.getId());

            Invoice i = xb.getCharges().get(0).getInvoice();

            Payment p = new Payment();
            p.setAgent(xb.getAgency().getFinancialAgent());
            p.setDate(LocalDate.now());
            p.setAccount(Populator.banco);
            em.persist(p);

            PaymentLine l;
            p.setLines(Lists.newArrayList(l = new PaymentLine()));
            l.setPayment(p);
            l.setMethodOfPayment(Populator.visa);
            l.setCurrency(xb.getCurrency());
            l.setValue(i.getBalance());


            InvoicePaymentAllocation ipa;
            i.getPayments().add(ipa = new InvoicePaymentAllocation());
            ipa.setInvoice(i);
            ipa.setPayment(p);
            ipa.setValue(p.getValueInNucs());
            ipa.getPayment().setBreakdown(Lists.newArrayList(ipa));

        });


        // comprobamos
        Helper.notransact(em -> {

            FreeTextBooking xb = em.find(FreeTextBooking.class, b.getId());

            assertNotNull(xb.getCharges().get(0).getInvoice());

            assertEquals(0, xb.getCharges().get(0).getInvoice().getBalance(), 0);

            assertTrue(xb.getCharges().get(0).getInvoice().isPaid());

        });


    }

    public static FreeTextBooking crearReserva(double venta, Currency monedaVenta, double compra, Currency monedaCompra) throws Throwable {
        FreeTextBooking b = new FreeTextBooking();
        Helper.transact(em -> {
            b.setAudit(new Audit(MDD.getCurrentUser()));
            b.setAgency(Populator.agencia);//em.createQuery(em.getCriteriaBuilder().createQuery(Partner.class)).getResultList()
            b.setAgencyReference("TEST");
            b.setPos(Populator.pos);
            b.setProvider(Populator.proveedor);

            b.setValueOverrided(true);
            b.setOverridedValue(venta);
            b.setCurrency(monedaVenta);
            b.setOverridedBillingConcept(em.find(BillingConcept.class, "ANY"));


            b.setStart(LocalDate.of(2019, 10, 1));
            b.setEnd(LocalDate.of(2019, 10, 6));
            b.setServiceDescription("Servicios varios prueba");
            b.setProductLine(Populator.prodLine);
            b.setOffice(Populator.office);

            b.setCostOverrided(true);
            b.setOverridedCost(compra);
            b.setOverridedCostCurrency(monedaCompra);

            em.persist(b);
        });
        return b;
    }


}
