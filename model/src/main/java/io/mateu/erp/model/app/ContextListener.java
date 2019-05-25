package io.mateu.erp.model.app;

import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.email.Pop3Reader;
import io.mateu.erp.model.workflow.TaskRunnerRunnable;
import io.mateu.mdd.core.model.util.EmailHelper;
import io.mateu.mdd.core.util.Helper;

import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;
import java.time.LocalDateTime;

/**
 * Created by miguel on 28/4/17.
 */
@WebListener
public class ContextListener implements javax.servlet.ServletContextListener {
    private Thread hiloLogger;
    private Thread hiloTaskRunner;
    private Thread hiloPop3Reader;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("****CONTEXTLISTENER****");
        Helper.loadProperties();

        try {
            Helper.notransact(em -> {
                System.out.println("AppConfig.get(em).getBusinessName()=" + AppConfig.get(em).getBusinessName());
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        if ("yes".equalsIgnoreCase(System.getProperty("taskrunner")) || "true".equalsIgnoreCase(System.getProperty("taskrunner"))) {
            if (hiloTaskRunner == null) {
                System.out.println("****ARRANCANDO TASKRUNNER****");
                hiloTaskRunner = new Thread(new TaskRunnerRunnable());
                hiloTaskRunner.start();
                System.out.println("****TASKRUNNER ARRANCADO****");
            }
            if (hiloPop3Reader == null && !EmailHelper.isTesting()) {
                System.out.println("****ARRANCANDO POP3READER****");
                hiloPop3Reader = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            new Pop3Reader().read();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                hiloPop3Reader.start();
                System.out.println("****POP3READER ARRANCADO****");
            }
        }

        if (hiloLogger == null) {
            System.out.println("****ARRANCANDO HILOLOGGER****");
            hiloLogger = new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean dentro = true;
                    while (dentro) {
                        try {
                            logarEstado();
                            Thread.sleep(60000l);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            dentro = false;
                        }
                    }
                    System.out.println("****HILOLOGGER DETENIDO****");
                }
            });
            hiloLogger.start();
            System.out.println("****HILOLOGGER ARRANCADO****");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        if (hiloTaskRunner != null) hiloTaskRunner.interrupt();
        if (hiloPop3Reader != null) hiloPop3Reader.interrupt();
        if (hiloLogger != null) hiloLogger.interrupt();
    }

    private void logarEstado() {
        int mb = 1024*1024;

        //Getting the runtime reference from system
        Runtime runtime = Runtime.getRuntime();

        System.out.println("##### Heap utilization statistics [MB] ##### " + LocalDateTime.now());

        //Print used memory
        System.out.println("Used Memory:"
                + (runtime.totalMemory() - runtime.freeMemory()) / mb);

        //Print free memory
        System.out.println("Free Memory:"
                + runtime.freeMemory() / mb);

        //Print total available memory
        System.out.println("Total Memory:" + runtime.totalMemory() / mb);

        //Print Maximum available memory
        System.out.println("Max Memory:" + runtime.maxMemory() / mb);


        //Print memory line for analisys
        System.out.println("XXXFP " + LocalDateTime.now()
                + " " + (runtime.totalMemory() - runtime.freeMemory()) / mb
                + " " + runtime.freeMemory() / mb
                + " " + runtime.totalMemory() / mb
                + " " + runtime.maxMemory() / mb);

    }
}
