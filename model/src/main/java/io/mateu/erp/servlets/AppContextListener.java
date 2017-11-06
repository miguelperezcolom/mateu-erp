package io.mateu.erp.servlets;

import io.mateu.erp.model.util.Helper;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {


        System.out.println("Trying to load properties...");
        Helper.loadProperties();
        System.out.println("Properties loaded");

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
