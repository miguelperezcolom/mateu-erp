package io.mateu.erp.model.booking;

import com.google.common.base.Strings;
import com.vaadin.ui.Grid;
import com.vaadin.ui.StyleGenerator;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.financials.Amount;
import io.mateu.erp.model.financials.PurchaseOrderSendingMethod;
import io.mateu.erp.model.invoicing.PurchaseCharge;
import io.mateu.erp.model.mdd.CancelledCellStyleGenerator;
import io.mateu.erp.model.mdd.PurchaseOrderStatusCellStyleGenerator;
import io.mateu.erp.model.mdd.SentCellStyleGenerator;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.workflow.SendPurchaseOrdersByEmailTask;
import io.mateu.erp.model.workflow.SendPurchaseOrdersTask;
import io.mateu.erp.model.workflow.TaskStatus;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.data.UserData;
import io.mateu.mdd.core.interfaces.GridDecorator;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.User;
import io.mateu.mdd.core.model.util.Constants;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;
import io.mateu.mdd.core.workflow.WorkflowEngine;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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
@UseIdToSelect
public class PurchaseOrder {

    @Transient
    @Ignored
    private boolean preventAfterSet;

    @Section("Info")

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SearchFilter
    private long id;

    private String reference;

    @Embedded
    @Output
    private Audit audit;

    @NotNull
    private ServiceType serviceType;

    @SearchFilter
    @ListColumn
    @Output
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
    @NoChart
    private Partner provider;

    @ListColumn
    @CellStyleGenerator(CancelledCellStyleGenerator.class)
    @ColumnWidth(100)
    private boolean active = true;

    @TextArea
    private String comment;



    @SearchFilter(field = "id")
    @UseLinkToListView
    @ManyToMany(mappedBy = "purchaseOrders")
    @Caption("Service Id")
    private List<Service> services = new ArrayList<>();


    @KPI
    @ListColumn
    @CellStyleGenerator(SentCellStyleGenerator.class)
    @ColumnWidth(68)
    private boolean sent;

    public void setSent(boolean v) {
        this.sent = v;
    }

    @Section("Delivering")
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


    @Ignored
    private String signature;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name="purchaseorder_task",
            joinColumns=@JoinColumn(name="purchaseorders_ID"),
            inverseJoinColumns=@JoinColumn(name="sendingtasks_ID"))
    @SearchFilter(field = "id")
    @Caption("Task Id")
    @OrderColumn(name = "_orderInPO")
    @UseLinkToListView
    private List<SendPurchaseOrdersTask> sendingTasks = new ArrayList<>();


    @Section("Price")
    private boolean valueOverrided;

    @SameLine
    private double overridedValue;

    @Ignored
    private String overridedValueCalculator;




    @KPI
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


    @OneToMany(mappedBy = "purchaseOrder")
    @UseLinkToListView
    private List<PurchaseCharge> charges = new ArrayList<>();

    @Ignored
    private boolean updatePending = true;



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

        if (isActive() || getSendingTasks().size() > 0) {

            SendPurchaseOrdersTask t = null;

            t = getProvider().createTask(em, this);

            t.setOffice(getOffice());
            t.setProvider(getProvider());
            t.setStatus(TaskStatus.PENDING);
            t.setAudit(new Audit(u));

            t.setPostscript("");


            t.getPurchaseOrders().add(this);
            getSendingTasks().add(t);

        }

    }

    public Map<String,Object> getData() {
        Map<String, Object> d = new HashMap<>();

        d.put("id", getId());
        d.put("reference", getReference());
        d.put("provider", getProvider().getName());
        d.put("status", (!isActive())?"CANCELLED":"ACTIVE");
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
            if (!isActive()) ds.put("status", "CANCELLED");
            ls.add(ds);
        }

        d.put("services", ls);

        return d;
    }

    public void cancel(EntityManager em) {
        if (isActive()) {
            if (!isSent()) {
                setActive(false);
                setStatus(PurchaseOrderStatus.CONFIRMED);
            } else {
                setActive(false);
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
        if (isActive()) for (Service s : getServices()) if (s.isActive()) {
            double serviceCost = s.getOverridedCostValue();
            if (!s.isCostOverrided()) serviceCost = s.rate(em, false, getProvider(), report);
            total += serviceCost;
        }
        return Helper.roundEuros(total);
    }


    @Action
    public static void price(EntityManager em, Set<PurchaseOrder> selection) {
        for (PurchaseOrder po : selection) {
            try {
                po.price(em);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public String toString() {
        return "" + id + " " + reference + " " + (audit != null?audit.getCreated():"");
    }

    @PrePersist@PreUpdate
    public void pre() {
        LocalDate d = null;
        for (Service s : services) if (d == null || d.isAfter(s.getStart())) d = s.getStart();
        start = d;
    }

    @PostPersist@PostUpdate
    public void post() throws Exception, Throwable {

        if (updatePending) WorkflowEngine.add(new Runnable() {
            @Override
            public void run() {

                try {
                    if (updatePending) Helper.transact(new JPATransaction() {
                        @Override
                        public void run(EntityManager em) throws Throwable {
                            PurchaseOrder po = em.merge(PurchaseOrder.this);
                            try {
                                po.price(em);
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                            po.summarize(em);



                            po.setUpdatePending(false);
                        }
                    });
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

            }
        });

    }

    public void summarize(EntityManager em) {
        System.out.println("PO " + getId() + ".summarize");

        updateStatusFromTasks(em);

        try {
            summarizeServices(em);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private void updateStatusFromTasks(EntityManager em) {
        if (sendingTasks.size() > 0) {
            SendPurchaseOrdersTask t = sendingTasks.get(sendingTasks.size() - 1);
            if (TaskStatus.FINISHED.equals(t.getStatus())) {
                setSent(true);
                if (getProvider().isAutomaticOrderConfirmation()) setStatus(PurchaseOrderStatus.CONFIRMED);
                setSentTime(t.getFinished());
            } else {
                setSent(false);
                if (getProvider().isAutomaticOrderConfirmation()) setStatus(PurchaseOrderStatus.CONFIRMED);
                setSentTime(t.getFinished());
            }
        }
    }

    public void summarizeServices(EntityManager em) throws Exception, Throwable {

        System.out.println("po " + getId() + ".summarizeServices");

        for (Service s : getServices()) {
            s.summarize(em);
        }

    }



    public static GridDecorator getGridDecorator() {
        return new GridDecorator() {
            @Override
            public void decorateGrid(Grid grid) {
                grid.getColumns().forEach(col -> {

                    StyleGenerator old = ((Grid.Column) col).getStyleGenerator();

                    ((Grid.Column)col).setStyleGenerator(new StyleGenerator() {
                        @Override
                        public String apply(Object o) {
                            String s = null;
                            if (old != null) s = old.apply(o);

                            if (o instanceof PurchaseOrder) {
                                if (!((PurchaseOrder)o).isActive()) s = (s != null)?s + " cancelled":"cancelled";
                            } else {
                                if (!((Boolean)((Object[])o)[5])) {
                                    s = (s != null)?s + " cancelled":"cancelled";
                                }
                            }
                            return s;
                        }
                    });
                });
            }
        };
    }
}
