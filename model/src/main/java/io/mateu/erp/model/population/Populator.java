package io.mateu.erp.model.population;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;
import io.mateu.common.model.authentication.Permission;
import io.mateu.common.model.authentication.USER_STATUS;
import io.mateu.common.model.common.File;
import io.mateu.erp.model.accounting.AccountingPlan;
import io.mateu.erp.model.financials.*;
import io.mateu.common.model.multilanguage.Literal;
import io.mateu.erp.model.organization.Company;
import io.mateu.erp.model.product.hotel.BoardType;
import io.mateu.erp.model.product.hotel.RoomType;
import io.mateu.common.model.util.Constants;
import io.mateu.erp.model.world.Zone;
import io.mateu.erp.model.world.Country;
import io.mateu.erp.model.world.Destination;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.erp.model.product.transfer.Contract;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.erp.model.product.transfer.TransferPointType;
import io.mateu.ui.core.server.BaseServiceImpl;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;

/**
 * used to populate a database with initial values
 *
 * Created by miguel on 13/9/16.
 */
public class Populator {

    public static final String USER_ADMIN = "admin";

    public static void main(String... args) throws Throwable {

        populate(io.mateu.erp.model.config.AppConfig.class);

    }

    public static void populate(Class appConfigClass) throws Throwable {

        System.out.println("Populating database...");


        //authentication
        Helper.transact((JPATransaction) (em)->{

            io.mateu.erp.model.config.AppConfig c = (io.mateu.erp.model.config.AppConfig) appConfigClass.newInstance();
            c.setId(1);
            c.setXslfoForTransferContract(Resources.toString(Resources.getResource(Contract.class, "contract.xsl"), Charsets.UTF_8));
            c.setXslfoForHotelContract(Resources.toString(Resources.getResource(Hotel.class, "contract.xsl"), Charsets.UTF_8));
            c.setXslfoForWorld(Resources.toString(Resources.getResource(Contract.class, "portfolio.xsl"), Charsets.UTF_8));
            c.setXslfoForList(Resources.toString(Resources.getResource(BaseServiceImpl.class, "listing.xsl"), Charsets.UTF_8));
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
                u.setEmail("miguelperezclom@gmail.com");
                u.setPassword("1");
                u.setStatus(USER_STATUS.ACTIVE);
                u.getPermissions().add(p);
                File f;
                u.setPhoto(f = new File());
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
                u.setEmail("miguelperezclom@gmail.com");
                u.setPassword("1");
                u.setStatus(USER_STATUS.ACTIVE);
                u.getPermissions().add(p);
                em.persist(u);
            }

            {
                // create user admin
                io.mateu.erp.model.authentication.User u = new io.mateu.erp.model.authentication.User();
                u.setLogin(Constants.IMPORTING_USER_LOGIN);
                u.setName("Importing User");
                //u.setPassword(Helper.md5("1"));
                u.setEmail("miguelperezclom@gmail.com");
                u.setPassword("1");
                u.setStatus(USER_STATUS.ACTIVE);
                u.getPermissions().add(p);
                em.persist(u);
            }

            Currency eur;
            {
                eur = new Currency();
                eur.setIsoCode("EUR");
                eur.setIso4217Code("978");
                eur.setName("Euro");
                em.persist(eur);
            }

            {
                Currency usd = new Currency();
                usd.setIsoCode("USD");
                usd.setIso4217Code("840");
                usd.setName("US Dollar");
                em.persist(usd);
            }



            {
                PointOfSale pos = new PointOfSale();
                pos.setName("Point of sale");
                em.persist(pos);
            }

            {
                BillingConcept bc = new BillingConcept();
                bc.setName("Anything");
                bc.setCode("ANY");
                bc.setLocalizationRule(LocalizationRule.ISSUING_COMPANY);
                em.persist(bc);
            }

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


            {

                AccountingPlan plan = new AccountingPlan();
                plan.setName("Accounting plan");
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

                Office o = new Office();
                o.setName("Head office");
                o.setCity(ct);
                o.setCurrency(eur);
                o.setDefaultAirportForTransfers(apt);
                o.setCompany(b);
                em.persist(o);
            }

        });

        // multilanguage


        System.out.println("Database populated.");

    }
}
