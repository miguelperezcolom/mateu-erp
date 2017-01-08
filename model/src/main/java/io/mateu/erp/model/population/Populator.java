package io.mateu.erp.model.population;

import io.mateu.erp.model.authentication.Grant;
import io.mateu.erp.model.authentication.Permission;
import io.mateu.erp.model.authentication.USER_STATUS;
import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.util.Helper;
import io.mateu.erp.model.util.JPATransaction;

import java.util.Date;

/**
 * used to populate a database with initial values
 *
 * Created by miguel on 13/9/16.
 */
public class Populator {

    public static void main(String... args) throws Exception {

        System.out.println("Populating database...");


        //authentication
        Helper.transact((JPATransaction) (em)->{

            // create super admin permission
            Permission p = new Permission();
            p.setId(1);
            p.setName("Super admin");
            em.persist(p);

            // create user admin
            User u = new User();
            u.setLogin("ADMIN");
            u.setPassword(Helper.md5("1"));
            u.setCreated(new Date());
            u.setStatus(USER_STATUS.ACTIVE);
            u.getGrants().add(new Grant(u, p));
            em.persist(u);

        });

        // multilanguage


        System.out.println("Database populated.");

    }
}
