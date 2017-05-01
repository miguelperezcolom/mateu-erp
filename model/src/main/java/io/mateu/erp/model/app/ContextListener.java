package io.mateu.erp.model.app;

import io.mateu.erp.model.workflow.TaskRunner;

import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

/**
 * Created by miguel on 28/4/17.
 */
@WebListener
public class ContextListener implements javax.servlet.ServletContextListener {
    private Thread hiloTaskRunner;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        hiloTaskRunner = new Thread(new TaskRunner());
        hiloTaskRunner.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        hiloTaskRunner.interrupt();
    }
}
