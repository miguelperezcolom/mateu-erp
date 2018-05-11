package io.mateu.erp.servlets;

import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

@WebServlet(urlPatterns = "/resources/*", loadOnStartup = 1, initParams = {
        @WebInitParam(name = "javax.ws.rs.Application", value = "io.mateu.common.services.MyApplication")
})
public class JerseyServlet extends ServletContainer {
}
