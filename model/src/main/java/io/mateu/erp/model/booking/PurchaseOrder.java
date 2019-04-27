package io.mateu.erp.model.booking;

import com.google.common.base.Strings;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Grid;
import com.vaadin.ui.StyleGenerator;
import io.mateu.erp.model.authentication.ERPUser;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.financials.PurchaseOrderSendingMethod;
import io.mateu.erp.model.invoicing.ChargeType;
import io.mateu.erp.model.invoicing.PurchaseCharge;
import io.mateu.erp.model.mdd.CancelledCellStyleGenerator;
import io.mateu.erp.model.mdd.PurchaseOrderStatusCellStyleGenerator;
import io.mateu.erp.model.mdd.SentCellStyleGenerator;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.partners.Provider;
import io.mateu.erp.model.workflow.SendPurchaseOrdersByEmailTask;
import io.mateu.erp.model.workflow.SendPurchaseOrdersTask;
import io.mateu.erp.model.workflow.TaskResult;
import io.mateu.erp.model.workflow.TaskStatus;
import io.mateu.mdd.core.MDD;
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

    @Version
    private int version;


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
    @ColumnWidth(172)
    @NoChart
    private Provider provider;

    @ListColumn
    @CellStyleGenerator(CancelledCellStyleGenerator.class)
    @ColumnWidth(100)
    private boolean active = true;

    public void setActive(boolean active) {
        this.active = active;
    }

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


    private boolean confirmationNeeded;

    @ListColumn
    private String providerComment;


    @Ignored
    private String signature;

    @Ignored
    private String priceSignature;


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
    @KPI
    private boolean valued;

    @KPI
    private double total;

    @NotNull@KPI@ManyToOne
    private Currency currency;

    @KPI
    private double currencyExchange;

    @KPI
    private double valueInNucs;


    @KPI
    private double balance;


    @Output
    private String priceReport;


    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @UseLinkToListView
    private List<PurchaseCharge> charges = new ArrayList<>();

    @Ignored
    private LocalDateTime updateRqTime = LocalDateTime.now();

    public void setUpdateRqTime(LocalDateTime updateRqTime) {
        this.updateRqTime = updateRqTime;
    }

    @Ignored
    private LocalDateTime priceUpdateRqTime = LocalDateTime.now();

    @Ignored
    private transient String servicesUpdateSignature;


    @Action(order = 0, icon = VaadinIcons.MAP_MARKER)
    public BookingMap map() {
        return new BookingMap(this);
    }

    @Action("Send")
    public void sendFromEditor(UserData user, EntityManager em) throws Throwable {
        send(em, em.find(ERPUser.class, user.getLogin()));
    }

    @Action("Send")
    public static void sendFromList(EntityManager em, Set<PurchaseOrder> selection, String email) throws Exception {
        SendPurchaseOrdersByEmailTask t = new SendPurchaseOrdersByEmailTask();
        t.setStatus(TaskStatus.PENDING);
        t.setMethod(PurchaseOrderSendingMethod.EMAIL);
        t.setAudit(new Audit(em.find(ERPUser.class, Constants.SYSTEM_USER_LOGIN)));
        String a = email;
        for (PurchaseOrder po : selection) {
            t.getPurchaseOrders().add(po);
            po.getSendingTasks().add(t);
            if (Strings.isNullOrEmpty(a)) a = po.getProvider().getSendOrdersTo();
        }
        t.setTo(a);
        if (!Strings.isNullOrEmpty(a)) em.persist(t);
        t.execute(em, em.find(ERPUser.class, Constants.SYSTEM_USER_LOGIN));
    }





    public String createSignature() {
        String s = "error when serializing";
        try {
            Map<String, Object> m = new HashMap<>();
            m.put("provider", getProvider().getName());
            m.put("active", active);
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

            setStatus(PurchaseOrderStatus.PENDING);
            setSent(true);
            setSentTime(LocalDateTime.now());
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
        if (isConfirmationNeeded()) d.put("confirmationUrl", (MDD.getApp() != null?MDD.getApp().getBaseUrl():"") + "/poconfirmation/" + getId());
        d.put("total", getTotal());
        d.put("currency", getCurrency().getIsoCode());

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
            setActive(false);
            if (!isSent()) {
                setStatus(PurchaseOrderStatus.CONFIRMED);
            } else {
                setStatus(PurchaseOrderStatus.PENDING);
            }
        }
    }

    private void updateCharges(EntityManager em) {
        getCharges().clear();

        createCharges(em);
    }

    public void createCharges(EntityManager em) {
        for (Service s : getServices()) {
            PurchaseCharge c;
            getCharges().add(c = new PurchaseCharge());
            c.setAudit(new Audit(MDD.getCurrentUser()));

            c.setTotal(s.getTotalCost());
            c.setCurrency(getCurrency());

            c.setText("" + s);

            c.setProvider(getProvider());

            c.setType(ChargeType.PURCHASE);
            c.setPurchaseOrder(this);

            c.setInvoice(null);

            c.setBillingConcept(s.getBillingConcept(em));
        }
    }


    private double rate(EntityManager em, PrintWriter report) throws Throwable {
        double total = 0;
        if (isActive()) for (Service s : getServices()) if (s.isActive()) {
            double serviceCost = s.getOverridedCostValue();
            if (!s.isCostOverrided()) serviceCost = s.rateCost(em, getProvider(), report);
            total += serviceCost;
        }
        return Helper.roundEuros(total);
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

        if (currencyExchange == 0) {
            currencyExchange = currency.getExchangeRateToNucs();
        }

        double t = 0;
        boolean v = !isActive() || services.size() > 0;
        if (isActive()) {
            for (Service service : services) {
                t += service.getTotalCost();
                v = v && service.isCostValued();
            }
        }
        setTotal(Helper.roundEuros(t));
        setValued(v);

        setValueInNucs(total * currencyExchange);

        setConfirmationNeeded(getProvider() != null && !getProvider().isAutomaticOrderConfirmation());


        if (getSignature() == null || !getSignature().equals(createSignature()) || getServicesUpdateSignature() ==null || !getServicesUpdateSignature().equals(createServicesUpdateSignature())) {
            setUpdateRqTime(LocalDateTime.now());
            if (getSignature() == null || !getSignature().equals(createSignature())) {
                setStatus(PurchaseOrderStatus.PENDING);
            }
        }

        if (getPriceSignature() == null || !getPriceSignature().equals(createPriceSignature())) {
            setPriceUpdateRqTime(LocalDateTime.now());
        }

    }

    @PostLoad
    public void postLoad() {
        setServicesUpdateSignature(createServicesUpdateSignature());
    }

    private String createServicesUpdateSignature() {
        return "" + isActive() + "-" + isSent() + "-" + status;
    }

    private String createPriceSignature() {
        String s = "error when serializing";
        try {
            Map<String, Object> m = new HashMap<>();
            m.put("provider", getProvider().getName());
            m.put("active", active);
            List<String> serviceSignatures = new ArrayList<>();
            for (Service sv : getServices()) {
                serviceSignatures.add("" + sv.getDescription() + " " + sv.getTotalCost());
            }
            m.put("serviceSignatures", serviceSignatures);
            s = Helper.toJson(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public void markServicesForUpdate() {
        getServices().forEach(s -> s.setUpdateRqTime(LocalDateTime.now()));
    }

    @PostPersist@PostUpdate
    public void post() throws Exception, Throwable {

        if (updateRqTime != null || priceUpdateRqTime != null) WorkflowEngine.add(new Runnable() {
            @Override
            public void run() {

                try {
                    Helper.transact(new JPATransaction() {
                        @Override
                        public void run(EntityManager em) throws Throwable {

                            PurchaseOrder po = em.merge(PurchaseOrder.this);

                            boolean somethingHappened = false;
                            if (po.getPriceUpdateRqTime() != null) {
                                po.updateCharges(em);
                                po.setPriceSignature(createPriceSignature());
                                po.setPriceUpdateRqTime(null);
                                somethingHappened = true;
                            }

                            if (po.getUpdateRqTime() != null) {
                                po.summarize(em);

                                po.markServicesForUpdate();

                                po.setSignature(po.createSignature());
                                po.setServicesUpdateSignature(po.createServicesUpdateSignature());
                                po.setUpdateRqTime(null);

                                somethingHappened = true;
                            }

                            if (somethingHappened) {
                                po.getProvider().setUpdatePending(true);
                            }

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

    }

    private void updateStatusFromTasks(EntityManager em) {
        if (sendingTasks.size() > 0) {
            SendPurchaseOrdersTask t = sendingTasks.get(sendingTasks.size() - 1);
            if (t.getSignature() != null && t.getSignature().equals(t.createSignature())) {
                if (TaskStatus.FINISHED.equals(t.getStatus()) && TaskResult.OK.equals(t.getResult())) {
                    if (getProvider().isAutomaticOrderConfirmation()) setStatus(PurchaseOrderStatus.CONFIRMED);
                    setSentTime(t.getFinished());
                    setSent(true);
                }
            } else {
                setStatus(PurchaseOrderStatus.PENDING);
                setSent(false);
                setSentTime(null);
            }
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
