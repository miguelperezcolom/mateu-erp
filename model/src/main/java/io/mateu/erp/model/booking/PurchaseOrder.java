package io.mateu.erp.model.booking;

import com.google.common.base.Strings;
import io.mateu.erp.model.authentication.Audit;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.mdd.CancelledCellStyleGenerator;
import io.mateu.erp.model.mdd.PurchaseOrderStatusCellStyleGenerator;
import io.mateu.erp.model.mdd.SentCellStyleGenerator;
import io.mateu.erp.model.organization.Office;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.mdd.server.annotations.*;
import io.mateu.ui.mdd.server.annotations.Parameter;
import io.mateu.ui.mdd.server.util.Helper;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import javax.mail.internet.InternetAddress;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Created by miguel on 25/2/17.
 */
@Entity
@Getter
@Setter
public class PurchaseOrder {

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
    @ListColumn
    double total;
    @ManyToOne
    @Required
    Currency currency;

    @Ignored
    private String signature;


    @ManyToMany(mappedBy = "purchaseOrders")
    @Ignored
    private List<Service> services = new ArrayList<>();


    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @OwnedList
    private List<PurchaseOrderLine> lines = new ArrayList<>();

    @Action(name = "Send")
    public void sendFromEditor(EntityManager em) throws Exception {
        send(em);
    }

    @Action(name = "Send")
    public static void sendFromList(EntityManager em, @Selection List<Data> selection, @Parameter(name = "Email") String email) throws Exception {
        for (Data d : selection) {
            PurchaseOrder po = em.find(PurchaseOrder.class, d.get("_id"));
            String a = email;
            if (Strings.isNullOrEmpty(a)) a = po.getProvider().getSendOrdersTo();
            if (!Strings.isNullOrEmpty(a)) po.send(em, a);
        }
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

    public void send(EntityManager em) throws Exception {
        send(em, getProvider().getSendOrdersTo());
    }

    public void send(EntityManager em, String toEmail) throws Exception {
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

        for (Service s : getServices()) {
            if (s.getEffectiveProcessingStatus() < 400) {
                s.setProcessingStatus(ProcessingStatus.PURCHASEORDERS_SENT);
            }
        }

        if (getProvider().isAutomaticOrderConfirmation()) {
            setStatus(PurchaseOrderStatus.CONFIRMED);
            for (Service s : getServices()) {
                if (s.getEffectiveProcessingStatus() < 400) {
                    s.setProcessingStatus(ProcessingStatus.PURCHASEORDERS_CONFIRMED);
                }
            }
        }

    }

    private Map<String,Object> getData() {
        Map<String, Object> d = new HashMap<>();

        d.put("id", getId());
        d.put("provider", getProvider().getName());
        d.put("status", (isCancelled())?"CANCELLED":"ACTIVE");
        d.put("created", getAudit().getCreated().format(DateTimeFormatter.BASIC_ISO_DATE.ISO_DATE_TIME));
        d.put("office", getOffice().getName());

        List<Map<String, Object>> ls = new ArrayList<>();

        for (PurchaseOrderLine l : getLines()) {
            Map<String, Object> x;
            ls.add(x = new HashMap<>());

            x.put("id", l.getId());
            x.put("description", l.getDescription());
            x.put("action", l.getAction());
            x.put("units", l.getUnits());

        }

        d.put("lines", ls);

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

    public void updateLinesFromServices(EntityManager em) {
        for (PurchaseOrderLine l : getLines()) em.remove(l);
        getLines().clear();
        LocalDate d = null;
        for (Service s : getServices()) {
            getLines().addAll(s.toPurchaseLines(em));
            if (s.getStart() != null && (d == null || d.isAfter(s.getStart()))) d = s.getStart();
        }
        if (d != null) setStart(d);
        for (PurchaseOrderLine l : getLines()) l.setOrder(this);
    }

    public void price(EntityManager em) {
        //todo: completar
    }
}
