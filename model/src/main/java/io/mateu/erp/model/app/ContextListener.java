package io.mateu.erp.model.app;

import io.mateu.erp.model.email.Pop3Reader;
import io.mateu.erp.model.workflow.TaskRunnerRunnable;
import io.mateu.ui.mdd.server.util.Helper;

import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

/**
 * Created by miguel on 28/4/17.
 */
@WebListener
public class ContextListener implements javax.servlet.ServletContextListener {
    private Thread hiloTaskRunner;
    private Thread hiloPop3Reader;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("****CONTEXTLISTENER****");
        Helper.loadProperties();
        if ("yes".equalsIgnoreCase(System.getProperty("taskrunner")) || "true".equalsIgnoreCase(System.getProperty("taskrunner"))) {
            if (hiloTaskRunner == null) {
                System.out.println("****ARRANCANDO TASKRUNNER****");
                hiloTaskRunner = new Thread(new TaskRunnerRunnable());
                hiloTaskRunner.start();
                System.out.println("****TASKRUNNER ARRANCADO****");
            }
            if (hiloPop3Reader == null) {
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
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        if (hiloTaskRunner != null) hiloTaskRunner.interrupt();
        if (hiloPop3Reader != null) hiloPop3Reader.interrupt();
    }
}
