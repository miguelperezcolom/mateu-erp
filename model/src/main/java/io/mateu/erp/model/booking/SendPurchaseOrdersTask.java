package io.mateu.erp.model.booking;

import com.google.common.base.Strings;
import freemarker.template.TemplateException;
import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.booking.generic.GenericService;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.financials.PurchaseOrderSendingMethod;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.workflow.AbstractTask;
import io.mateu.ui.mdd.server.util.Helper;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;

import javax.mail.internet.InternetAddress;
import javax.persistence.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by miguel on 28/4/17.
 */
@Entity
@Getter
@Setter
public class SendPurchaseOrdersTask extends AbstractTask {

    @Column(name = "_to")
    private String to;
    private String cc;

    @ManyToOne
    private Office office;

    @ManyToMany(mappedBy = "sendingTasks")
    private List<PurchaseOrder> purchaseOrders = new ArrayList<>();

    @ManyToOne
    private Actor provider;

    private PurchaseOrderSendingMethod method;

    public SendPurchaseOrdersTask() {

    }

    public SendPurchaseOrdersTask(List<PurchaseOrder> purchaseOrders) {
        setPurchaseOrders(purchaseOrders);
    }

    @Override
    public void run(EntityManager em, User user) throws Throwable {
        switch (getMethod()) {
            case EMAIL:

                AppConfig appconfig = AppConfig.get(em);

                Email email = new HtmlEmail();
                email.setHostName((getOffice() != null)?getOffice().getEmailHost():appconfig.getAdminEmailSmtpHost());
                email.setSmtpPort((getOffice() != null)?getOffice().getEmailPort():appconfig.getAdminEmailSmtpPort());
                email.setAuthenticator(new DefaultAuthenticator((getOffice() != null)?getOffice().getEmailUsuario():appconfig.getAdminEmailUser(), (getOffice() != null)?getOffice().getEmailPassword():appconfig.getAdminEmailPassword()));
                //email.setSSLOnConnect(true);
                email.setFrom((getOffice() != null)?getOffice().getEmailFrom():appconfig.getAdminEmailFrom());
                if (!Strings.isNullOrEmpty((getOffice() != null)?getOffice().getEmailCC():appconfig.getAdminEmailCC())) email.getCcAddresses().add(new InternetAddress((getOffice() != null)?getOffice().getEmailCC():appconfig.getAdminEmailCC()));

                email.setSubject("Purchase Orders");
                email.setMsg(getMessage(appconfig));
                email.addTo(getTo());
                email.send();


                break;
            case XMLISLANDBUS:
                break;
                default:throw new Throwable("Unknown method: " + getMethod());
        }
        for (PurchaseOrder po : getPurchaseOrders()) {
            po.setSent(true);
            if (po.getProvider().isAutomaticOrderConfirmation()) po.setStatus(PurchaseOrderStatus.CONFIRMED);
            po.setSentTime(LocalDateTime.now());
            po.afterSet(em, false);
        }
    }

    private String getMessage(AppConfig appconfig) throws IOException, TemplateException {
        Map<String, Object> data = getData();
        System.out.println("data=" + Helper.toJson(data));
        return Helper.freemark(appconfig.getPurchaseOrderTemplate(), data);
    }

    private Map<String,Object> getData() {
        Map<String, Object> d = new HashMap<>();
        List<Map<String, Object>> t = new ArrayList<>();
        List<Map<String, Object>> g = new ArrayList<>();
        for (PurchaseOrder po : getPurchaseOrders()) {
            for (Service s : po.getServices()) {
                Map<String, Object> ds = s.getData();
                ds.put("po", po.getId());
                if (s instanceof TransferService) {
                    ds.put("orderby", ((TransferService) s).getFlightTime());
                } else {
                    ds.put("orderby", s.getStart().atStartOfDay());
                }
                if (s instanceof TransferService) t.add(ds);
                else if (s instanceof GenericService) g.add(ds);
            }
        }
        Collections.sort(t, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                return (o1.get("orderby") != null)?((LocalDateTime)o1.get("orderby")).compareTo(((LocalDateTime)o2.get("orderby"))):-1;
            }
        });
        if (t.size() > 0) d.put("transfers", t);

        Collections.sort(g, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                return (o1.get("orderby") != null)?((LocalDate)o1.get("orderby")).compareTo(((LocalDate)o2.get("orderby"))):-1;
            }
        });
        if (g.size() > 0) d.put("generics", g);

        return d;
    }
}
