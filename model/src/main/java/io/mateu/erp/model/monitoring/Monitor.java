package io.mateu.erp.model.monitoring;



import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;

public class Monitor {


    private static Thread hilo;

    public static void main(String... args) {

        System.setProperty("appconf", "/Users/miguel/mateu.properties");

        monitor();

    }

    public static void monitor() {

        if (hilo == null) {

            hilo = new Thread(new Runnable() {
                @Override
                public void run() {

                    try {

                        while (true) {

                            try {
                                Helper.transact(new JPATransaction() {
                                    @Override
                                    public void run(EntityManager em) throws Throwable {

                                        for (Watchdog w : (List<Watchdog>) em.createQuery("select x from " + Watchdog.class.getName() + " x where x.active = true order by x.priority").getResultList()) {

                                            if (w.isActive()) {

                                                System.out.println("checking watchdog " + w.getName());

                                                try {

                                                    w.setLastRun(new Date());
                                                    w.check(em);

                                                    w.setStatus(WatchdogStatus.OK);
                                                    w.setErrors(0);
                                                    w.setLastError("");
                                                    w.setLastStackTrace("");

                                                } catch (Throwable e) {
                                                    e.printStackTrace();

                                                    w.notifyWatchers(em, e);

                                                }

                                            }

                                        }

                                    }
                                });
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }

                            System.out.println("Monitor is going to sleep for a minute");
                            Thread.sleep(60000);

                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    hilo = null;

                }
            });

            hilo.start();

        } else {
            System.out.println("Monitor already running.");
        }

    }

}
