package io.mateu.common.booking;

import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.booking.ProcessingStatus;
import io.mateu.erp.model.booking.PurchaseOrderStatus;
import io.mateu.erp.model.booking.parts.FreeTextBooking;
import io.mateu.erp.model.booking.parts.GenericBooking;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.population.Populator;
import io.mateu.erp.model.workflow.TaskStatus;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.util.EmailHelper;
import io.mateu.mdd.core.util.Helper;
import org.javamoney.moneta.FastMoney;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;


public class GenericBookingTest {

    @BeforeClass
    public static void setUpClass() throws Throwable {
        Helper.closeEMFs();
        EmailHelper.setTesting(true);
        Populator.main();
        Populator.populateBaseForTests();
        Populator.populateGenericProduct();
    }

    @Test
    public void test() throws Throwable {

        assertTrue(EmailHelper.isTesting());

        assertNotNull(Helper.find(User.class, "admin"));

    }


    @Test
    public void testBooking() throws Throwable {

        GenericBooking b = new GenericBooking();
        Helper.transact(em -> {
            b.setAudit(new Audit(MDD.getCurrentUser()));
            b.setAgency(Populator.agencia);//em.createQuery(em.getCriteriaBuilder().createQuery(Partner.class)).getResultList()
            b.setAgencyReference("TEST");
            b.setPos(Populator.pos);
            b.setCurrency(Populator.agencia.getCurrency());

            b.setStart(LocalDate.of(2019, 10, 1));
            b.setEnd(LocalDate.of(2019, 10, 6));
            b.setOffice(Populator.office);

            b.setProduct(Populator.genericProduct);
            b.setUnits(1);

            em.persist(b);
        });

        // comprobamos antes de confirmar
        Helper.notransact(em -> {

            GenericBooking xb = em.find(GenericBooking.class, b.getId());

            // no debe haber egenrado servicios, ya que no está confirmada
            assertEquals(0, xb.getServices().size());

            // debe haber generado exactamente 1 línea de cargo por el servicio
            assertEquals(1, xb.getServiceCharges().size());

            // la línea de cargo debe ser por el total
            assertEquals(251, xb.getServiceCharges().get(0).getTotal(), 0);

            // el total debe estar ok
            assertEquals(251, xb.getTotalValue(), 0);

        });


        // confirmamos el servicio

        Helper.transact(em -> {

            GenericBooking xb = em.find(GenericBooking.class, b.getId());

            xb.setConfirmed(true);

        });


        // comprobamos
        Helper.notransact(em -> {

            GenericBooking xb = em.find(GenericBooking.class, b.getId());

            // debe haber generado 1 servicio
            assertEquals(1, xb.getServices().size());

            // el coste total debe estar ok
            assertEquals(251, xb.getTotalValue(), 0);


            // el coste total debe estar ok
            assertEquals(150.5, xb.getTotalCost(), 0);

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

    }


}
