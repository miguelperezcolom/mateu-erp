package io.mateu.erp.model.population;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import io.mateu.erp.model.authentication.Grant;
import io.mateu.erp.model.authentication.Permission;
import io.mateu.erp.model.authentication.USER_STATUS;
import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.product.transfer.Contract;
import io.mateu.ui.core.server.BaseServiceImpl;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;

/**
 * used to populate a database with initial values
 *
 * Created by miguel on 13/9/16.
 */
public class Populator {

    public static void main(String... args) throws Throwable {

        populate();

    }

    public static void populate() throws Throwable {

        System.out.println("Populating database...");


        //authentication
        Helper.transact((JPATransaction) (em)->{

            AppConfig c = new AppConfig();
            c.setId(1);
            c.setXslfoForContract(Resources.toString(Resources.getResource(Contract.class, "contract.xsl"), Charsets.UTF_8));
            c.setXslfoForWorld(Resources.toString(Resources.getResource(Contract.class, "world.xsl"), Charsets.UTF_8));
            c.setXslfoForList(Resources.toString(Resources.getResource(BaseServiceImpl.class, "listing.xsl"), Charsets.UTF_8));
            em.persist(c);


            // create super admin permission
            Permission p = new Permission();
            p.setId(1);
            p.setName("Super admin");
            em.persist(p);


            // create user admin
            User u = new User();
            u.setLogin("admin");
            u.setName("Admin");
            //u.setPassword(Helper.md5("1"));
            u.setPassword("1");
            u.setStatus(USER_STATUS.ACTIVE);
            u.getPermissions().add(p);
            em.persist(u);

        });

        // multilanguage


        System.out.println("Database populated.");

    }
}
