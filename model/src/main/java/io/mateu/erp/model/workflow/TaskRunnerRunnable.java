package io.mateu.erp.model.workflow;

import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.importing.TransferBookingRequest;
import io.mateu.erp.model.importing.TransferImportQueue;
import io.mateu.mdd.core.model.util.Constants;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by miguel on 28/4/17.
 */
public class TaskRunnerRunnable implements Runnable {

    long pausaMs = 100;

    @Override
    public void run() {

        try {
            pausaMs = Long.parseLong(System.getProperty("taskrunnerpausams", "10000"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

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
                        Thread.sleep(pausaMs);
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

        try {
            TransferBookingRequest.run();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }


    public static void main(String... args) {
        new TaskRunnerRunnable().iterate();
    }
}
