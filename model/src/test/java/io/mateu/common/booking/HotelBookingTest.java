package io.mateu.common.booking;

import io.mateu.erp.model.authentication.ERPUser;
import io.mateu.erp.model.booking.ProcessingStatus;
import io.mateu.erp.model.booking.PurchaseOrderStatus;
import io.mateu.erp.model.booking.parts.HotelBooking;
import io.mateu.erp.model.booking.parts.HotelBookingLine;
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


public class HotelBookingTest {

    @BeforeClass
    public static void setUpClass() throws Throwable {
        Helper.closeEMFs();
        EmailHelper.setTesting(true);
        Populator.main();
        Populator.populateBaseForTests();
        Populator.populateHotelProduct();
    }

    @Test
    public void test() throws Throwable {

        assertTrue(EmailHelper.isTesting());

        assertNotNull(Helper.find(AdminUser.class, "admin"));

    }


    @Test
    public void testBooking() throws Throwable {

        HotelBooking b = new HotelBooking();
        Helper.transact(em -> {
            b.setAudit(new Audit(MDD.getCurrentUser()));
            b.setAgency(Populator.agencia);//em.createQuery(em.getCriteriaBuilder().createQuery(Partner.class)).getResultList()
            b.setAgencyReference("TEST");
            b.setLeadName("Mr test");
            b.setPos(Populator.pos);
            b.setCurrency(Populator.agencia.getCurrency());

            b.setStart(LocalDate.of(2019, 10, 1));
            b.setEnd(LocalDate.of(2019, 10, 6));

            b.setHotel(Populator.hotel);
            HotelBookingLine l;
            b.getLines().add(l = new HotelBookingLine());
            l.setBooking(b);
            l.setActive(true);
            l.setBoard(b.getHotel().getBoards().get(0));
            l.setRoom(b.getHotel().getRooms().get(0));
            l.setRooms(1);
            l.setAdultsPerRoom(2);

            em.persist(b);
        });

        // comprobamos antes de confirmar
        Helper.notransact(em -> {

            HotelBooking xb = em.find(HotelBooking.class, b.getId());

            // no debe haber egenrado servicios, ya que no está confirmada
            assertEquals(0, xb.getServices().size());

            // debe haber generado exactamente 1 línea de cargo por el servicio
            assertEquals(1, xb.getServiceCharges().size());

            // la línea de cargo debe ser por el total
            assertEquals(244, xb.getServiceCharges().get(0).getTotal(), 0);

            // el total debe estar ok
            assertEquals(244, xb.getTotalValue(), 0);

        });


        // confirmamos el servicio

        Helper.transact(em -> {

            HotelBooking xb = em.find(HotelBooking.class, b.getId());

            xb.setConfirmed(true);

        });


        // comprobamos
        Helper.notransact(em -> {

            HotelBooking xb = em.find(HotelBooking.class, b.getId());

            // debe haber generado 1 servicio
            assertEquals(1, xb.getServices().size());

            // el coste total debe estar ok
            assertEquals(305, xb.getServices().get(0).getTotalSale(), 0);


            // el coste total debe estar ok
            assertEquals(305, xb.getTotalCost(), 0);

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
