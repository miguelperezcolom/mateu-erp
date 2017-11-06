package io.mateu.erp.servlets;

import io.mateu.erp.model.util.Helper;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

@WebServlet(urlPatterns = "/resources/*", loadOnStartup = 1, initParams = {
        @WebInitParam(name = "javax.ws.rs.Application", value = "io.mateu.erp.services.MyApplication")
})
public class JerseyServlet extends ServletContainer {
}
