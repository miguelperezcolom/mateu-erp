package io.mateu.erp.vaadin;

import com.google.common.base.Strings;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Viewport;
import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import io.mateu.erp.model.booking.PurchaseOrder;
import io.mateu.erp.model.booking.PurchaseOrderStatus;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.workflow.SendPurchaseOrdersTask;
import io.mateu.mdd.core.CSS;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.util.Helper;

import java.net.URL;
import java.util.Map;

@Theme("mytheme")
@StyleSheet("https://use.fontawesome.com/releases/v5.5.0/css/all.css")
@Viewport("width=device-width, initial-scale=1")
@PushStateNavigation // para urls sin #!
@PreserveOnRefresh
public class PurchaseOrderConfirmationUI extends UI {
    @Override
    protected void init(VaadinRequest vaadinRequest) {


        String url = ((VaadinServletRequest)vaadinRequest).getHttpServletRequest().getRequestURL().toString();
        String uri = ((VaadinServletRequest)vaadinRequest).getHttpServletRequest().getRequestURI();

        String contextUrl = url.substring(0, url.length() - uri.length());
        contextUrl += ((VaadinServletRequest)vaadinRequest).getHttpServletRequest().getContextPath();
        if (!contextUrl.endsWith("/")) contextUrl += "/";
        String sp = ((VaadinServletRequest)vaadinRequest).getHttpServletRequest().getServletPath();
        if (sp.startsWith("/")) sp = sp.substring(1);
        contextUrl += sp;
        if (!contextUrl.endsWith("/")) contextUrl += "/";

        if (Strings.isNullOrEmpty(System.getProperty("tmpurl"))) {
            System.setProperty("tmpurl", contextUrl + "tmp");
            System.setProperty("tmpdir", ((VaadinServletRequest)vaadinRequest).getHttpServletRequest().getServletContext().getRealPath("/tmp/"));
        }

        setContent(createContent(uri));

    }

    private Component createContent(String uri) {

        VerticalLayout vl = new VerticalLayout();

        long poId = 0;

        try {

            if (!Strings.isNullOrEmpty(uri)) {

                String[] ts = uri.split("/");

                if (ts.length > 1 && "poconfirmation".equalsIgnoreCase(ts[ts.length - 2])) poId = Long.parseLong(ts[ts.length - 1]);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        if (poId > 0) {

            try {
                long finalPoId = poId;
                Helper.notransact(em -> {

                    AppConfig appconfig = AppConfig.get(em);

                    SendPurchaseOrdersTask t = em.find(SendPurchaseOrdersTask.class, finalPoId);

                    if (t != null) {

                        if (t.getPurchaseOrders().size() == 0) {

                            Label l;
                            vl.addComponent(l = new Label("Invalid purchase order id."));
                            l.addStyleName(ValoTheme.LABEL_H1);
                            l.addStyleName(CSS.REDFGD);


                        } else if (PurchaseOrderStatus.CONFIRMED.equals(t.getPurchaseOrders().get(0).getStatus())) {

                            Label l;
                            vl.addComponent(l = new Label("This purchase order has already been confirmed."));
                            l.addStyleName(ValoTheme.LABEL_H1);
                            l.addStyleName(CSS.REDFGD);

                        } else if (PurchaseOrderStatus.REJECTED.equals(t.getPurchaseOrders().get(0).getStatus())) {

                            Label l;
                            vl.addComponent(l = new Label("This purchase order has already been rejected."));
                            l.addStyleName(ValoTheme.LABEL_H1);
                            l.addStyleName(CSS.REDFGD);

                        } else {

                            Map<String, Object> data = t.getData();
                            data.put("confirming", true);
                            System.out.println("data=" + Helper.toJson(data));

                            String msg = Helper.freemark(AppConfig.get(em).getPurchaseOrderTemplate(), data);

                            if (msg.contains("mylogosrc") && appconfig.getLogo() != null) {
                                URL url = new URL(appconfig.getLogo().toFileLocator().getUrl());
                                String cid = url.toString(); //email.embed(url, "" + appconfig.getBusinessName() + " logo");
                                msg = msg.replaceAll("mylogosrc", cid); //"cid:" + cid);
                            }


                            Label l;
                            vl.addComponent(l = new Label(msg, ContentMode.HTML));

                            HorizontalLayout hl;
                            vl.addComponent(hl = new HorizontalLayout());


                            Button b;
                            hl.addComponent(b = new Button("This service is valid"));
                            b.addStyleName(ValoTheme.BUTTON_FRIENDLY);
                            b.addClickListener(e -> {

                                vl.removeAllComponents();

                                vl.addComponent(new Label("Your locator for this service:"));

                                TextField ta;
                                vl.addComponent(ta = new TextField());
                                ta.focus();


                                Button bx;
                                vl.addComponent(bx = new Button("Confirm this service"));
                                bx.addStyleName(ValoTheme.BUTTON_FRIENDLY);
                                bx.addClickListener(ex -> {

                                    try {
                                        Helper.transact(emx -> {

                                            SendPurchaseOrdersTask tx = em.find(SendPurchaseOrdersTask.class, finalPoId);

                                            tx.getPurchaseOrders().get(0).setStatus(PurchaseOrderStatus.CONFIRMED);
                                            if (!Strings.isNullOrEmpty(ta.getValue())) {
                                                tx.getPurchaseOrders().get(0).setReference(ta.getValue());
                                            }

                                        });

                                        vl.removeAllComponents();

                                        Label lx;
                                        vl.addComponent(lx = new Label("Done. Thanks for your cooperation ;)"));
                                        lx.addStyleName(ValoTheme.LABEL_H1);

                                    } catch (Throwable throwable) {
                                        Label lx;
                                        vl.addComponent(lx = new Label("ERROR: " + throwable.getClass().getSimpleName() + "... " + throwable.getMessage()));
                                        lx.addStyleName(ValoTheme.LABEL_H1);
                                        lx.addStyleName(CSS.REDFGD);
                                    }


                                });

                            });

                            hl.addComponent(b = new Button("This service is NOT valid"));
                            b.addStyleName(ValoTheme.BUTTON_DANGER);
                            b.addClickListener(e -> {

                                vl.removeAllComponents();

                                vl.addComponent(new Label("Reason:"));

                                TextArea ta;
                                vl.addComponent(ta = new TextArea());
                                ta.focus();


                                Button bx;
                                vl.addComponent(bx = new Button("Reject this service"));
                                bx.addStyleName(ValoTheme.BUTTON_DANGER);
                                bx.addClickListener(ex -> {

                                    try {
                                        Helper.transact(emx -> {

                                            SendPurchaseOrdersTask tx = em.find(SendPurchaseOrdersTask.class, finalPoId);

                                            tx.getPurchaseOrders().get(0).setStatus(PurchaseOrderStatus.REJECTED);
                                            if (!Strings.isNullOrEmpty(ta.getValue())) {
                                                tx.getPurchaseOrders().get(0).setProviderComment(ta.getValue());
                                            }

                                        });

                                        vl.removeAllComponents();

                                        Label lx;
                                        vl.addComponent(lx = new Label("Done. Thanks for your cooperation ;)"));
                                        lx.addStyleName(ValoTheme.LABEL_H1);

                                    } catch (Throwable throwable) {
                                        Label lx;
                                        vl.addComponent(lx = new Label("ERROR: " + throwable.getClass().getSimpleName() + "... " + throwable.getMessage()));
                                        lx.addStyleName(ValoTheme.LABEL_H1);
                                        lx.addStyleName(CSS.REDFGD);
                                    }

                                });
                            });

                        }


                    } else {

                        Label l;
                        vl.addComponent(l = new Label("Invalid purchase order id."));
                        l.addStyleName(ValoTheme.LABEL_H1);
                        l.addStyleName(CSS.REDFGD);

                    }

                });
            } catch (Throwable throwable) {
                Label l;
                vl.addComponent(l = new Label("ERROR: " + throwable.getClass().getSimpleName() + "... " + throwable.getMessage()));
                l.addStyleName(ValoTheme.LABEL_H1);
                l.addStyleName(CSS.REDFGD);
            }

        } else {

            Label l;
            vl.addComponent(l = new Label("Invalid purchase order id."));
            l.addStyleName(ValoTheme.LABEL_H1);
            l.addStyleName(CSS.REDFGD);

        }


        return vl;

    }

    @Override
    protected void refresh(VaadinRequest request) {
        super.refresh(request);
        String state = getPage().getLocation().getPath();
        if (state.startsWith("/")) state = state.substring(1);
        if (state.startsWith("app")) state = state.substring("app".length());
        if (state.startsWith("/")) state = state.substring(1);
        System.out.println("MDDUI.refresh: new state = " + state);
        setContent(createContent(state));
    }

}
