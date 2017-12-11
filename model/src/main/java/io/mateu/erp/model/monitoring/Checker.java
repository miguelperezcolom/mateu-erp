package io.mateu.erp.model.monitoring;

import com.google.common.base.Strings;
import io.mateu.erp.model.util.Helper;
import io.mateu.erp.model.util.JPATransaction;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;

public class Checker {

    public static void run() {

        boolean dentro = true;

        while (dentro) {

            try {
                Helper.transact(new JPATransaction() {
                    @Override
                    public void run(EntityManager em) throws Throwable {

                        for (Watchdog wd : (List<Watchdog>) em.createQuery("select x from " + Watchdog.class.getName() + " x where x.active").getResultList()) {
                            if (wd.isActive()) {
                                wd.setLastRun(new Date());
                                try {
                                    wd.check(em);

                                    wd.setErrors(0);
                                    wd.setStatus(WatchdogStatus.OK);
                                } catch (Throwable t) {
                                    t.printStackTrace();

                                    wd.setStatus(WatchdogStatus.ERROR);
                                    for (Watcher w : wd.getNotifyTo()) {
                                        if (!Strings.isNullOrEmpty(w.getEmails())) {

                                        }
                                    }
                                }

                            }
                        }

                    }
                });
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

            try {
                Thread.sleep(60000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
                dentro = false;
            }

        }

    }

}
