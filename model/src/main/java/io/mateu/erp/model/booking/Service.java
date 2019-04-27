package io.mateu.erp.model.booking;

import com.google.common.base.Strings;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Grid;
import com.vaadin.ui.StyleGenerator;
import io.mateu.erp.model.authentication.ERPUser;
import io.mateu.erp.model.booking.transfer.TransferDirection;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.financials.PurchaseOrderSendingMethod;
import io.mateu.erp.model.mdd.*;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.partners.Provider;
import io.mateu.erp.model.product.transfer.TransferType;
import io.mateu.erp.model.workflow.SendPurchaseOrdersByEmailTask;
import io.mateu.erp.model.workflow.SendPurchaseOrdersTask;
import io.mateu.erp.model.workflow.TaskStatus;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.data.UserData;
import io.mateu.mdd.core.interfaces.GridDecorator;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.User;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.workflow.WorkflowEngine;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by miguel on 31/1/17.
 */
@Entity
@Getter
@Setter
@NewNotAllowed
@Indelible
@Table(indexes = { @Index(name = "i_service_deprecated", columnList = "deprecated") })
public abstract class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SearchFilter
    private long id;

    @Version
    private int version;


    @Section("Service")
    @Embedded
    @Output
    private Audit audit;

    @Ignored
    @NotNull
    private ServiceType serviceType;

    @Output
    @HtmlCol
    @ColumnWidth(100)
    private String icons;

    @ManyToOne
    @Output
    @NotNull
    private Booking booking;

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    @ManyToOne
    @Output
    private ManagedEvent managedEvent;

    @NotNull
    @ManyToOne
    @ListColumn
    @SearchFilter
    @ColumnWidth(170)
    @Output
    private Office office;

    @ListColumn
    @CellStyleGenerator(ProcessingStatusCellStyleGenerator.class)
    @SearchFilter
    @KPI
    @ColumnWidth(130)
    private ProcessingStatus processingStatus = ProcessingStatus.INITIAL;

    public void setProcessingStatus(ProcessingStatus processingStatus) {
        if (!Helper.equals(this.processingStatus, processingStatus)) {
            this.processingStatus = processingStatus;
            if (getProcessingStatus() != null) switch (getProcessingStatus()) {
                case INITIAL: setEffectiveProcessingStatus(100); break;
                case DATA_OK: setEffectiveProcessingStatus(200); break;
                case READY: setEffectiveProcessingStatus(300); break;
                case SENT: setEffectiveProcessingStatus(400); break;
                case REJECTED: setEffectiveProcessingStatus(450); break;
                case CONFIRMED: setEffectiveProcessingStatus(500); break;
                default: setEffectiveProcessingStatus(0);
            }
            if (booking != null) booking.setUpdateRqTime(LocalDateTime.now());
        }
    }

    @ListColumn(value = "Active")
    @CellStyleGenerator(CancelledCellStyleGenerator.class)
    @ColumnWidth(106)
    @KPI
    private boolean active = true;

    @ListColumn
    @CellStyleGenerator(NoShowCellStyleGenerator.class)
    @ColumnWidth(106)
    private boolean noShow;

    @ListColumn
    @CellStyleGenerator(LockedCellStyleGenerator.class)
    @SameLine
    @ColumnWidth(106)
    private boolean locked;

    @ListColumn
    @CellStyleGenerator(HeldCellStyleGenerator.class)
    @SameLine
    @ColumnWidth(106)
    private boolean held;


    @Output
    private String specialRequests;

    @TextArea
    private String operationsComment;

    @TextArea
    @SameLine
    private String privateComment;



    @Ignored
    private int effectiveProcessingStatus;

    @Section("Purchase")

    @ManyToOne
    private Provider preferredProvider;

    private boolean alreadyPurchased;

    @SameLine
    @Output
    private LocalDateTime alreadyPurchasedDate;

    public void setAlreadyPurchased(boolean alreadyPurchased) {
        this.alreadyPurchased = alreadyPurchased;
        if (alreadyPurchased) setProcessingStatus(ProcessingStatus.CONFIRMED);
    }

    private String providerReference;


    @Output
    @ListColumn
    @SearchFilter
    private String providers;

    @Output
    private LocalDateTime sentToProvider;

    @NotInEditor
    @SearchFilter
    @ListColumn
    private LocalDate start;

    @Ignored
    private LocalDate finish;

    @Ignored
    private LocalDate serviceDateForInvoicing;

    @SearchFilter(field = "id")
    @Caption("Purchase Order Id")
    @ManyToMany
    @OrderColumn(name = "_orderInService")
    @UseLinkToListView
    private List<PurchaseOrder> purchaseOrders = new ArrayList<>();


    @Ignored
    private String signature;

    public void setSignature(String signature) {
        if (!Helper.equals(this.signature, signature)) updateRqTime = LocalDateTime.now();
        this.signature = signature;
    }

    @Transient
    @Ignored
    private String signatureBefore;

    @Ignored
    private boolean visibleInSummary;


    @ListColumn
    @CellStyleGenerator(ValidCellStyleGenerator.class)
    @Output
    @ColumnWidth(120)
    private ValidationStatus validationStatus = ValidationStatus.VALID;

    @Output
    @SameLine
    private String validationMessage;






    private boolean costOverrided;
    @SameLine
    private double overridedCostValue;

    @Ignored
    private String overridedCostValueCalculator;


    @KPI
    @CellStyleGenerator(ValuedCellStyleGenerator.class)
    private boolean saleValued;

    @KPI
    private double totalSale;

    @KPI
    @CellStyleGenerator(ValuedCellStyleGenerator.class)
    private boolean costValued;

    @KPI
    private double totalCost;

    @KPI
    private boolean available;

    @Section("Log")

    @Output
    private String changeLog;

    @Ignored
    private LocalDateTime updateRqTime = LocalDateTime.now();

    @Ignored
    private boolean deprecated;

    @Section("Delivering")
    @Output
    private LocalDateTime delivered;

    @Output
    private String deliveringComments;



    public void updateProcessingStatus(EntityManager em) {

        updateSentTime(em);

        ProcessingStatus ps = getProcessingStatus();
        if (isAlreadyPurchased()) {
            ps = ProcessingStatus.CONFIRMED;
        } else if (getFinish() != null && !getFinish().isBefore(LocalDate.now())) {
            ps = ProcessingStatus.INITIAL;
            if (!isActive() && getSentToProvider() == null) ps = ProcessingStatus.CONFIRMED;
            else if (isAllMapped(em)) {
                ps = ProcessingStatus.DATA_OK;

                if (getPurchaseOrders().size() > 0) {
                    ps = ProcessingStatus.READY;

                    boolean allSent = true;

                    for (PurchaseOrder po : getPurchaseOrders()) if (po.isActive()) if (!po.isSent()) allSent = false;

                    if (allSent) {
                        ps = ProcessingStatus.SENT;

                        boolean allConfirmed = true;
                        boolean anyRejected = false;

                        for (PurchaseOrder po : getPurchaseOrders()) if (po.isActive()) {
                            if (!PurchaseOrderStatus.CONFIRMED.equals(po.getStatus())) allConfirmed = false;
                            if (PurchaseOrderStatus.REJECTED.equals(po.getStatus())) anyRejected = true;
                        }

                        if (allConfirmed) ps = ProcessingStatus.CONFIRMED;
                        else if (anyRejected) ps = ProcessingStatus.REJECTED;

                    }

                }

            }
        } else {
            // finished service. Nothing to do
        }
        setProcessingStatus(ps);
    }

    private void updateSentTime(EntityManager em) {
        if (purchaseOrders.size() > 0) {
            PurchaseOrder po = purchaseOrders.get(purchaseOrders.size() - 1);
            setSentToProvider(po.getSentTime());
        } else setSentToProvider(null);
    }

    public boolean isAllMapped(EntityManager em) {
        return true;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new HashMap<>();
        m.put("cancelled", !isActive());
        String c = getBooking().getSpecialRequests();
        if (!Strings.isNullOrEmpty(getBooking().getCommentsForProvider())) {
            if (c == null) c = "";
            else if (!"".equals(c)) c += " \n/\n ";
            c += getBooking().getCommentsForProvider();
        }
        if (!Strings.isNullOrEmpty(getOperationsComment())) {
            if (c == null) c = "";
            else if (!"".equals(c)) c += " \n/\n ";
            c += getOperationsComment();
        }
        m.put("comment", c);
        if (getPreferredProvider() != null) m.put("preferredprovider", getPreferredProvider().getName());
        m.put("start", getStart());
        m.put("finish", getFinish());
        m.put("overridedCost", "" + getOverridedCostValue());
        return m;
    }


    @Action(order = 0, icon = VaadinIcons.MAP_MARKER)
    public BookingMap map() {
        return new BookingMap(this);
    }

    @Action
    public static void sendToProvider(EntityManager em, Set<Service> selection, Provider provider, String email, @TextArea String postscript) {
        Set services = selection.stream().map(s -> em.merge(s)).collect(Collectors.toSet());
        for (Service s : selection) {
            s.setAlreadyPurchased(false);
            if (provider != null) s.setPreferredProvider(provider);
            try {
                s.checkPurchase(em);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        Map<Provider, SendPurchaseOrdersTask> taskPerProvider = new HashMap<>();
        User u = MDD.getCurrentUser();
        for (Service s : selection) {
            if (s.isActive() || s.getSentToProvider() != null) {
                if (provider != null) s.setPreferredProvider(provider);
                for (PurchaseOrder po : s.getPurchaseOrders()) if (po.isActive()) {

                    if (false && po.getProvider().isAutomaticOrderSending()) {
                        po.setSent(false);
                    } else {
                        po.setSignature(po.createSignature());
                        SendPurchaseOrdersTask t = taskPerProvider.get(po.getProvider());
                        if (t == null) {
                            if (po.getProvider() != null && PurchaseOrderSendingMethod.QUOONAGENT.equals(po.getProvider().getOrdersSendingMethod())) {
                        /*
                        taskPerProvider.put(po.getProvider(), t = new SendPurchaseOrdersToAgentTask());
                        em.persist(t);
                        t.setOffice(s.getOffice());
                        t.setProvider(po.getProvider());
                        t.setStatus(TaskStatus.PENDING);
                        t.setAudit(new Audit(u));
                        ((SendPurchaseOrdersToAgentTask)t).setAgent(provider.getAgent());
                        t.setPostscript(postscript);
                        */
                            } else { // email
                                taskPerProvider.put(po.getProvider(), t = new SendPurchaseOrdersByEmailTask());
                                em.persist(t);
                                t.setOffice(s.getOffice());
                                t.setProvider(po.getProvider());
                                t.setStatus(TaskStatus.PENDING);
                                t.setAudit(new Audit(u));
                                if (!Strings.isNullOrEmpty(email)) {
                                    t.setMethod(PurchaseOrderSendingMethod.EMAIL);
                                    ((SendPurchaseOrdersByEmailTask) t).setTo(email);
                                } else {
                                    t.setMethod((po.getProvider().getOrdersSendingMethod() != null) ? po.getProvider().getOrdersSendingMethod() : PurchaseOrderSendingMethod.EMAIL);
                                    ((SendPurchaseOrdersByEmailTask) t).setTo(po.getProvider().getSendOrdersTo());
                                }
                                ((SendPurchaseOrdersByEmailTask) t).setCc(s.getOffice().getEmailCC());
                                t.setPostscript(postscript);
                            }
                        }
                        t.getPurchaseOrders().add(po);
                        po.getSendingTasks().add(t);
                        po.setSent(true);
                    }
                }
            }
        }

    }

    @Action(saveBefore = true)
    public void sendToProvider(EntityManager em, Provider provider, String email, @TextArea String postscript) throws Throwable {
        
        setAlreadyPurchased(false);
        if (provider != null) setPreferredProvider(provider);
        try {
            checkPurchase(em);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        Map<Provider, SendPurchaseOrdersTask> taskPerProvider = new HashMap<>();
        User u = MDD.getCurrentUser();
        {
            Service s = this;
            if (s.isActive() || s.getSentToProvider() != null) {
                if (provider != null) s.setPreferredProvider(provider);
                for (PurchaseOrder po : s.getPurchaseOrders())
                    if (po.isActive()) {

                        if (false && po.getProvider().isAutomaticOrderSending()) {
                            po.setSent(false);
                        } else {
                            po.setSignature(po.createSignature());
                            SendPurchaseOrdersTask t = taskPerProvider.get(po.getProvider());
                            if (t == null) {
                                if (po.getProvider() != null && PurchaseOrderSendingMethod.QUOONAGENT.equals(po.getProvider().getOrdersSendingMethod())) {
                        /*
                        taskPerProvider.put(po.getProvider(), t = new SendPurchaseOrdersToAgentTask());
                        em.persist(t);
                        t.setOffice(s.getOffice());
                        t.setProvider(po.getProvider());
                        t.setStatus(TaskStatus.PENDING);
                        t.setAudit(new Audit(u));
                        ((SendPurchaseOrdersToAgentTask)t).setAgent(provider.getAgent());
                        t.setPostscript(postscript);
                        */
                                } else { // email
                                    taskPerProvider.put(po.getProvider(), t = new SendPurchaseOrdersByEmailTask());
                                    em.persist(t);
                                    t.setOffice(s.getOffice());
                                    t.setProvider(po.getProvider());
                                    t.setStatus(TaskStatus.PENDING);
                                    t.setAudit(new Audit(u));
                                    if (!Strings.isNullOrEmpty(email)) {
                                        t.setMethod(PurchaseOrderSendingMethod.EMAIL);
                                        ((SendPurchaseOrdersByEmailTask) t).setTo(email);
                                    } else {
                                        t.setMethod((po.getProvider().getOrdersSendingMethod() != null) ? po.getProvider().getOrdersSendingMethod() : PurchaseOrderSendingMethod.EMAIL);
                                        ((SendPurchaseOrdersByEmailTask) t).setTo(po.getProvider().getSendOrdersTo());
                                    }
                                    ((SendPurchaseOrdersByEmailTask) t).setCc(s.getOffice().getEmailCC());
                                    t.setPostscript(postscript);
                                }
                            }
                            t.getPurchaseOrders().add(po);
                            po.getSendingTasks().add(t);
                            po.setSent(true);
                        }
                    }
            }
        }
    }


    public abstract String createSignature();

    public void checkPurchase(EntityManager em, User u) throws Throwable {
        if (getPurchaseOrders().size() == 0 || getSignature() == null || !getSignature().equals(createSignature())) {
            setSignature(createSignature());
            setProcessingStatus(ProcessingStatus.INITIAL);
            refreshPurchaseOrders(em, u);
        } else {
            throw new Throwable("Nothing changed. No need to purchase again");
        }

    }

    public void price(EntityManager em) {
        try {
            setTotalSale(rate(em, true));
            setSaleValued(true);
        } catch (Throwable e) {
            setTotalCost(0);
            setSaleValued(false);
        }

        try {
            setTotalCost(rate(em, false));
            setCostValued(true);
        } catch (Throwable e) {
            setTotalCost(0);
            setCostValued(false);
        }

        for (PurchaseOrder po : getPurchaseOrders()) {
            po.pre();
        }
    }

    public void refreshPurchaseOrders(EntityManager em, User u) throws Throwable {
        generatePurchaseOrders(em);
        for (PurchaseOrder po : getPurchaseOrders()) if (po.isActive()) {
            if (po.getSignature() == null || !po.getSignature().equals(po.createSignature())) po.setSent(false);
        }
        for (PurchaseOrder po : getPurchaseOrders()) if (po.isActive()) {
            if (!isAlreadyPurchased() && !po.isSent() && po.getProvider() != null && po.getProvider().isAutomaticOrderSending()) {
                try {
                    po.send(em, u);
                } catch (Exception e) {
                    String error = "" + e.getClass().getName() + ":" + e.getMessage();
                    if (!error.startsWith("java.lang.Throwable") && !error.startsWith("java.lang.Exception")) e.printStackTrace();
                    else error = error.substring(error.indexOf(":"));
                    System.out.println(error);
                }
            }
        }
        String ps = "";
        for (PurchaseOrder po : getPurchaseOrders()) {
            if (!"".equals(ps)) ps += ",";
            ps += po.getProvider().getName();
        }
        setProviders(ps);
    }


    @Action(value = "Purchase", saveBefore = true)
    public void checkPurchase(EntityManager em) throws Throwable {
        checkPurchase(em, MDD.getCurrentUser());
    }

    @Action(value = "Print POs", saveBefore = true)
    public URL printOrders(EntityManager em) throws Throwable {

        Document xml = new Document();
        Element arrel = new Element("root");
        xml.addContent(arrel);

        arrel.setAttribute("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        String[] xslfo = {""};

        io.mateu.erp.model.config.AppConfig appconfig = io.mateu.erp.model.config.AppConfig.get(em);

        xslfo[0] = appconfig.getXslfoForPurchaseOrder();

        arrel.setAttribute("businessName", appconfig.getBusinessName());


        for (PurchaseOrder po : getPurchaseOrders()) {


            Element epo;
            arrel.addContent(epo = new Element("po").setAttribute("id", "" + po.getId()));
            if (po.getProvider() != null && po.getProvider().getName() != null) epo.setAttribute("provider", po.getProvider().getName());

            for (Service sx : po.getServices()) {

                if (Service.this instanceof TransferService) {

                    TransferService s = (TransferService) Service.this;

                    Element eg;
                    epo.addContent(eg = new Element("group"));
                    eg.setAttribute("date", "" + s.getStart().format(DateTimeFormatter.ofPattern("yyyy-MMM-dd")));
                    eg.setAttribute("direction", "" + s.getDirection());
                    eg.setAttribute("type", "" + s.getTransferType());


                    int totalPax = 0;
                    {

                        if (s.isActive() && !s.isHeld()) {

                            totalPax += s.getPax();

                            Element es;
                            eg.addContent(es = new Element("service"));

                            es.setAttribute("id", "" + s.getId());
                            es.setAttribute("agency", "" + this.getBooking().getAgency().getName());
                            if (this.getBooking().getAgencyReference() != null) es.setAttribute("agencyReference", this.getBooking().getAgencyReference());
                            es.setAttribute("leadName", "" + this.getBooking().getLeadName());
                            String comments = "";
                            if (this.getBooking().getSpecialRequests() != null) comments += this.getBooking().getSpecialRequests();
                            if (s.getBooking().getSpecialRequests() != null) comments += s.getBooking().getSpecialRequests();
                            if (!Strings.isNullOrEmpty(getOperationsComment())) {
                                if (!"".equals(comments)) comments += " / ";
                                comments += getOperationsComment();
                            }
                            es.setAttribute("comments", comments);
                            es.setAttribute("direction", "" + s.getDirection());
                            es.setAttribute("pax", "" + s.getPax());
                            es.setAttribute("pickup", "" + ((s.getEffectivePickup() != null)?s.getEffectivePickup().getName():s.getPickupText()));
                            if (s.getEffectivePickup() != null && s.getEffectivePickup().getResort().getName() != null) es.setAttribute("pickupResort", s.getEffectivePickup().getResort().getName());
                            if (s.getEffectivePickup() != null && s.getEffectivePickup().getAlternatePointForShuttle() != null && !TransferType.EXECUTIVE.equals(s.getTransferType()) && (TransferType.SHUTTLE.equals(s.getTransferType()) || s.getEffectivePickup().isAlternatePointForNonExecutive())) {
                                es.setAttribute("alternatePickup", "" + s.getEffectivePickup().getAlternatePointForShuttle().getName());
                            }
                            es.setAttribute("dropoff", "" + ((s.getEffectiveDropoff() != null)?s.getEffectiveDropoff().getName():s.getDropoffText()));
                            if (s.getEffectiveDropoff() != null && s.getEffectiveDropoff().getResort().getName() != null) es.setAttribute("dropoffResort", s.getEffectiveDropoff().getResort().getName());
                            if (s.getProviders() != null) es.setAttribute("providers", s.getProviders());
                            if (s.getPickupTime() != null) es.setAttribute("pickupTime", s.getPickupTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                            es.setAttribute("transferType", "" + s.getTransferType());
                            if (s.getReturnTransfer() != null) {
                                if (s.getReturnTransfer().isNoShow()) es.setAttribute("wasNoShow", "");
                                if (TransferDirection.OUTBOUND.equals(s.getReturnTransfer().getDirection())) es.setAttribute("returns", s.getReturnTransfer().getFlightTime().format(DateTimeFormatter.BASIC_ISO_DATE));
                            }
                            if (s.getFlightNumber() != null) es.setAttribute("flight", s.getFlightNumber());
                            es.setAttribute("flightTime", s.getFlightTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                            if (s.getFlightOriginOrDestination() != null) es.setAttribute("flightOriginOrDestination", s.getFlightOriginOrDestination());

                            if (s.getPreferredVehicle() != null)  es.setAttribute("preferredVehicle", s.getPreferredVehicle().getName());
                        }

                    }
                    eg.setAttribute("totalPax", "" + totalPax);
                }

            }

        }


        String archivo = UUID.randomUUID().toString();

        java.io.File temp = (System.getProperty("tmpdir") == null)? java.io.File.createTempFile(archivo, ".pdf"):new java.io.File(new java.io.File(System.getProperty("tmpdir")), archivo + ".pdf");


        System.out.println("java.io.tmpdir=" + System.getProperty("java.io.tmpdir"));
        System.out.println("Temp file : " + temp.getAbsolutePath());

        FileOutputStream fileOut = new FileOutputStream(temp);
        String sxml = new XMLOutputter(Format.getPrettyFormat()).outputString(xml);
        System.out.println("xslfo=" + xslfo[0]);
        System.out.println("xml=" + sxml);
        fileOut.write(Helper.fop(new StreamSource(new StringReader(xslfo[0])), new StreamSource(new StringReader(sxml))));
        fileOut.close();

        String baseUrl = System.getProperty("tmpurl");
        if (baseUrl == null) {
            return temp.toURI().toURL();
        }
        return new URL(baseUrl + "/" + temp.getName());
    }

    @Action
    public String showSignature(EntityManager em) throws Throwable {
        return getSignature();
    }


    public double rate(EntityManager em, boolean sale) throws Throwable {
        StringWriter sw = new StringWriter();
        PrintWriter report = new PrintWriter(sw);

        double v = 0;
        if (!sale && isCostOverrided()) {
            v = getOverridedCostValue();
            report.print("Cost overrided for " + v);
        } else if (sale && booking.isValueOverrided()) {
            v = Helper.roundEuros(booking.getOverridedValue() / booking.getServices().size());
            report.print("Sale overrided at booking for " + v);
        } else {
            v = sale?rateSale(em, report):rateCost(em, null, report);
        }

        System.out.println(sw.toString());
        return v;
    }

    public abstract double rateSale(EntityManager em, PrintWriter report) throws Throwable;

    public abstract double rateCost(EntityManager em, Provider supplier, PrintWriter report) throws Throwable;


    public void generatePurchaseOrders(EntityManager em) throws Throwable {
        Provider provider = (getPreferredProvider() != null)?getPreferredProvider():findBestProvider(em);
        if (provider == null) throw new Throwable("Preferred provider needed for service " + getId());
        if (isHeld()) throw new Throwable("Service " + getId() + " is held");
        if (!isActive() && getSentToProvider() == null) throw new Throwable("Cancelled and was never sent");
        if (!ProcessingStatus.CONFIRMED.equals(getProcessingStatus())) {
            PurchaseOrder po = null;
            if (getPurchaseOrders().size() > 0) {
                po = getPurchaseOrders().get(getPurchaseOrders().size() - 1);
                if (!provider.equals(po.getProvider())) {
                    po.cancel(em);
                    po = null;
                }
            }

            //todo: comprobar si hay algún pedido que podamos reutilizar

            boolean nueva = false;
            if (po == null) {
                nueva = true;
                po = new PurchaseOrder();
                po.setServiceType(serviceType);
                po.setAudit(new Audit());
                po.getServices().add(this);
                getPurchaseOrders().add(po);
                po.setStatus(PurchaseOrderStatus.PENDING);
            }
            po.setOffice(getOffice());
            po.setProvider(provider);
            po.setCurrency(provider.getCurrency());
            po.setValued(false);
            po.setTotal(0);
            po.setUpdateRqTime(LocalDateTime.now());

            if (nueva) em.persist(po);

        }
    }

    public abstract Provider findBestProvider(EntityManager em) throws Throwable;


    @Override
    public String toString() {
        String s = "" + getId();
        if (getBooking() != null) s += " " + getBooking().getAgency().getName() + " / " + getBooking().getLeadName() + " - " + getBooking().getAgencyReference();
        return s;
    }


    public static void main(String... args) throws Throwable {

        System.setProperty("appconf", "/home/miguel/quonext/mateu.properties");

        //Service.repair(null);

        /*
        Service s = new Service() {
            @Override
            public String createSignature() {
                return null;
            }

            @Override
            public double rateCost(EntityManager em) throws Throwable {
                return 0;
            }
        };
        s.setProcessingStatus(ProcessingStatus.DATA_OK);

        System.out.println(s.getProcessingStatus().ordinal());
*/



    }


    public Map<String,Object> getData() {
        Map<String, Object> d = new HashMap<>();

        d.put("id", getId());
        d.put("locator", this.getBooking().getId());
        d.put("leadName", this.getBooking().getLeadName());
        d.put("agency", this.getBooking().getAgency().getName());
        if (this.getBooking().getAgency().getMarket() != null) d.put("market", this.getBooking().getAgency().getMarket().getName());
        d.put("agencyReference", this.getBooking().getAgencyReference());
        d.put("status", (isActive())?"ACTIVE":"CANCELLED");
        if (getAudit().getCreated() != null) d.put("created", getAudit().getCreated().format(DateTimeFormatter.BASIC_ISO_DATE.ISO_DATE_TIME));
        if (getOffice() != null) d.put("office", getOffice().getName());

        String c = getBooking().getSpecialRequests();
        if (!Strings.isNullOrEmpty(getBooking().getCommentsForProvider())) {
            if (c == null) c = "";
            else if (!"".equals(c)) c += "\n/\n";
            c += getBooking().getCommentsForProvider();
        }
        if (!Strings.isNullOrEmpty(getOperationsComment())) {
            if (c == null) c = "";
            else if (!"".equals(c)) c += "\n/\n";
            c += getOperationsComment();
        }
        d.put("comments", c);

        if (getStart() != null) d.put("start", getStart().format(DateTimeFormatter.ISO_DATE));
        if (getFinish() != null) d.put("finish", getFinish().format(DateTimeFormatter.ISO_DATE));
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        d.put("serviceDates", "" + ((getStart() != null)?getStart().format(f):"..."));
        d.put("startddmmyyyy", getStart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        return d;
    }


    public void cancel(EntityManager em, User u) {
        if (isActive()) {
            setActive(false);
        }
    }

    @PostLoad
    public void postLoad() {
        setAlreadyPurchasedBefore(isAlreadyPurchased());
        setSignatureBefore(getSignature());
    }

    @Transient
    @Ignored
    private List<String> vistos = new ArrayList<>();

    @Transient
    @Ignored
    private boolean alreadyPurchasedBefore;


    public void validate(EntityManager em) {
        // todo: esto lo hemos llevado a la reserva
        setValidationStatus(ValidationStatus.VALID);
        setValidationMessage("");
    }


    @PrePersist@PreUpdate
    public void pre() {
        if (getSignature() == null || !getSignature().equals(createSignature())) {
            setUpdateRqTime(LocalDateTime.now());
            setAlreadyPurchased(false);
            System.out.println("service.signature changed from ");
            System.out.println("" + getSignature());
            System.out.println("to ");
            System.out.println("" + createSignature());
            System.out.println("====================== ");

        }
    }

    @PostPersist@PostUpdate
    public void post() {
        if (updateRqTime != null) WorkflowEngine.add(new Runnable() {
            @Override
            public void run() {
                try {
                    Helper.transact(em -> {
                        Service s = em.find(Service.class, getId());
                        if (s.getUpdateRqTime() != null) {
                            s.complete(em);
                            s.logChanges(em);
                            s.purchase(em);
                            s.price(em);
                            s.summarize(em);
                            s.setUpdateRqTime(null);
                        }
                    });
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
    }

    public void complete(EntityManager em) throws Throwable {
    }

    public void logChanges(EntityManager em) throws Throwable {

        String l = getChangeLog();
        if (l == null) l = "";
        boolean update = false;
        if (Strings.isNullOrEmpty(getSignatureBefore())) {
            if (vistos.size() == 0) {
                if (!"".equals(l)) l += "\n";
                l += "" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                l += " ";
                l += "ADDED";
                vistos.add("***");
            }
            update = true;
        } else if (!getSignatureBefore().equals(getSignature())) {
            try {
                Map<String, Object> m0 = Helper.fromJson(getSignatureBefore());
                Map<String, Object> m = Helper.fromJson(getSignature());
                List<String> ks = new ArrayList<>();
                ks.addAll(m0.keySet());
                for (String k : m.keySet()) if (!ks.contains(k)) ks.add(k);
                for (String k : ks) {
                    Object v0 = m0.get(k);
                    Object v = m.get(k);
                    if (v0 == null) v0 = "---";
                    if (v == null) v = "---";
                    if (v0 instanceof Map && ((Map)v0).containsKey("chronology")) {
                        Map x0 = (Map) v0;
                        v0 = "" + x0.get("year") + "-" + x0.get("month") + "-" + x0.get("dayOfMonth") + " " + x0.get("hour") + ":" + x0.get("minute");
                    }
                    if (v instanceof Map && ((Map)v).containsKey("chronology")) {
                        Map x0 = (Map) v;
                        v = "" + x0.get("year") + "-" + x0.get("month") + "-" + x0.get("dayOfMonth") + " " + x0.get("hour") + ":" + x0.get("minute");
                    }
                    if (!v0.equals(v)) {
                        if (!vistos.contains(k)) {
                            if (vistos.size() == 0) {
                                if (!"".equals(l)) l += "\n";
                                l += "" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                                l += " ";
                                l += "MODIFIED";
                            }
                            vistos.add(k);
                            l += "\n" + Helper.capitalize(k) + ": " + v0 + " -> " + v;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            update = true;
        }
        setChangeLog(l);
    }

    public void purchase(EntityManager em) {

        System.out.println("service " + getId() + ".purchase");

        if (isAlreadyPurchased() && getAlreadyPurchasedDate() == null) {
            setAlreadyPurchasedDate(LocalDateTime.now());
        } else if (!isAlreadyPurchased() && getAlreadyPurchasedDate() != null) {
            setAlreadyPurchasedDate(null);
        }

        try {
            checkPurchase(em, getAudit().getModifiedBy());
        } catch (Throwable e) {
            String error = "" + e.getClass().getName() + ":" + e.getMessage();
            if (!error.startsWith("java.lang.Throwable") && !error.startsWith("java.lang.Exception")) e.printStackTrace();
            else error = error.substring(error.indexOf(":"));
            System.out.println(error);
        }

    }

    public void summarize(EntityManager em) {

        System.out.println("service " + getId() + ".summarize");

        updateProcessingStatus(em);
        validate(em);

        setVisibleInSummary(isActive() || (getSentToProvider() != null && !ProcessingStatus.CONFIRMED.equals(getProcessingStatus())));

        booking.summarize(em);

    }

    public Element toXml(boolean includeQR) {
        Element xml = toXml();

        if (includeQR) {
            try {
                String archivo = UUID.randomUUID().toString();
                java.io.File temp = (System.getProperty("tmpdir") == null)? java.io.File.createTempFile(archivo, ".pdf"):new java.io.File(new java.io.File(System.getProperty("tmpdir")), archivo + ".pdf");

                Helper.generateQRCodeImage("BOOKING" + getBooking().getId() + "/SERVICE" + getId(), 300, 300, temp.getAbsolutePath());

                xml.setAttribute("qrfile", temp.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return xml;
    }


    public Element toXml() {
        Element xml = new Element("service");
        xml.setAttribute("id", "" + getId());
        xml.setAttribute("status", isActive()?"OK":"CANCELLED");
        xml.setAttribute("header", getDescription());
        try {
            if (getOffice().getCompany().getLogo() != null) xml.setAttribute("urllogo", getOffice().getCompany().getLogo().toFileLocator().getTmpPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        xml.setAttribute("serviceType", getClass().getSimpleName().replaceAll("Service", ""));
        xml.setAttribute("description", getDescription());
        if (booking.getLeadName() != null) xml.setAttribute("leadname", booking.getLeadName());
        xml.setAttribute("formalization", "" + (booking.getFormalizationDate() != null?booking.getFormalizationDate().toLocalDate():booking.getAudit().getCreated().toLocalDate()));
        if (booking.getAgencyReference() != null) xml.setAttribute("fileto", booking.getAgencyReference());
        xml.setAttribute("remarks", Strings.isNullOrEmpty(booking.getSpecialRequests())?"None":booking.getSpecialRequests());

        Element supplxml = getSupplierXml();
        if (supplxml != null) xml.addContent(supplxml);
        if (getProviderReference() != null) supplxml.setAttribute("hisreference", getProviderReference());

        if (getPreferredProvider() != null && !Strings.isNullOrEmpty(getPreferredProvider().getPayableByInVoucher())) {
            xml.setAttribute("payableBy", getPreferredProvider().getPayableByInVoucher());
        } else {
            xml.setAttribute("payableBy", getOffice().getCompany().getName());
        }


        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (getStart() != null) xml.setAttribute("start", getStart().format(dtf));
        if (getFinish() != null) xml.setAttribute("finish", getFinish().format(dtf));

        return xml;
    }

    public Element getSupplierXml() {
        Element xml = null;
        if (getPreferredProvider() != null) {
            xml = new Element("supplier");
            Provider p = getPreferredProvider();
            if (p.getName() != null) xml.setAttribute("name", p.getName());
            if (p.getFullAddress() != null) xml.setAttribute("address", p.getFullAddress());
            if (p.getTelephone() != null) xml.setAttribute("telephone", p.getTelephone());
            if (p.getEmail() != null) xml.setAttribute("telephone", p.getEmail());
            xml.setAttribute("gps", "---");

        }
        return xml;
    }

    protected abstract String getDescription();



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

                            if (o instanceof Service) {
                                if (!((Service)o).isActive()) s = (s != null)?s + " cancelled":"cancelled";
                            } else {
                                if (!((Boolean)((Object[])o)[4])) {
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

    public abstract BillingConcept getBillingConcept(EntityManager em);

    public void rateSale(EntityManager em) {

        try {
            setTotalSale(rate(em, true));
            setSaleValued(true);
        } catch (Throwable e) {
            setTotalCost(0);
            setSaleValued(false);
        }

    }

    public void rateCost(EntityManager em) {

        try {
            setTotalCost(rate(em, false));
            setCostValued(true);
        } catch (Throwable e) {
            setTotalCost(0);
            setCostValued(false);
        }

        purchaseOrders.forEach(o -> o.setValued(false));
    }


    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj != null && obj instanceof Service && id == ((Service) obj).getId());
    }

    public String getChargeSubject() {
        return "Service " + getId();
    }
}
