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
import io.mateu.mdd.core.workflow.Task;
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

    @MainSearchFilter
    private String reference;

    @Embedded
    @Output
    private Audit audit;

    @NotNull
    private ServiceType serviceType;

    @MainSearchFilter
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
    @MainSearchFilter
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
    @ManyToOne@NotNull
    private Service service;


    @KPI
    @ListColumn
    @CellStyleGenerator(SentCellStyleGenerator.class)
    @ColumnWidth(68)
    private boolean sent;

    public void setSent(boolean v) {
        this.sent = v;
        if (!v) status = PurchaseOrderStatus.PENDING;
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

    public String createSignature() {
        String s = "error when serializing";
        try {
            Map<String, Object> m = new HashMap<>();
            m.put("provider", getProvider().getName());
            m.put("active", active);
            List<String> serviceSignatures = new ArrayList<>();
            serviceSignatures.add(getService().createSignature());
            m.put("serviceSignatures", serviceSignatures);
            s = Helper.toJson(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public void send(EntityManager em, User u) throws Throwable {
        send(em, u, "", "");
    }

    public void send(EntityManager em, User u, String email, String postscript) throws Throwable {

        if (isActive() || getSendingTasks().size() > 0) {

            SendPurchaseOrdersByEmailTask t = null;

            t = getProvider().createTask(em, this);

            if (!Strings.isNullOrEmpty(email)) t.setTo(email);

            t.setOffice(getOffice());
            t.setProvider(getProvider());
            t.setStatus(TaskStatus.PENDING);
            t.setAudit(new Audit(u));

            t.setPostscript(postscript);


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
        if (isConfirmationNeeded()) {
            String u = (MDD.getApp() != null?MDD.getApp().getBaseUrl():"");
            if (!u.endsWith("/")) u += "/";
            if (u.endsWith("/app/")) u = u.replaceAll("\\/app\\/", "/");
            u +=  "poconfirmation/" + Base64.getEncoder().encodeToString(("" + getId()).getBytes());
            d.put("confirmationUrl", u);
        }
        d.put("total", getTotal());
        d.put("currency", getCurrency().getIsoCode());

        List<Map<String, Object>> ls = new ArrayList<>();

        {
            Map<String, Object> ds = getService().getData();
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

        //huhhih


        getCharges().clear();

        createCharges(em);
    }

    public void createCharges(EntityManager em) {
        if (getService().getTotalCost() != 0) {
            PurchaseCharge c;
            getCharges().add(c = new PurchaseCharge());
            c.setAudit(new Audit(MDD.getCurrentUser()));

            c.setTotal(getService().getTotalCost());
            c.setCurrency(getCurrency());

            c.setText("" + getService());

            c.setProvider(getProvider());

            c.setType(ChargeType.PURCHASE);
            c.setPurchaseOrder(this);

            c.setInvoice(null);

            c.setBillingConcept(getService().getBillingConcept(em));
        }
    }


    @Override
    public String toString() {
        return "" + id + " " + reference + " " + (audit != null?audit.getCreated():"");
    }

    @PrePersist@PreUpdate
    public void pre() {
        LocalDate d = null;
        if (d == null || d.isAfter(getService().getStart())) d = getService().getStart();
        start = d;

        if (currencyExchange == 0) {
            currencyExchange = currency.getExchangeRateToNucs();
        }

        double t = 0;
        boolean v = !isActive() || getService() != null;
        if (isActive()) {
            {
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
            {
                serviceSignatures.add("" + getService().getDescription() + " " + getService().getTotalCost());
            }
            m.put("serviceSignatures", serviceSignatures);
            s = Helper.toJson(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public void markServicesForUpdate() {
        getService().setUpdateRqTime(LocalDateTime.now());
    }

    @PostPersist@PostUpdate
    public void post() throws Exception, Throwable {

        if (updateRqTime != null || priceUpdateRqTime != null) WorkflowEngine.add(new Task() {
            @Override
            public void run() {

                try {
                    Helper.transact(new JPATransaction() {
                        @Override
                        public void run(EntityManager em) throws Throwable {

                            PurchaseOrder po = em.find(PurchaseOrder.class, getId());

                            boolean somethingHappened = false;
                            if (po.getPriceUpdateRqTime() != null) {
                                po.updateCharges(em);
                                po.setPriceSignature(createPriceSignature());
                                po.setPriceUpdateRqTime(null);
                                somethingHappened = true;
                            }

                            if (po.getUpdateRqTime() != null) {
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
