package io.mateu.common.booking;

import io.mateu.erp.model.authentication.ERPUser;
import io.mateu.erp.model.booking.ProcessingStatus;
import io.mateu.erp.model.booking.PurchaseOrderStatus;
import io.mateu.erp.model.booking.parts.TransferBooking;
import io.mateu.erp.model.partners.Provider;
import io.mateu.erp.model.population.Populator;
import io.mateu.erp.model.product.transfer.TransferType;
import io.mateu.erp.model.workflow.TaskStatus;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.model.authentication.AdminUser;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.util.EmailHelper;
import io.mateu.mdd.core.model.util.EmailMock;
import io.mateu.mdd.core.util.Helper;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;


public class TransferBookingTest {

    @BeforeClass
    public static void setUpClass() throws Throwable {
        Helper.closeEMFs();
        EmailHelper.setMock(new EmailMock());
        Populator.main();
        Populator.populateBaseForTests();
        Populator.populateTransferProduct();
    }

    private TransferBooking crearReserva() throws Throwable {

        EmailHelper.getMock().reset();

        TransferBooking b = new TransferBooking();
        Helper.transact(em -> {
            b.setAudit(new Audit(MDD.getCurrentUser()));
            b.setAgency(Populator.agencia);//em.createQuery(em.getCriteriaBuilder().createQuery(Partner.class)).getResultList()
            b.setAgencyReference("TEST");
            b.setLeadName("Mr. Test");
            b.setPos(Populator.pos);
            b.setCurrency(Populator.agencia.getCurrency());

            b.setEmail("miguelperezcolom@gmail.com");

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

            em.find(Provider.class, Populator.proveedor.getId()).setAutomaticOrderConfirmation(true);
            em.find(Provider.class, Populator.proveedor.getId()).setAutomaticOrderSending(true);
        });
        return b;
    }

    private TransferBooking crearReservaConfirmada() throws Throwable {
        TransferBooking b = crearReserva();

        // confirmamos el servicio

        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.setConfirmed(true);

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // debe haber generado 2 servicios
            assertEquals(2, xb.getServices().size());

            // el coste del servicio debe estar ok
            assertEquals(60.4, xb.getServices().get(0).getTotalSale(), 0);

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



        EmailHelper.getMock().print();

        // 1 email booking recibida
        // 1 email booking confirmada
        // 2 emails compra servicios
        assertEquals(4, EmailHelper.getMock().getSent().size());

        return b;
    }


    @Test
    public void test() throws Throwable {

        assertTrue(EmailHelper.isTesting());

        assertNotNull(Helper.find(AdminUser.class, "admin"));

    }


    @Test
    public void testBooking() throws Throwable {

        TransferBooking b = crearReserva();

        // comprobamos antes de confirmar
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // no debe haber egenrado servicios, ya que no está confirmada
            assertEquals(0, xb.getServices().size());

            // debe haber generado exactamente 1 línea de cargo por el servicio
            assertEquals(1, xb.getServiceCharges().size());

            // la línea de cargo debe ser por el total
            assertEquals(120.8, xb.getServiceCharges().get(0).getTotal(), 0);

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
            assertEquals(60.4, xb.getServices().get(0).getTotalSale(), 0);


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


        // cancelamos el servicio

        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.cancel(em);

        });

        EmailHelper.getMock().print();

        // 1 email booking recibida
        // 1 email booking confirmada
        // 2 emails compra servicios
        // 1 email booking cancelada
        // 2 emails cancelación servicios
        assertEquals(7, EmailHelper.getMock().getSent().size());

    }



    @Test
    public void testBookingPaid() throws Throwable {

        EmailHelper.getMock().reset();

        TransferBooking b = crearReserva();

        Helper.transact(em -> {
            TransferBooking xb = em.find(TransferBooking.class, b.getId());
            xb.enterPayment(em, Populator.banco, Populator.visa, xb.getCurrency(), xb.getTotalValue());
        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // depués de pagar el saldo debe ser 0
            assertEquals(0, xb.getBalance(), 0);

            // la reserva debe aparecer como pagada
            assertEquals(true, xb.isPaid());

            // la reserva debe aparecer como pagada
            EmailHelper.getMock().print();

            // 1 email booking recibida
            // 1 email booking confirmada
            // 2 emails compra servicios
            assertEquals(4, EmailHelper.getMock().getSent().size());

        });


        Helper.transact(em -> {
            TransferBooking xb = em.find(TransferBooking.class, b.getId());
            xb.cancel(em);
        });

        // la reserva debe aparecer como pagada
        EmailHelper.getMock().print();

        // 1 email booking recibida
        // 1 email booking confirmada
        // 2 emails compra servicios
        // 1 email booking cancelada
        // 2 emails cancelación servicios
        assertEquals(7, EmailHelper.getMock().getSent().size());


    }




    @Test
    public void testModificarLlegadaHora() throws Throwable {

        TransferBooking b = crearReservaConfirmada();

        // modificamos el servicio
        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.getServices().get(0).getPurchaseOrders().get(0).getProvider().setAutomaticOrderSending(false);

            xb.setArrivalFlightTime(xb.getArrivalFlightTime().plusMinutes(5));

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // el 1er servicio ahora NO debe estar confirmado. Debe estar READY puesto que tenemos la P.O. creada
            assertEquals(ProcessingStatus.READY, xb.getServices().get(0).getProcessingStatus());

            // el 2o servicio debe estar confirmado
            assertEquals(ProcessingStatus.CONFIRMED, xb.getServices().get(1).getProcessingStatus());

        });

        EmailHelper.getMock().print();

        // 1 email booking recibida
        // 1 email booking confirmada
        // 2 emails compra servicios
        // 1 email booking confirmada (por la modificación que hemos hecho)
        assertEquals(5, EmailHelper.getMock().getSent().size());

        // enviamos el servicio
        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.getServices().get(0).sendToProvider(em, null, null, null);

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // el 1er servicio ahora NO debe estar confirmado. Debe estar READY puesto que tenemos la P.O. creada
            assertEquals(ProcessingStatus.CONFIRMED, xb.getServices().get(0).getProcessingStatus());

            // el 2o servicio debe estar confirmado
            assertEquals(ProcessingStatus.CONFIRMED, xb.getServices().get(1).getProcessingStatus());

        });

        EmailHelper.getMock().print();

        // 1 email booking recibida
        // 1 email booking confirmada
        // 2 emails compra servicios
        // 1 email booking confirmada (por la modificación que hemos hecho)
        // 1 email servicio reenviado al proveedor
        assertEquals(6, EmailHelper.getMock().getSent().size());


        // modificamos el servicio
        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.getServices().get(0).getPurchaseOrders().get(0).getProvider().setAutomaticOrderConfirmation(false);

            xb.setArrivalFlightTime(xb.getArrivalFlightTime().plusMinutes(5));

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // el 1er servicio ahora NO debe estar confirmado. Debe estar READY puesto que tenemos la P.O. creada
            assertEquals(ProcessingStatus.READY, xb.getServices().get(0).getProcessingStatus());

            // el 2o servicio debe estar confirmado
            assertEquals(ProcessingStatus.CONFIRMED, xb.getServices().get(1).getProcessingStatus());

        });

        EmailHelper.getMock().print();

        // 1 email booking recibida
        // 1 email booking confirmada
        // 2 emails compra servicios
        // 1 email booking confirmada (por la modificación que hemos hecho)
        // 1 email servicio reenviado al proveedor
        // 1 email booking confirmada (por la 2ª modificación que hemos hecho)
        assertEquals(7, EmailHelper.getMock().getSent().size());


        // enviamos el servicio
        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.getServices().get(0).sendToProvider(em, null, null, null);

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // el 1er servicio ahora NO debe estar confirmado. Debe estar READY puesto que tenemos la P.O. creada
            assertEquals(ProcessingStatus.SENT, xb.getServices().get(0).getProcessingStatus());

            // el 2o servicio debe estar confirmado
            assertEquals(ProcessingStatus.CONFIRMED, xb.getServices().get(1).getProcessingStatus());

        });

        EmailHelper.getMock().print();

        // 1 email booking recibida
        // 1 email booking confirmada
        // 2 emails compra servicios
        // 1 email booking confirmada (por la modificación que hemos hecho)
        // 1 email servicio reenviado al proveedor
        // 1 email booking confirmada (por la 2ª modificación que hemos hecho)
        // 1 email servicio reenviado al proveedor
        assertEquals(8, EmailHelper.getMock().getSent().size());


        // confirmamos el servicio
        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.getServices().get(0).getPurchaseOrders().get(0).setStatus(PurchaseOrderStatus.CONFIRMED);

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // el 1er servicio ahora NO debe estar confirmado. Debe estar READY puesto que tenemos la P.O. creada
            assertEquals(ProcessingStatus.CONFIRMED, xb.getServices().get(0).getProcessingStatus());

            // el 2o servicio debe estar confirmado
            assertEquals(ProcessingStatus.CONFIRMED, xb.getServices().get(1).getProcessingStatus());

        });

        EmailHelper.getMock().print();

        // 1 email booking recibida
        // 1 email booking confirmada
        // 2 emails compra servicios
        // 1 email booking confirmada (por la modificación que hemos hecho)
        // 1 email servicio reenviado al proveedor
        // 1 email booking confirmada (por la 2ª modificación que hemos hecho)
        // 1 email servicio reenviado al proveedor
        assertEquals(8, EmailHelper.getMock().getSent().size());

    }

    @Test
    public void testModificarLlegadaVuelo() throws Throwable {

        TransferBooking b = crearReservaConfirmada();

        // modificamos el servicio
        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.getServices().get(0).getPurchaseOrders().get(0).getProvider().setAutomaticOrderSending(false);

            xb.setArrivalFlightNumber(xb.getArrivalFlightNumber() + "x");

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // el 1er servicio ahora NO debe estar confirmado. Debe estar READY puesto que tenemos la P.O. creada
            assertEquals(ProcessingStatus.READY, xb.getServices().get(0).getProcessingStatus());

            // el 2o servicio debe estar confirmado
            assertEquals(ProcessingStatus.CONFIRMED, xb.getServices().get(1).getProcessingStatus());

        });

        EmailHelper.getMock().print();

        // 1 email booking recibida
        // 1 email booking confirmada
        // 2 emails compra servicios
        // 1 email booking confirmada (por la modificación que hemos hecho)
        assertEquals(5, EmailHelper.getMock().getSent().size());

    }

    @Test
    public void testModificarLlegadaOrigen() throws Throwable {

        TransferBooking b = crearReservaConfirmada();

        // modificamos el servicio
        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.getServices().get(0).getPurchaseOrders().get(0).getProvider().setAutomaticOrderSending(false);

            xb.setArrivalFlightOrigin(xb.getArrivalFlightOrigin() + "x");

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // el 1er servicio ahora NO debe estar confirmado. Debe estar READY puesto que tenemos la P.O. creada
            assertEquals(ProcessingStatus.READY, xb.getServices().get(0).getProcessingStatus());

            // el 2o servicio debe estar confirmado
            assertEquals(ProcessingStatus.CONFIRMED, xb.getServices().get(1).getProcessingStatus());

        });

        EmailHelper.getMock().print();

        // 1 email booking recibida
        // 1 email booking confirmada
        // 2 emails compra servicios
        // 1 email booking confirmada (por la modificación que hemos hecho)
        assertEquals(5, EmailHelper.getMock().getSent().size());

    }



    @Test
    public void testModificarSalidaHora() throws Throwable {

        TransferBooking b = crearReservaConfirmada();

        // modificamos el servicio
        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.getServices().get(0).getPurchaseOrders().get(0).getProvider().setAutomaticOrderSending(false);

            xb.setDepartureFlightTime(xb.getDepartureFlightTime().plusMinutes(5));

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // el 1er servicio ahora NO debe estar confirmado. Debe estar READY puesto que tenemos la P.O. creada
            assertEquals(ProcessingStatus.CONFIRMED, xb.getServices().get(0).getProcessingStatus());

            // el 2o servicio debe estar confirmado
            assertEquals(ProcessingStatus.READY, xb.getServices().get(1).getProcessingStatus());

        });

        EmailHelper.getMock().print();

        // 1 email booking recibida
        // 1 email booking confirmada
        // 2 emails compra servicios
        // 1 email booking confirmada (por la modificación que hemos hecho)
        assertEquals(5, EmailHelper.getMock().getSent().size());
    }

    @Test
    public void testModificarSalidaVuelo() throws Throwable {

        TransferBooking b = crearReservaConfirmada();

        // modificamos el servicio
        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.getServices().get(0).getPurchaseOrders().get(0).getProvider().setAutomaticOrderSending(false);

            xb.setDepartureFlightNumber(xb.getDepartureFlightNumber() + "x");

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // el 1er servicio ahora NO debe estar confirmado. Debe estar READY puesto que tenemos la P.O. creada
            assertEquals(ProcessingStatus.CONFIRMED, xb.getServices().get(0).getProcessingStatus());

            // el 2o servicio debe estar confirmado
            assertEquals(ProcessingStatus.READY, xb.getServices().get(1).getProcessingStatus());

        });

        EmailHelper.getMock().print();

        // 1 email booking recibida
        // 1 email booking confirmada
        // 2 emails compra servicios
        // 1 email booking confirmada (por la modificación que hemos hecho)
        assertEquals(5, EmailHelper.getMock().getSent().size());

    }

    @Test
    public void testModificarSalidaDestino() throws Throwable {

        TransferBooking b = crearReservaConfirmada();

        // modificamos el servicio
        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.getServices().get(0).getPurchaseOrders().get(0).getProvider().setAutomaticOrderSending(false);

            xb.setDepartureFlightDestination(xb.getDepartureFlightDestination() + "x");

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // el 1er servicio ahora NO debe estar confirmado. Debe estar READY puesto que tenemos la P.O. creada
            assertEquals(ProcessingStatus.CONFIRMED, xb.getServices().get(0).getProcessingStatus());

            // el 2o servicio debe estar confirmado
            assertEquals(ProcessingStatus.READY, xb.getServices().get(1).getProcessingStatus());

        });

        EmailHelper.getMock().print();

        // 1 email booking recibida
        // 1 email booking confirmada
        // 2 emails compra servicios
        // 1 email booking confirmada (por la modificación que hemos hecho)
        assertEquals(5, EmailHelper.getMock().getSent().size());

    }


    @Test
    public void testModificarLeadName() throws Throwable {

        TransferBooking b = crearReservaConfirmada();

        // modificamos el servicio
        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.getServices().get(0).getPurchaseOrders().get(0).getProvider().setAutomaticOrderSending(false);

            xb.setLeadName(xb.getLeadName() + "x");

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // el 1er servicio ahora NO debe estar confirmado. Debe estar READY puesto que tenemos la P.O. creada
            assertEquals(ProcessingStatus.READY, xb.getServices().get(0).getProcessingStatus());

            // el 2o servicio debe estar confirmado
            assertEquals(ProcessingStatus.READY, xb.getServices().get(1).getProcessingStatus());

        });

        EmailHelper.getMock().print();

        // 1 email booking recibida
        // 1 email booking confirmada
        // 2 emails compra servicios
        // 1 email booking confirmada (por la modificación que hemos hecho)
        assertEquals(5, EmailHelper.getMock().getSent().size());

    }


    @Test
    public void testModificarAdultos() throws Throwable {

        TransferBooking b = crearReservaConfirmada();

        // modificamos el servicio
        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.getServices().get(0).getPurchaseOrders().get(0).getProvider().setAutomaticOrderSending(false);

            xb.setAdults(xb.getAdults() + 1);

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // el 1er servicio ahora NO debe estar confirmado. Debe estar READY puesto que tenemos la P.O. creada
            assertEquals(ProcessingStatus.READY, xb.getServices().get(0).getProcessingStatus());

            // el 2o servicio debe estar confirmado
            assertEquals(ProcessingStatus.READY, xb.getServices().get(1).getProcessingStatus());

        });

        EmailHelper.getMock().print();

        // 1 email booking recibida
        // 1 email booking confirmada
        // 2 emails compra servicios
        // 1 email booking confirmada (por la modificación que hemos hecho)
        assertEquals(5, EmailHelper.getMock().getSent().size());

    }

    @Test
    public void testModificarNinos() throws Throwable {

        TransferBooking b = crearReservaConfirmada();

        // modificamos el servicio
        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.getServices().get(0).getPurchaseOrders().get(0).getProvider().setAutomaticOrderSending(false);

            xb.setChildren(xb.getChildren() + 1);

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // el 1er servicio ahora NO debe estar confirmado. Debe estar READY puesto que tenemos la P.O. creada
            assertEquals(ProcessingStatus.READY, xb.getServices().get(0).getProcessingStatus());

            // el 2o servicio debe estar confirmado
            assertEquals(ProcessingStatus.READY, xb.getServices().get(1).getProcessingStatus());

        });

        EmailHelper.getMock().print();

        // 1 email booking recibida
        // 1 email booking confirmada
        // 2 emails compra servicios
        // 1 email booking confirmada (por la modificación que hemos hecho)
        assertEquals(5, EmailHelper.getMock().getSent().size());

    }


    @Test
    public void testModificarBultos() throws Throwable {

        TransferBooking b = crearReservaConfirmada();

        // modificamos el servicio
        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.getServices().get(0).getPurchaseOrders().get(0).getProvider().setAutomaticOrderSending(false);

            xb.setBigLuggages(xb.getBigLuggages() + 1);

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // el 1er servicio ahora NO debe estar confirmado. Debe estar READY puesto que tenemos la P.O. creada
            assertEquals(ProcessingStatus.READY, xb.getServices().get(0).getProcessingStatus());

            // el 2o servicio debe estar confirmado
            assertEquals(ProcessingStatus.READY, xb.getServices().get(1).getProcessingStatus());

        });

        EmailHelper.getMock().print();

        // 1 email booking recibida
        // 1 email booking confirmada
        // 2 emails compra servicios
        // 1 email booking confirmada (por la modificación que hemos hecho)
        assertEquals(5, EmailHelper.getMock().getSent().size());

    }

    @Test
    public void testModificarGolf() throws Throwable {

        TransferBooking b = crearReservaConfirmada();

        // modificamos el servicio
        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.getServices().get(0).getPurchaseOrders().get(0).getProvider().setAutomaticOrderSending(false);

            xb.setGolf(xb.getGolf() + 1);

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // el 1er servicio ahora NO debe estar confirmado. Debe estar READY puesto que tenemos la P.O. creada
            assertEquals(ProcessingStatus.READY, xb.getServices().get(0).getProcessingStatus());

            // el 2o servicio debe estar confirmado
            assertEquals(ProcessingStatus.READY, xb.getServices().get(1).getProcessingStatus());

        });

        EmailHelper.getMock().print();

        // 1 email booking recibida
        // 1 email booking confirmada
        // 2 emails compra servicios
        // 1 email booking confirmada (por la modificación que hemos hecho)
        assertEquals(5, EmailHelper.getMock().getSent().size());

    }

    @Test
    public void testModificarBicis() throws Throwable {

        TransferBooking b = crearReservaConfirmada();

        // modificamos el servicio
        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.getServices().get(0).getPurchaseOrders().get(0).getProvider().setAutomaticOrderSending(false);

            xb.setBikes(xb.getBikes() + 1);

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // el 1er servicio ahora NO debe estar confirmado. Debe estar READY puesto que tenemos la P.O. creada
            assertEquals(ProcessingStatus.READY, xb.getServices().get(0).getProcessingStatus());

            // el 2o servicio debe estar confirmado
            assertEquals(ProcessingStatus.READY, xb.getServices().get(1).getProcessingStatus());

        });

        EmailHelper.getMock().print();

        // 1 email booking recibida
        // 1 email booking confirmada
        // 2 emails compra servicios
        // 1 email booking confirmada (por la modificación que hemos hecho)
        assertEquals(5, EmailHelper.getMock().getSent().size());

    }

    @Test
    public void testModificarTipoTraslado() throws Throwable {

        TransferBooking b = crearReservaConfirmada();

        // modificamos el servicio
        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.getServices().get(0).getPurchaseOrders().get(0).getProvider().setAutomaticOrderSending(false);

            xb.setTransferType(TransferType.SHUTTLE.equals(xb.getTransferType())?TransferType.PRIVATE:TransferType.SHUTTLE);

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // el 1er servicio ahora NO debe estar confirmado. Debe estar READY puesto que tenemos la P.O. creada
            assertEquals(ProcessingStatus.READY, xb.getServices().get(0).getProcessingStatus());

            // el 2o servicio debe estar confirmado
            assertEquals(ProcessingStatus.READY, xb.getServices().get(1).getProcessingStatus());

        });

        EmailHelper.getMock().print();

        // 1 email booking recibida
        // 1 email booking confirmada
        // 2 emails compra servicios
        // 1 email booking confirmada (por la modificación que hemos hecho)
        assertEquals(5, EmailHelper.getMock().getSent().size());

    }

    @Test
    public void testModificarHoraPickup() throws Throwable {

        TransferBooking b = crearReservaConfirmada();

        // modificamos el servicio
        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.getServices().get(0).getPurchaseOrders().get(0).getProvider().setAutomaticOrderSending(false);

            xb.setOverridePickupTime(xb.getOverridePickupTime() != null?xb.getOverridePickupTime().plusMinutes(5):LocalDateTime.now());

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // el 1er servicio ahora NO debe estar confirmado. Debe estar READY puesto que tenemos la P.O. creada
            assertEquals(ProcessingStatus.CONFIRMED, xb.getServices().get(0).getProcessingStatus());

            // el 2o servicio debe estar confirmado
            assertEquals(ProcessingStatus.READY, xb.getServices().get(1).getProcessingStatus());

        });

        EmailHelper.getMock().print();

        // 1 email booking recibida
        // 1 email booking confirmada
        // 2 emails compra servicios
        // 1 email booking confirmada (por la modificación que hemos hecho)
        assertEquals(5, EmailHelper.getMock().getSent().size());

    }

    @Test
    public void testModificarOrigen() throws Throwable {

        TransferBooking b = crearReservaConfirmada();

        // modificamos el servicio
        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.getServices().get(0).getPurchaseOrders().get(0).getProvider().setAutomaticOrderSending(false);

            xb.setOrigin(Populator.hotelEnAlcudia2);

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // el 1er servicio ahora NO debe estar confirmado. Debe estar READY puesto que tenemos la P.O. creada
            assertEquals(ProcessingStatus.READY, xb.getServices().get(0).getProcessingStatus());

            // el 2o servicio debe estar confirmado
            assertEquals(ProcessingStatus.READY, xb.getServices().get(1).getProcessingStatus());

        });

        EmailHelper.getMock().print();

        // 1 email booking recibida
        // 1 email booking confirmada
        // 2 emails compra servicios
        // 1 email booking confirmada (por la modificación que hemos hecho)
        assertEquals(5, EmailHelper.getMock().getSent().size());

    }

    @Test
    public void testModificarOrigenDireccion() throws Throwable {

        TransferBooking b = crearReservaConfirmada();

        // modificamos el servicio
        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.getServices().get(0).getPurchaseOrders().get(0).getProvider().setAutomaticOrderSending(false);

            xb.setOriginAddress("" + xb.getOriginAddress() + "x");

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // el 1er servicio ahora NO debe estar confirmado. Debe estar READY puesto que tenemos la P.O. creada
            assertEquals(ProcessingStatus.READY, xb.getServices().get(0).getProcessingStatus());

            // el 2o servicio debe estar confirmado
            assertEquals(ProcessingStatus.READY, xb.getServices().get(1).getProcessingStatus());

        });

        EmailHelper.getMock().print();

        // 1 email booking recibida
        // 1 email booking confirmada
        // 2 emails compra servicios
        // 1 email booking confirmada (por la modificación que hemos hecho)
        assertEquals(5, EmailHelper.getMock().getSent().size());

    }

    @Test
    public void testModificarDestino() throws Throwable {

        TransferBooking b = crearReservaConfirmada();

        // modificamos el servicio
        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.getServices().get(0).getPurchaseOrders().get(0).getProvider().setAutomaticOrderSending(false);

            xb.setDestination(Populator.hotelEnAlcudia2);

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // el 1er servicio ahora NO debe estar confirmado. Debe estar READY puesto que tenemos la P.O. creada
            assertEquals(ProcessingStatus.READY, xb.getServices().get(0).getProcessingStatus());

            // el 2o servicio debe estar confirmado
            assertEquals(ProcessingStatus.READY, xb.getServices().get(1).getProcessingStatus());

        });

        EmailHelper.getMock().print();

        // 1 email booking recibida
        // 1 email booking confirmada
        // 2 emails compra servicios
        // 1 email booking confirmada (por la modificación que hemos hecho)
        assertEquals(5, EmailHelper.getMock().getSent().size());

    }

    @Test
    public void testModificarDestinoDireccion() throws Throwable {

        TransferBooking b = crearReservaConfirmada();

        // modificamos el servicio
        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.getServices().get(0).getPurchaseOrders().get(0).getProvider().setAutomaticOrderSending(false);

            xb.setDestinationAddress("" + xb.getDestinationAddress() + "x");

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // el 1er servicio ahora NO debe estar confirmado. Debe estar READY puesto que tenemos la P.O. creada
            assertEquals(ProcessingStatus.READY, xb.getServices().get(0).getProcessingStatus());

            // el 2o servicio debe estar confirmado
            assertEquals(ProcessingStatus.READY, xb.getServices().get(1).getProcessingStatus());

        });

        EmailHelper.getMock().print();

        // 1 email booking recibida
        // 1 email booking confirmada
        // 2 emails compra servicios
        // 1 email booking confirmada (por la modificación que hemos hecho)
        assertEquals(5, EmailHelper.getMock().getSent().size());

    }

    @Test
    public void testModificarComentariosOperaciones() throws Throwable {

        TransferBooking b = crearReservaConfirmada();

        // modificamos el servicio
        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.getServices().get(0).getPurchaseOrders().get(0).getProvider().setAutomaticOrderSending(false);

            xb.getServices().get(0).setOperationsComment("" + xb.getServices().get(0).getOperationsComment() + "x");

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // el 1er servicio ahora NO debe estar confirmado. Debe estar READY puesto que tenemos la P.O. creada
            assertEquals(ProcessingStatus.READY, xb.getServices().get(0).getProcessingStatus());

            // el 2o servicio debe estar confirmado
            assertEquals(ProcessingStatus.CONFIRMED, xb.getServices().get(1).getProcessingStatus());

        });

        EmailHelper.getMock().print();

        // 1 email booking recibida
        // 1 email booking confirmada
        // 2 emails compra servicios
        assertEquals(4, EmailHelper.getMock().getSent().size());

    }

    @Test
    public void testModificarComentariosPrivados() throws Throwable {

        TransferBooking b = crearReservaConfirmada();

        // modificamos el servicio
        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.getServices().get(0).getPurchaseOrders().get(0).getProvider().setAutomaticOrderSending(false);

            xb.setPrivateComments("" + xb.getPrivateComments() + "x");

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // el 1er servicio ahora NO debe estar confirmado. Debe estar READY puesto que tenemos la P.O. creada
            assertEquals(ProcessingStatus.CONFIRMED, xb.getServices().get(0).getProcessingStatus());

            // el 2o servicio debe estar confirmado
            assertEquals(ProcessingStatus.CONFIRMED, xb.getServices().get(1).getProcessingStatus());

        });

        EmailHelper.getMock().print();

        // 1 email booking recibida
        // 1 email booking confirmada
        // 2 emails compra servicios
        assertEquals(4, EmailHelper.getMock().getSent().size());

    }

    @Test
    public void testEnviar() throws Throwable {

        TransferBooking b = crearReservaConfirmada();

        // modificamos el servicio
        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.getServices().get(0).getPurchaseOrders().get(0).getProvider().setAutomaticOrderSending(false);

            xb.setSpecialRequests("" + xb.getSpecialRequests() + "x");

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // el 1er servicio ahora NO debe estar confirmado. Debe estar READY puesto que tenemos la P.O. creada
            assertEquals(ProcessingStatus.READY, xb.getServices().get(0).getProcessingStatus());

            // el 2o servicio debe estar confirmado
            assertEquals(ProcessingStatus.READY, xb.getServices().get(1).getProcessingStatus());

        });


        // modificamos el servicio
        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.getServices().get(0).sendToProvider(em, null, null, null);

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // el 1er servicio ahora NO debe estar confirmado. Debe estar READY puesto que tenemos la P.O. creada
            assertEquals(ProcessingStatus.CONFIRMED, xb.getServices().get(0).getProcessingStatus());

            // el 2o servicio debe estar confirmado
            assertEquals(ProcessingStatus.READY, xb.getServices().get(1).getProcessingStatus());

        });


        EmailHelper.getMock().print();

        // 1 email booking recibida
        // 1 email booking confirmada
        // 2 emails compra servicios
        // 1 email reserva modificada
        // 1 emails compra servicios forzada
        assertEquals(6, EmailHelper.getMock().getSent().size());

    }


    @Test
    public void testMarcarComoConfirmado() throws Throwable {

        TransferBooking b = crearReservaConfirmada();

        // modificamos el servicio
        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.getServices().get(0).getPurchaseOrders().get(0).getProvider().setAutomaticOrderSending(false);

            xb.setSpecialRequests("" + xb.getSpecialRequests() + "x");

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // el 1er servicio ahora NO debe estar confirmado. Debe estar READY puesto que tenemos la P.O. creada
            assertEquals(ProcessingStatus.READY, xb.getServices().get(0).getProcessingStatus());

            // el 2o servicio debe estar confirmado
            assertEquals(ProcessingStatus.READY, xb.getServices().get(1).getProcessingStatus());

        });


        // modificamos el servicio
        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.getServices().get(0).setAlreadyPurchased(true);

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // el 1er servicio ahora NO debe estar confirmado. Debe estar READY puesto que tenemos la P.O. creada
            assertEquals(ProcessingStatus.CONFIRMED, xb.getServices().get(0).getProcessingStatus());

            // el 2o servicio debe estar confirmado
            assertEquals(ProcessingStatus.READY, xb.getServices().get(1).getProcessingStatus());

        });


        EmailHelper.getMock().print();

        // 1 email booking recibida
        // 1 email booking confirmada
        // 2 emails compra servicios
        // 1 email reserva modificada
        assertEquals(5, EmailHelper.getMock().getSent().size());

    }


    @Test
    public void testModificarMarcadaComoConfirmado() throws Throwable {

        TransferBooking b = crearReservaConfirmada();

        // modificamos el servicio
        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.getServices().get(0).getPurchaseOrders().get(0).getProvider().setAutomaticOrderSending(false);

            xb.setSpecialRequests("" + xb.getSpecialRequests() + "x");

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // el 1er servicio ahora NO debe estar confirmado. Debe estar READY puesto que tenemos la P.O. creada
            assertEquals(ProcessingStatus.READY, xb.getServices().get(0).getProcessingStatus());

            // el 2o servicio debe estar confirmado
            assertEquals(ProcessingStatus.READY, xb.getServices().get(1).getProcessingStatus());

        });


        // modificamos el servicio
        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.getServices().get(0).setAlreadyPurchased(true);

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            // el 1er servicio ahora NO debe estar confirmado. Debe estar READY puesto que tenemos la P.O. creada
            assertEquals(ProcessingStatus.CONFIRMED, xb.getServices().get(0).getProcessingStatus());

            // el 2o servicio debe estar confirmado
            assertEquals(ProcessingStatus.READY, xb.getServices().get(1).getProcessingStatus());

        });

        // modificamos el servicio
        Helper.transact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            xb.getServices().get(0).getPurchaseOrders().get(0).getProvider().setAutomaticOrderSending(false);

            xb.setSpecialRequests("" + xb.getSpecialRequests() + "x");

        });


        // comprobamos
        Helper.notransact(em -> {

            TransferBooking xb = em.find(TransferBooking.class, b.getId());

            assertEquals(false, xb.getServices().get(0).isAlreadyPurchased());

            // el 1er servicio ahora NO debe estar confirmado. Debe estar READY puesto que tenemos la P.O. creada
            assertEquals(ProcessingStatus.READY, xb.getServices().get(0).getProcessingStatus());

            // el 2o servicio debe estar confirmado
            assertEquals(ProcessingStatus.READY, xb.getServices().get(1).getProcessingStatus());

        });



        EmailHelper.getMock().print();

        // 1 email booking recibida
        // 1 email booking confirmada
        // 2 emails compra servicios
        // 1 email reserva modificada
        // 1 email reserva modificada
        assertEquals(6, EmailHelper.getMock().getSent().size());

    }
}
