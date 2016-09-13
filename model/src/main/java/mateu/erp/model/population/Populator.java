package mateu.erp.model.population;

import mateu.erp.model.authentication.User;
import mateu.erp.model.util.Helper;
import mateu.erp.model.util.Transaction;

import javax.persistence.EntityManager;

/**
 * Created by miguel on 13/9/16.
 */
public class Populator {

    public static void main(String... args) {

        Helper.transact(new Transaction() {
            public void run(EntityManager em) throws Exception {

                User u = new User();


            }
        });

    }
}
