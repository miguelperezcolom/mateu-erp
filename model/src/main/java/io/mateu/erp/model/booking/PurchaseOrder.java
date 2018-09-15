package io.mateu.erp.model.booking;

import com.google.common.base.Strings;
import io.mateu.erp.model.financials.Amount;
import io.mateu.erp.model.invoicing.PurchaseCharge;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.User;
import io.mateu.mdd.core.model.util.Constants;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.financials.PurchaseOrderSendingMethod;
import io.mateu.erp.model.invoicing.Charge;
import io.mateu.erp.model.mdd.CancelledCellStyleGenerator;
import io.mateu.erp.model.mdd.PurchaseOrderStatusCellStyleGenerator;
import io.mateu.erp.model.mdd.SentCellStyleGenerator;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.workflow.AbstractTask;
import io.mateu.erp.model.workflow.SendPurchaseOrdersByEmailTask;
import io.mateu.erp.model.workflow.SendPurchaseOrdersTask;
import io.mateu.erp.model.workflow.TaskStatus;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.app.ActionType;
import io.mateu.mdd.core.app.MDDLink;
import io.mateu.mdd.core.data.Data;
import io.mateu.mdd.core.data.UserData;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;
import io.mateu.mdd.core.workflow.WorkflowEngine;
import lombok.Getter;
import lombok.Setter;
import org.javamoney.moneta.FastMoney;

import javax.persistence.*;
import javax.persistence.Parameter;
import javax.validation.constraints.NotNull;
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
public class PurchaseOrder {

    @Transient
    @Ignored
    private boolean preventAfterSet;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SearchFilter
    private long id;

    private String reference;

    @Tab("Info")
    @Embedded
    @Ignored
    private Audit audit;

    @Ignored
    @NotNull
    private ServiceType serviceType;

    @SearchFilter
    @ListColumn
    private LocalDate start;

    @ManyToOne
    @NotNull
    @ListColumn
    @ColumnWidth(172)
    private Office office;

    @ManyToOne
    @NotNull
    @ListColumn
    @SearchFilter
    @QLFilter("x.provider = true")
    @ColumnWidth(172)
    private Partner provider;

    @ListColumn
    @CellStyleGenerator(CancelledCellStyleGenerator.class)
    @ColumnWidth(100)
    private boolean cancelled;

    private String comment;

    @Tab("Delivering")
    @Output
    @ListColumn
    @CellStyleGenerator(SentCellStyleGenerator.class)
    @ColumnWidth(68)
    private boolean sent;

    public void setSent(boolean v) {
        this.sent = v;
    }

    @Output
    @ListColumn
    private LocalDateTime sentTime;
    @Output
    @ListColumn
    private LocalDateTime responseTime;

    @NotNull
    @ListColumn
    @SearchFilter
    @CellStyleGenerator(PurchaseOrderStatusCellStyleGenerator.class)
    @ColumnWidth(150)
    private PurchaseOrderStatus status;
    @ListColumn
    private String providerComment;


    @Output
    private String signature;

    @Tab("Price")
    private boolean valueOverrided;

    private double overridedValue;

    @Ignored
    private String overridedValueCalculator;


    @Output
    private boolean valued;


    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="value", column=@Column(name="value_value"))
            , @AttributeOverride(name="date", column=@Column(name="value_date"))
            , @AttributeOverride(name="officeChangeRate", column=@Column(name="value_offchangerate"))
            , @AttributeOverride(name="officeValue", column=@Column(name="value_offvalue"))
            , @AttributeOverride(name="nucChangeRate", column=@Column(name="value_nuchangerate"))
            , @AttributeOverride(name="nucValue", column=@Column(name="value_nucvalue"))
    })
    @AssociationOverrides({
            @AssociationOverride(name="currency", joinColumns = @JoinColumn(name = "value_currency"))
    })
    @KPI
    @NotWhenCreating
    @ListColumn
    private Amount value;


    @Output
    private String priceReport;


    @Tab("Charges")
    @OneToMany(mappedBy = "purchaseOrder")
    @Output
    private List<PurchaseCharge> charges = new ArrayList<>();


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


    @Action("Send")
    public void sendFromEditor(UserData user, EntityManager em) throws Throwable {
        send(em, em.find(io.mateu.erp.model.authentication.User.class, user.getLogin()));
    }

    @Action("Send")
    public static void sendFromList(EntityManager em, Set<PurchaseOrder> selection, String email) throws Exception {
        SendPurchaseOrdersByEmailTask t = new SendPurchaseOrdersByEmailTask();
        t.setStatus(TaskStatus.PENDING);
        t.setMethod(PurchaseOrderSendingMethod.EMAIL);
        t.setAudit(new Audit(em.find(io.mateu.erp.model.authentication.User.class, Constants.SYSTEM_USER_LOGIN)));
        String a = email;
        for (PurchaseOrder po : selection) {
            t.getPurchaseOrders().add(po);
            po.getSendingTasks().add(t);
            if (Strings.isNullOrEmpty(a)) a = po.getProvider().getSendOrdersTo();
        }
        t.setTo(a);
        if (!Strings.isNullOrEmpty(a)) em.persist(t);
        t.execute(em, em.find(io.mateu.erp.model.authentication.User.class, Constants.SYSTEM_USER_LOGIN));
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

    public void send(EntityManager em, User u) throws Throwable {

        setSignature(createSignature());

        if (!isCancelled() || getSendingTasks().size() > 0) {

            SendPurchaseOrdersTask t = null;

            t = getProvider().createTask(em, this);

            t.setOffice(getOffice());
            t.setProvider(getProvider());
            t.setStatus(TaskStatus.PENDING);
            t.setAudit(new Audit(u));

            t.setPostscript("");


            t.getPurchaseOrders().add(this);
            getSendingTasks().add(t);

            em.persist(t);

        }

    }

    public Map<String,Object> getData() {
        Map<String, Object> d = new HashMap<>();

        d.put("id", getId());
        d.put("reference", getReference());
        d.put("provider", getProvider().getName());
        d.put("status", (isCancelled())?"CANCELLED":"ACTIVE");
        d.put("created", getAudit().getCreated().format(DateTimeFormatter.BASIC_ISO_DATE.ISO_DATE_TIME));
        if (getOffice() != null) d.put("office", getOffice().getName());
        d.put("sent", isSent());
        d.put("sentTime", getSentTime());
        d.put("valued", isValued());
        d.put("total", getValue());

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
            Map<String, Object> ds = s.getData();
            if (isCancelled()) ds.put("status", "CANCELLED");
            ls.add(ds);
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

    public void price(EntityManager em) throws Throwable {
        boolean v = false;
        double t = 0;
        if (isValueOverrided()) {
            t = getOverridedValue();
            v = true;
            setPriceReport("Used overrided value");
        }
        else {
            try {
                StringWriter sw = new StringWriter();
                t = rate(em, new PrintWriter(sw));
                setPriceReport(sw.toString());
                v = true;
            } catch (Throwable throwable) {
                String error = "" + throwable.getClass().getName() + ":" + throwable.getMessage();
                if (!error.startsWith("java.lang.Throwable") && !error.startsWith("java.lang.Exception")) throwable.printStackTrace();
                else error = error.substring(error.indexOf(":"));
                System.out.println(error);
                setPriceReport(error);
            }
        }
        setValued(v);
        getValue().setValue(t);
        getValue().setCurrency(getProvider().getCurrency());
    }

    private double rate(EntityManager em, PrintWriter report) throws Throwable {
        double total = 0;
        if (!isCancelled()) for (Service s : getServices()) if (!s.isCancelled()) {
            double serviceCost = s.getOverridedCostValue();
            if (!s.isCostOverrided()) serviceCost = s.rate(em, false, getProvider(), report);
            total += serviceCost;
        }
        return Helper.roundEuros(total);
    }


    @Action
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



    @PostPersist@PostUpdate
    public void afterSet() throws Exception, Throwable {

        long finalId = getId();

        if (!isPreventAfterSet()) {
            WorkflowEngine.add(new Runnable() {
                @Override
                public void run() {

                    try {
                        Helper.transact(new JPATransaction() {
                            @Override
                            public void run(EntityManager em) throws Throwable {
                                em.find(PurchaseOrder.class, finalId).afterSet(em);
                            }
                        });
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }

                }
            });
        }


    }

    public void afterSet(EntityManager em) throws Exception, Throwable {

        System.out.println("po " + getId() + ".afterset");

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
        try {
            price(em);
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }
}
