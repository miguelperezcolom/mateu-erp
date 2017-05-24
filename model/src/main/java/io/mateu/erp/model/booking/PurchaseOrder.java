package io.mateu.erp.model.booking;

import com.google.common.base.Strings;
import io.mateu.erp.model.authentication.Audit;
import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.financials.PurchaseOrderSendingMethod;
import io.mateu.erp.model.mdd.CancelledCellStyleGenerator;
import io.mateu.erp.model.mdd.PurchaseOrderStatusCellStyleGenerator;
import io.mateu.erp.model.mdd.SentCellStyleGenerator;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.util.Constants;
import io.mateu.erp.model.workflow.AbstractTask;
import io.mateu.erp.model.workflow.SendPurchaseOrdersTask;
import io.mateu.erp.model.workflow.TaskStatus;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.mdd.server.annotations.*;
import io.mateu.ui.mdd.server.annotations.Parameter;
import io.mateu.ui.mdd.server.interfaces.WithTriggers;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.shared.ActionType;
import io.mateu.ui.mdd.shared.MDDLink;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;

import javax.mail.internet.InternetAddress;
import javax.persistence.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by miguel on 25/2/17.
 */
@Entity
@Getter
@Setter
public class PurchaseOrder implements WithTriggers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Embedded
    @Ignored
    private Audit audit;

    @SearchFilter
    @ListColumn
    private LocalDate start;

    @ManyToOne
    @Required
    @ListColumn
    private Office office;

    @ManyToOne
    @Required
    @ListColumn
    @SearchFilter
    private Actor provider;

    @ListColumn
    @CellStyleGenerator(CancelledCellStyleGenerator.class)
    private boolean cancelled;

    @Output
    @ListColumn
    @CellStyleGenerator(SentCellStyleGenerator.class)
    private boolean sent;
    @Output
    @ListColumn
    private LocalDateTime sentTime;
    @Output
    @ListColumn
    private LocalDateTime responseTime;

    @Required
    @ListColumn
    @SearchFilter
    @CellStyleGenerator(PurchaseOrderStatusCellStyleGenerator.class)
    private PurchaseOrderStatus status;
    @ListColumn
    private String providerComment;

    private boolean valueOverrided;

    private double overridedValue;

    @Output
    private boolean valued;

    @Output
    @ListColumn
    double total;
    @ManyToOne
    @Required
    Currency currency;

    @Output
    private String priceReport;

    @Ignored
    private String signature;


    @SearchFilter(value="Service Id", field = "id")
    @NotInEditor
    @ManyToMany(mappedBy = "purchaseOrders")
    private List<Service> services = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name="purchaseorder_task",
            joinColumns=@JoinColumn(name="purchaseorders_ID"),
            inverseJoinColumns=@JoinColumn(name="sendingtasks_ID"))
    @SearchFilter(value="Task Id", field = "id")
    @NotInEditor
    private List<SendPurchaseOrdersTask> sendingTasks = new ArrayList<>();


    @Links
    public List<MDDLink> getLinks() {
        List<MDDLink> l = new ArrayList<>();
        l.add(new MDDLink("Tasks", AbstractTask.class, ActionType.OPENLIST, new Data("purchaseOrders.id", getId())));
        l.add(new MDDLink("Services", Service.class, ActionType.OPENLIST, new Data("purchaseOrders.id", getId())));
        return l;
    }


    @Action(name = "Send")
    public void sendFromEditor(EntityManager em) throws Throwable {
        send(em);
    }

    @Action(name = "Send")
    public static void sendFromList(EntityManager em, @Selection List<Data> selection, @Parameter(name = "Email") String email) throws Exception {
        SendPurchaseOrdersTask t = new SendPurchaseOrdersTask();
        t.setStatus(TaskStatus.PENDING);
        t.setMethod(PurchaseOrderSendingMethod.EMAIL);
        t.setAudit(new Audit(em.find(User.class, Constants.SYSTEM_USER_LOGIN)));
        String a = email;
        for (Data d : selection) {
            PurchaseOrder po = em.find(PurchaseOrder.class, d.get("_id"));
            t.getPurchaseOrders().add(po);
            po.getSendingTasks().add(t);
            if (Strings.isNullOrEmpty(a)) a = po.getProvider().getSendOrdersTo();
        }
        t.setTo(a);
        if (!Strings.isNullOrEmpty(a)) em.persist(t);
        t.execute(em, em.find(User.class, Constants.SYSTEM_USER_LOGIN));
    }



    public String createSignature() {
        String s = "error when serializing";
        try {
            Map<String, Object> m = new HashMap<>();
            m.put("provider", getProvider().getName());
            List<String> serviceSignatures = new ArrayList<>();
            for (Service sv : getServices()) {
                serviceSignatures.add(sv.createSignature());
            }
            m.put("serviceSignatures", serviceSignatures);
            s = Helper.toJson(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public void send(EntityManager em) throws Throwable {
        send(em, getProvider().getSendOrdersTo());
    }

    public void send(EntityManager em, String toEmail) throws Throwable {
        if (Strings.isNullOrEmpty(toEmail)) throw new Exception("Email address is missing");
        setSignature(createSignature());
        Email email = new HtmlEmail();
        email.setHostName(getOffice().getEmailHost());
        email.setSmtpPort(getOffice().getEmailPort());
        email.setAuthenticator(new DefaultAuthenticator(getOffice().getEmailUsuario(), getOffice().getEmailPassword()));
        //email.setSSLOnConnect(true);
        email.setFrom(getOffice().getEmailFrom());
        if (!Strings.isNullOrEmpty(getOffice().getEmailCC())) email.getCcAddresses().add(new InternetAddress(getOffice().getEmailCC()));

        String asunto = "Purchase Order";
        String template = AppConfig.get(em).getPurchaseOrderTemplate();

        if (isCancelled()) asunto += " Cancellation";
        if (isSent()) asunto += " Resent";

        email.setSubject(asunto);
        email.setMsg(Helper.freemark(template, getData()));
        email.addTo(toEmail);
        email.send();

        setSent(true);
        setSentTime(LocalDateTime.now());

        if (getProvider().isAutomaticOrderConfirmation()) {
            setStatus(PurchaseOrderStatus.CONFIRMED);
        }
        afterSet(em, false);

    }

    public Map<String,Object> getData() {
        Map<String, Object> d = new HashMap<>();

        d.put("id", getId());
        d.put("provider", getProvider().getName());
        d.put("status", (isCancelled())?"CANCELLED":"ACTIVE");
        d.put("created", getAudit().getCreated().format(DateTimeFormatter.BASIC_ISO_DATE.ISO_DATE_TIME));
        if (getOffice() != null) d.put("office", getOffice().getName());
        d.put("sent", isSent());
        d.put("sentTime", getSentTime());
        d.put("valued", isValued());
        d.put("total", getTotal());

        List<Map<String, Object>> ls = new ArrayList<>();

        List<Service> ss = new ArrayList<>(getServices());

        Collections.sort(ss, new Comparator<Service>() {
            @Override
            public int compare(Service o1, Service o2) {
                LocalDateTime d1 = o1.getStart().atStartOfDay();
                LocalDateTime d2 = o2.getStart().atStartOfDay();
                if (o1 instanceof TransferService) d1 = ((TransferService)o1).getFlightTime();
                if (o2 instanceof TransferService) d2 = ((TransferService)o2).getFlightTime();
                return d1.compareTo(d2);
            }
        });

        for (Service s : ss) {
            ls.add(s.getData());
        }

        d.put("services", ls);

        return d;
    }

    public void cancel(EntityManager em) {
        if (!isCancelled()) {
            if (!isSent()) {
                setCancelled(true);
                setStatus(PurchaseOrderStatus.CONFIRMED);
            } else {
                setCancelled(true);
                setStatus(PurchaseOrderStatus.PENDING);
            }
        }
    }

    public void price(EntityManager em) {
        setValued(false);
        setTotal(0);
        if (isValueOverrided()) {
            setTotal(getOverridedValue());
            setValued(true);
            setPriceReport("Used overrided value");
        }
        else {
            try {
                StringWriter sw = new StringWriter();
                setTotal(rate(em, new PrintWriter(sw)));
                setPriceReport(sw.toString());
                setValued(true);
            } catch (Throwable throwable) {
                setPriceReport("" + throwable.getClass().getName() + ":" + throwable.getMessage());
                throwable.printStackTrace();
            }
        }
    }

    private double rate(EntityManager em, PrintWriter report) throws Throwable {
        double total = 0;
        for (Service s : getServices()) {
            double serviceCost = s.rate(em, false, report);
            total += serviceCost;
        }
        return Helper.roundEuros(total);
    }


    @Action(name = "Price")
    public static void price(EntityManager em, @Selection List<Data> selection) {
        for (Data d : selection) {
            PurchaseOrder po = em.find(PurchaseOrder.class, d.get("_id"));
            try {
                po.price(em);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public void beforeSet(EntityManager em, boolean isNew) throws Throwable {

    }

    @Override
    public void afterSet(EntityManager em, boolean isNew) throws Exception, Throwable {
        for (Service s : getServices()) {
            if (s.getEffectiveProcessingStatus() < 300) {
                s.setProcessingStatus(ProcessingStatus.PURCHASEORDERS_READY);
            }

            if (isSent() && s.getEffectiveProcessingStatus() < 400) {
                s.setProcessingStatus(ProcessingStatus.PURCHASEORDERS_SENT);
                s.setSentToProvider(getSentTime());
            }

            if (PurchaseOrderStatus.REJECTED.equals(getStatus()) && s.getEffectiveProcessingStatus() <= 400) {
                s.setProcessingStatus(ProcessingStatus.PURCHASEORDERS_REJECTED);
            }

            if (PurchaseOrderStatus.CONFIRMED.equals(getStatus()) && s.getEffectiveProcessingStatus() <= 400) {
                s.setProcessingStatus(ProcessingStatus.PURCHASEORDERS_CONFIRMED);
            }
        }
    }

    @Override
    public void beforeDelete(EntityManager em) throws Throwable {

    }

    @Override
    public void afterDelete(EntityManager em) throws Throwable {

    }
}
