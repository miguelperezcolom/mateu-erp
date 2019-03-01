package io.mateu.common.booking;

import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.booking.ProcessingStatus;
import io.mateu.erp.model.booking.PurchaseOrderStatus;
import io.mateu.erp.model.booking.parts.FreeTextBooking;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.population.Populator;
import io.mateu.erp.model.workflow.TaskStatus;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.util.EmailHelper;
import io.mateu.mdd.core.util.Helper;
import org.javamoney.moneta.FastMoney;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;


public class FreeTextBookingTest {

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

        assertNotNull(Helper.find(User.class, "admin"));

    }


    @Test
    public void testBooking() throws Throwable {

        FreeTextBooking b = new FreeTextBooking();
        Helper.transact(em -> {
            b.setAudit(new Audit(MDD.getCurrentUser()));
            b.setAgency(Populator.agencia);//em.createQuery(em.getCriteriaBuilder().createQuery(Partner.class)).getResultList()
            b.setAgencyReference("TEST");
            b.setPos(Populator.pos);
            b.setProvider(Populator.proveedor);

            b.setValueOverrided(true);
            b.setOverridedValue(FastMoney.of(130.91, "EUR"));
            b.setOverridedBillingConcept(em.find(BillingConcept.class, "ANY"));


            b.setStart(LocalDate.of(2019, 10, 1));
            b.setEnd(LocalDate.of(2019, 10, 6));
            b.setServiceDescription("Servicios varios prueba");
            b.setProductLine(Populator.prodLine);
            b.setOffice(Populator.office);

            b.setCostOverrided(true);
            b.setOverridedCost(FastMoney.of(85.3, "EUR"));

            em.persist(b);
        });

        // comprobamos antes de confirmar
        Helper.notransact(em -> {

            FreeTextBooking xb = em.find(FreeTextBooking.class, b.getId());

            // no debe haber egenrado servicios, ya que no está confirmada
            assertEquals(0, xb.getServices().size());

            // debe haber generado exactamente 1 línea de cargo por el servicio
            assertEquals(1, xb.getServiceCharges().size());

            // la línea de cargo debe ser por el total
            assertEquals(130.91, xb.getServiceCharges().get(0).getTotal().getValue(), 0);

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
        });


        // cambiamos el valor del servicio

        Helper.transact(em -> {

            FreeTextBooking xb = em.find(FreeTextBooking.class, b.getId());

            xb.setOverridedCost(FastMoney.of(70.01, "EUR"));

        });


        // comprobamos
        Helper.notransact(em -> {

            FreeTextBooking xb = em.find(FreeTextBooking.class, b.getId());

            // el coste total debe estar ok
            assertEquals(70.01, xb.getTotalCost(), 0);
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
            assertEquals(70.01, xb.getTotalCost(), 0);
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

    }


}
