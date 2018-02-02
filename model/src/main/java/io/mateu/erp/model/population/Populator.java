package io.mateu.erp.model.population;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;
import io.mateu.erp.model.authentication.Permission;
import io.mateu.erp.model.authentication.USER_STATUS;
import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.common.File;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.financials.LocalizationRule;
import io.mateu.erp.model.multilanguage.Literal;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.product.hotel.BoardType;
import io.mateu.erp.model.product.hotel.RoomType;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.erp.model.product.transfer.Contract;
import io.mateu.erp.model.util.Constants;
import io.mateu.erp.model.world.City;
import io.mateu.erp.model.world.Country;
import io.mateu.erp.model.world.State;
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

        populate(AppConfig.class);

    }

    public static void populate(Class appConfigClass) throws Throwable {

        System.out.println("Populating database...");


        //authentication
        Helper.transact((JPATransaction) (em)->{

            AppConfig c = (AppConfig) appConfigClass.newInstance();
            c.setId(1);
            c.setXslfoForTransferContract(Resources.toString(Resources.getResource(Contract.class, "contract.xsl"), Charsets.UTF_8));
            c.setXslfoForHotelContract(Resources.toString(Resources.getResource(HotelContract.class, "contract.xsl"), Charsets.UTF_8));
            c.setXslfoForWorld(Resources.toString(Resources.getResource(Contract.class, "portfolio.xsl"), Charsets.UTF_8));
            c.setXslfoForList(Resources.toString(Resources.getResource(BaseServiceImpl.class, "listing.xsl"), Charsets.UTF_8));
            em.persist(c);

            c.createDummyDates();


            // create super admin permission
            Permission p = new Permission();
            p.setId(1);
            p.setName("Super admin");
            em.persist(p);


            {
                // create user admin
                User u = new User();
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
                User u = new User();
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
                User u = new User();
                u.setLogin(Constants.IMPORTING_USER_LOGIN);
                u.setName("Importing User");
                //u.setPassword(Helper.md5("1"));
                u.setEmail("miguelperezclom@gmail.com");
                u.setPassword("1");
                u.setStatus(USER_STATUS.ACTIVE);
                u.getPermissions().add(p);
                em.persist(u);
            }


            {
                Currency eur = new Currency();
                em.persist(eur);
                eur.setIsoCode("EUR");
                eur.setIso4217Code("978");
                eur.setName("Euro");
                eur.setDecimals(2);
                em.persist(eur);
            }

            {
                Currency usd = new Currency();
                em.persist(usd);
                usd.setIsoCode("USD");
                usd.setIso4217Code("840");
                usd.setName("US Dollar");
                usd.setDecimals(2);
                em.persist(usd);
            }

            {
                Office o = new Office();
                o.setName("Head office");
                em.persist(o);
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

            {
                Country co = new Country();
                em.persist(co);
                co.setIsoCode("UNMAPPED");
                co.setName("UNMAPPED");

                State s;
                co.getStates().add(s = new State());
                em.persist(s);
                s.setCountry(co);
                s.setName("UNMAPPED");

                City ct;
                s.getCities().add(ct = new City());
                em.persist(ct);
                ct.setState(s);
                ct.setName("UNMAPPED");


                RoomType r = new RoomType();
                em.persist(r);
                r.setCode("UNMAPPED");
                r.setName(new Literal("UNMAPPED", "UNMAPPED"));

                BoardType b = new BoardType();
                em.persist(b);
                b.setCode("UNMAPPED");
                b.setName(new Literal("UNMAPPED", "UNMAPPED"));

            }

        });

        // multilanguage


        System.out.println("Database populated.");

    }
}
