package io.mateu.erp.model.population;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import io.mateu.mdd.core.model.common.Resource;
import io.mateu.mdd.core.model.config.TemplateUseCase;
import io.mateu.mdd.core.model.multilanguage.Literal;
import io.mateu.mdd.core.model.util.Constants;
import io.mateu.erp.model.accounting.AccountingPlan;
import io.mateu.erp.model.financials.*;
import io.mateu.erp.model.organization.Company;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.product.hotel.BoardType;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.erp.model.product.hotel.RoomType;
import io.mateu.erp.model.product.transfer.Contract;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.erp.model.product.transfer.TransferPointType;
import io.mateu.erp.model.world.Country;
import io.mateu.erp.model.world.Destination;
import io.mateu.erp.model.world.Zone;
import io.mateu.mdd.core.model.authentication.Permission;
import io.mateu.mdd.core.model.authentication.USER_STATUS;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * used to populate a database with initial values
 *
 * Created by miguel on 13/9/16.
 */
public class Populator extends io.mateu.mdd.core.model.population.Populator {

    public static final String USER_ADMIN = "admin";

    public static void main(String... args) throws Throwable {

        new Populator().populate(io.mateu.erp.model.config.AppConfig.class);

    }

    public void populate(Class appConfigClass) throws Throwable {

        System.out.println("Populating database...");


        //authentication
        Helper.transact((JPATransaction) (em)->{

            Map<String, Object> initialData = null;
            String initialDataPath = System.getProperty("initialdata", "/home/miguel/work/initialdata.yml");
            try {
                initialData = Helper.fromYaml(Files.toString(new java.io.File(initialDataPath), Charset.defaultCharset()));
            } catch (Exception e) {
                System.out.println("No initial data found at " + initialDataPath);
            }


            io.mateu.erp.model.config.AppConfig c = (io.mateu.erp.model.config.AppConfig) appConfigClass.newInstance();
            c.setId(1);

            c.setXslfoForTransferContract(Resources.toString(Resources.getResource("/io/mateu/erp/xsl/contrato_transfer.xsl"), Charsets.UTF_8));
            c.setXslfoForHotelContract(Resources.toString(Resources.getResource("/io/mateu/erp/xsl/contrato_hotel.xsl"), Charsets.UTF_8));
            c.setXslfoForGenericContract(Resources.toString(Resources.getResource("/io/mateu/erp/xsl/contrato_generico.xsl"), Charsets.UTF_8));
            c.setXslfoForTourContract(Resources.toString(Resources.getResource("/io/mateu/erp/xsl/contrato_tour.xsl"), Charsets.UTF_8));
            c.setXslfoForWorld(Resources.toString(Resources.getResource("/io/mateu/erp/xsl/world.xsl"), Charsets.UTF_8));
            c.setXslfoForList(Resources.toString(Resources.getResource("/xsl/listing.xsl"), Charsets.UTF_8));
            c.setXslfoForIssuedInvoice(Resources.toString(Resources.getResource("/io/mateu/erp/xsl/factura.xsl"), Charsets.UTF_8));
            c.setXslfoForPurchaseOrder(Resources.toString(Resources.getResource("/io/mateu/erp/xsl/please_book.xsl"), Charsets.UTF_8));
            c.setXslfoForTransfersList(Resources.toString(Resources.getResource("/io/mateu/erp/xsl/transfers_list.xsl"), Charsets.UTF_8));
            c.setXslfoForVoucher(Resources.toString(Resources.getResource("/io/mateu/erp/xsl/voucher.xsl"), Charsets.UTF_8));
            c.setXslfoForObject(Resources.toString(Resources.getResource("/xsl/object.xsl"), Charsets.UTF_8));

            c.setPickupEmailTemplate(Resources.toString(Resources.getResource("/io/mateu/erp/freemarker/pickupemail.ftl"), Charsets.UTF_8));
            c.setPickupSmsTemplate(Resources.toString(Resources.getResource("/io/mateu/erp/freemarker/pickupsms.ftl"), Charsets.UTF_8));
            c.setPickupSmsTemplateEs(Resources.toString(Resources.getResource("/io/mateu/erp/freemarker/pickupsmses.ftl"), Charsets.UTF_8));
            c.setPurchaseOrderTemplate(Resources.toString(Resources.getResource("/io/mateu/erp/freemarker/purchaseorder.ftl"), Charsets.UTF_8));


            c.setAdminEmailSmtpHost((String) Helper.get(initialData, "smtp/host"));
            c.setAdminEmailFrom((String) Helper.get(initialData, "smtp/user"));
            c.setAdminEmailPassword((String) Helper.get(initialData, "smtp/password"));
            c.setAdminEmailSmtpPort((Integer) Helper.get(initialData, "smtp/port", 0));
            c.setAdminEmailUser((String) Helper.get(initialData, "smtp/user"));
            c.setAdminEmailCC((String) Helper.get(initialData, "smtp/cc"));


            em.persist(c);

            c.createDummyDates();


            // create super admin permission
            int pid = 1;
            Permission p = new Permission();
            p.setId(pid++);
            p.setName("Super admin");
            em.persist(p);


            {
                Permission px = new Permission();
                px.setId(pid++);
                px.setName("Financial");
                em.persist(px);
            }
            {
                Permission px = new Permission();
                px.setId(pid++);
                px.setName("Booking");
                em.persist(px);
            }
            {
                Permission px = new Permission();
                px.setId(pid++);
                px.setName("Operations");
                em.persist(px);
            }
            {
                Permission px = new Permission();
                px.setId(pid++);
                px.setName("Portfolio");
                em.persist(px);
            }
            {
                Permission px = new Permission();
                px.setId(pid++);
                px.setName("Biz");
                em.persist(px);
            }
            {
                Permission px = new Permission();
                px.setId(pid++);
                px.setName("CMS");
                em.persist(px);
            }
            {
                Permission px = new Permission();
                px.setId(pid++);
                px.setName("Management");
                em.persist(px);
            }
            {
                Permission px = new Permission();
                px.setId(pid++);
                px.setName("Utils");
                em.persist(px);
            }


            {
                // create user admin
                io.mateu.erp.model.authentication.User u = new io.mateu.erp.model.authentication.User();
                u.setLogin(USER_ADMIN);
                u.setName("Admin");
                //u.setPassword(Helper.md5("1"));
                u.setEmail("miguelperezcolom@gmail.com");
                u.setPassword("1");
                u.setStatus(USER_STATUS.ACTIVE);
                u.getPermissions().add(p);
                Resource f;
                u.setPhoto(f = new Resource());
                f.setName("foto-perfil-ejemplo.png");
                f.setBytes(ByteStreams.toByteArray(Populator.class.getResourceAsStream("/images/" + f.getName())));
                em.persist(f);
                em.persist(u);
            }

            {
                // create user admin
                io.mateu.erp.model.authentication.User u = new io.mateu.erp.model.authentication.User();
                u.setLogin(Constants.SYSTEM_USER_LOGIN);
                u.setName("System");
                //u.setPassword(Helper.md5("1"));
                u.setEmail("miguelperezcolom@gmail.com");
                u.setPassword("1");
                u.setStatus(USER_STATUS.ACTIVE);
                u.getPermissions().add(p);
                Resource f;
                u.setPhoto(f = new Resource());
                f.setName("foto-perfil-ejemplo.png");
                f.setBytes(ByteStreams.toByteArray(Populator.class.getResourceAsStream("/images/" + f.getName())));
                em.persist(f);
                em.persist(u);
            }

            {
                // create user admin
                io.mateu.erp.model.authentication.User u = new io.mateu.erp.model.authentication.User();
                u.setLogin(Constants.IMPORTING_USER_LOGIN);
                u.setName("Importing User");
                //u.setPassword(Helper.md5("1"));
                u.setEmail("miguelperezcolom@gmail.com");
                u.setPassword("1");
                u.setStatus(USER_STATUS.ACTIVE);
                u.getPermissions().add(p);
                Resource f;
                u.setPhoto(f = new Resource());
                f.setName("foto-perfil-ejemplo.png");
                f.setBytes(ByteStreams.toByteArray(Populator.class.getResourceAsStream("/images/" + f.getName())));
                em.persist(f);
                em.persist(u);
            }


            {
                TemplateUseCase tuc = new TemplateUseCase();
                tuc.setName("User");
                em.persist(tuc);
            }

            {
                TemplateUseCase tuc = new TemplateUseCase();
                tuc.setName("Partner");
                em.persist(tuc);
            }

            {
                TemplateUseCase tuc = new TemplateUseCase();
                tuc.setName("Booking");
                em.persist(tuc);
            }


            Currency eur;
            if (false) {
                eur = new Currency();
                eur.setIsoCode("EUR");
                eur.setIsoNumericCode(978);
                eur.setName("Euro");
                em.persist(eur);
            }

            if (false) {
                Currency usd = new Currency();
                usd.setIsoCode("USD");
                usd.setIsoNumericCode(840);
                usd.setName("US Dollar");
                em.persist(usd);
            }



            if (false) {
                PointOfSale pos = new PointOfSale();
                pos.setName("Point of sale");
                em.persist(pos);
            }

            if (false) {
                BillingConcept bc = new BillingConcept();
                bc.setName("Anything");
                bc.setCode("ANY");
                bc.setLocalizationRule(LocalizationRule.ISSUING_COMPANY);
                em.persist(bc);
            }

            /*
            Zone ct;
            TransferPoint apt;
            {
                Country co = new Country();
                co.setIsoCode("UNMAPPED");
                co.setName("UNMAPPED");
                em.persist(co);

                Destination s;
                co.getDestinations().add(s = new Destination());
                s.setCountry(co);
                s.setName("UNMAPPED");
                em.persist(s);

                s.getZones().add(ct = new Zone());
                ct.setDestination(s);
                ct.setName("UNMAPPED");
                em.persist(ct);

                ct.getTransferPoints().add(apt = new TransferPoint());
                apt.setZone(ct);
                apt.setName("DEFAULT AIRPORT");
                apt.setType(TransferPointType.AIRPORT);
                em.persist(apt);


                RoomType r = new RoomType();
                r.setCode("UNMAPPED");
                r.setName(new Literal("UNMAPPED", "UNMAPPED"));
                em.persist(r);

                BoardType b = new BoardType();
                b.setCode("UNMAPPED");
                b.setName(new Literal("UNMAPPED", "UNMAPPED"));
                em.persist(b);

            }
            */


            if (false) {

                AccountingPlan plan = new AccountingPlan();
                plan.setName("Accounting plan");
                plan.setCurrency(eur);
                em.persist(plan);

                FinancialAgent a = new FinancialAgent();
                a.setName("We.inc");
                a.setAutomaticInvoiceBasis(AutomaticInvoiceBasis.NONE);
                a.setInvoiceGrouping(InvoiceGrouping.BOOKING);
                a.setRiskType(RiskType.CREDIT);
                a.setCurrency(eur);
                em.persist(a);

                Company b = new Company();
                b.setName("We");
                b.setFinancialAgent(a);
                b.setAccountingPlan(plan);
                em.persist(b);

                /*
                Office o = new Office();
                o.setName("Head office");
                o.setCity(ct);
                o.setCurrency(eur);
                o.setDefaultAirportForTransfers(apt);
                o.setCompany(b);
                em.persist(o);
                */

            }

        });

        // multilanguage


        System.out.println("Database populated.");

    }
}
