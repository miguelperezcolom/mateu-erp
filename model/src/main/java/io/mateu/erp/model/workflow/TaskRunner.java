package io.mateu.erp.model.workflow;

import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.importing.TransferImportQueue;
import io.mateu.erp.model.util.Constants;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by miguel on 28/4/17.
 */
public class TaskRunner implements Runnable {
    @Override
    public void run() {
        while (true) {
            iterate();
        }
    }

    private void iterate() {
        try {

            Helper.transact(new JPATransaction() {
                @Override
                public void run(EntityManager em) throws Throwable {

                    List<AbstractTask> l = em.createQuery("select x from " + AbstractTask.class.getName() + " x where x.status = :s order by x.id").setParameter("s", TaskStatus.PENDING).getResultList();

                    if (l.size() > 0) {
                        AbstractTask t = l.get(0);
                        t.execute(em, em.find(User.class, Constants.SYSTEM_USER_LOGIN));
                    } else {
                        Thread.sleep(100);
                    }

                }
            });


        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {
            TransferImportQueue.run();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }


    public static void main(String... args) {
        new TaskRunner().iterate();
    }
}
