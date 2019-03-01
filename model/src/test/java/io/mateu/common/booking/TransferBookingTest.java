package io.mateu.common.booking;

import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.booking.ProcessingStatus;
import io.mateu.erp.model.booking.PurchaseOrderStatus;
import io.mateu.erp.model.booking.parts.GenericBooking;
import io.mateu.erp.model.booking.parts.TransferBooking;
import io.mateu.erp.model.population.Populator;
import io.mateu.erp.model.product.transfer.TransferType;
import io.mateu.erp.model.workflow.TaskStatus;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.util.EmailHelper;
import io.mateu.mdd.core.util.Helper;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.*;


public class TransferBookingTest {

    @BeforeClass
    public static void setUpClass() throws Throwable {
        Helper.closeEMFs();
        EmailHelper.setTesting(true);
        Populator.main();
        Populator.populateBaseForTests();
        Populator.populateTransferProduct();
    }

    @Test
    public void test() throws Throwable {

        assertTrue(EmailHelper.isTesting());

        assertNotNull(Helper.find(User.class, "admin"));

    }


    @Test
    public void testBooking() throws Throwable {

        TransferBooking b = new TransferBooking();
        Helper.transact(em -> {
            b.setAudit(new Audit(MDD.getCurrentUser()));
            b.setAgency(Populator.agencia);//em.createQuery(em.getCriteriaBuilder().createQuery(Partner.class)).getResultList()
            b.setAgencyReference("TEST");
            b.setPos(Populator.pos);

            b.setTransferType(TransferType.PRIVATE);
            b.setOrigin(Populator.apt);
            b.setDestination(Populator.hotelEnAlcudia);

            b.setAdults(3);

            b.setArrivalFlightOrigin("MAD");
            b.setArrivalFlightNumber("UX4578");
            b.setArrivalFlightTime(LocalDateTime.of(2019, 10, 1, 10, 45));

            b.setDepartureFlightDestination("CDG");
            b.setDepartureFlightNumber("IB4577");
            b.setDepartureFlightTime(LocalDateTime.of(2019, 10, 7, 20, 30));


            em.persist(b);
        });

        // comprobamos antes de confirmar
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // no debe haber egenrado servicios, ya que no está confirmada
            assertEquals(0, xb.getServices().size());

            // debe haber generado exactamente 1 línea de cargo por el servicio
            assertEquals(1, xb.getServiceCharges().size());

            // la línea de cargo debe ser por el total
            assertEquals(120.8, xb.getServiceCharges().get(0).getTotal().getValue(), 0);

            // el total debe estar ok
            assertEquals(120.8, xb.getTotalValue(), 0);

        });


        // confirmamos el servicio

        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.setConfirmed(true);

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // debe haber generado 1 servicio
            assertEquals(2, xb.getServices().size());

            // el coste del servicio debe estar ok
            assertEquals(20.5, xb.getServices().get(0).getTotal(), 0);


            // el coste total debe estar ok
            assertEquals(41, xb.getTotalCost(), 0);

            // debemos haber creado las POs
            assertEquals(1, xb.getServices().get(0).getPurchaseOrders().size());

            // debemos haber creado el envío de la PO
            assertEquals(1, xb.getServices().get(0).getPurchaseOrders().get(0).getSendingTasks().size());

            // debemos haber enviado la PO
            assertEquals(TaskStatus.FINISHED, xb.getServices().get(0).getPurchaseOrders().get(0).getSendingTasks().get(0).getStatus());

            // la po debe estar confirmada
            assertEquals(PurchaseOrderStatus.CONFIRMED, xb.getServices().get(0).getPurchaseOrders().get(0).getStatus());

            // el 1er servicio debe estar confirmado
            assertEquals(ProcessingStatus.CONFIRMED, xb.getServices().get(0).getProcessingStatus());

            // el 2o servicio debe estar confirmado
            assertEquals(ProcessingStatus.CONFIRMED, xb.getServices().get(1).getProcessingStatus());

            // la reserva debe estar disponible
            assertEquals(true, xb.isAvailable());
        });

    }


}
