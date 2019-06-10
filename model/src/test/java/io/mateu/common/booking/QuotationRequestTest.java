package io.mateu.common.booking;

import com.google.common.collect.Lists;
import io.mateu.erp.model.authentication.ERPUser;
import io.mateu.erp.model.booking.QuotationRequest;
import io.mateu.erp.model.booking.QuotationRequestLine;
import io.mateu.erp.model.config.AppConfig;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class QuotationRequestTest {

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

        QuotationRequest r = crearPeticion(false);

        // comprobamos que no se ha enviado un email
        assertEquals(0, EmailHelper.getMock().getSent().size());

    }

    @Test
    public void testEnviar() throws Throwable {

        EmailHelper.getMock().reset();

        QuotationRequest r = crearPeticion(true);

        Helper.transact(em -> {
            QuotationRequest rx = em.find(QuotationRequest.class, r.getId());
            rx.sendEmail(null, "miguelperezcolom@gmail.com", "Test QR", "Hola!", true);
        });

        EmailHelper.getMock().print();

        // comprobamos que no se ha enviado un email
        assertEquals(1, EmailHelper.getMock().getSent().size());

    }

    @Test
    public void testConfirmar() throws Throwable {

        EmailHelper.getMock().reset();

        QuotationRequest r = crearPeticion(true);

        Helper.transact(em -> {
            em.find(QuotationRequest.class, r.getId()).confirm();
        });

        Helper.transact(em -> {
            QuotationRequest rx = em.find(QuotationRequest.class, r.getId());

            assertNotNull(rx.getFile());

            assertEquals(2, rx.getFile().getBookings().size());
        });

        EmailHelper.getMock().print();
        // comprobamos que se han enviado los emails (2 reservas confirmadas y 2 pedidos a proveedor)
        assertEquals(4, EmailHelper.getMock().getSent().size());

    }

    private QuotationRequest crearPeticion(boolean rellenarLineas) throws Throwable {

        QuotationRequest r = new QuotationRequest();

        Helper.transact(em -> {

            r.setActive(true);
            r.setGroupType(Populator.groupType);
            r.setAgency(Populator.agencia);
            r.setAudit(new Audit(MDD.getCurrentUser()));
            r.setCurrency(Populator.agencia.getCurrency());
            r.setEmail("miguelperezcolom@gmail.com");
            r.setOptionDate(LocalDate.now().plusDays(5));
            r.setName("Miguel");
            r.setPos(Populator.pos);
            if (rellenarLineas) {
                List<QuotationRequestLine> lineas = new ArrayList<>();
                {
                    QuotationRequestLine l;
                    lineas.add(l = new QuotationRequestLine());
                    l.setRq(r);
                    l.setActive(true);
                    l.setCost(300);
                    l.setStart(LocalDate.of(2019, 6, 1));
                    l.setEnd(LocalDate.of(2019, 6, 8));
                    l.setPrice(450);
                    l.setText("Habitación doble superior en Hotel Lago Park");
                    l.setProvider(Populator.proveedor);
                    l.setBillingConcept(AppConfig.get(em).getBillingConceptForHotel());
                    l.setOffice(Populator.office);
                    l.setProductLine(Populator.prodLine);
                    l.setUnits(3);
                }
                {
                    QuotationRequestLine l;
                    lineas.add(l = new QuotationRequestLine());
                    l.setRq(r);
                    l.setActive(true);
                    l.setCost(30);
                    l.setStart(LocalDate.of(2019, 6, 1));
                    l.setEnd(LocalDate.of(2019, 6, 8));
                    l.setPrice(60);
                    l.setText("Traslado llegada y salida");
                    l.setProvider(Populator.proveedor);
                    l.setBillingConcept(AppConfig.get(em).getBillingConceptForTransfer());
                    l.setOffice(Populator.office);
                    l.setProductLine(Populator.prodLine);
                    l.setUnits(3);
                }
                r.setLines(lineas);
            }
            //r.setLines();
            r.setTelephone("45646464646");
            r.setText("bla bla bla bla");
            r.setPrivateComments("Test");
            r.setTitle("Presupuesto test xxx");

            em.persist(r);

        });

        return r;
    }


}
