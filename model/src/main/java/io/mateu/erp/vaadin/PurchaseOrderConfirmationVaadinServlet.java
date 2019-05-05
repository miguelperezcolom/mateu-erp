package io.mateu.erp.vaadin;


import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.VaadinServlet;
import io.mateu.mdd.vaadinport.vaadin.MDDUI;

import javax.servlet.annotation.WebServlet;
import java.util.Properties;

@WebServlet(urlPatterns = {"/poconfirmation", "/poconfirmation/*"}, name = "PurchaseOrderConfirmationUIServlet", asyncSupported = true, loadOnStartup = 450)
@VaadinServletConfiguration(ui = PurchaseOrderConfirmationUI.class, productionMode = true)
public class PurchaseOrderConfirmationVaadinServlet extends VaadinServlet {

}



