package io.mateu.erp.model.booking;

import com.google.common.base.Strings;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.User;
import io.mateu.erp.model.booking.transfer.TransferDirection;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.financials.PurchaseOrderSendingMethod;
import io.mateu.erp.model.invoicing.Charge;
import io.mateu.erp.model.mdd.*;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.product.transfer.TransferType;
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


/**
 * Created by miguel on 31/1/17.
 */
@Entity
@Getter
@Setter
public abstract class Service {

    @Transient
    @Ignored
    private boolean preventAfterSet;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SearchFilter
    @ListColumn(width = 70)
    private long id;

    @Embedded
    @Ignored
    private Audit audit;

    @Tab("General")
    @FullWidth
    @ManyToOne
    @NotNull
    @SearchFilter(value="File Id", field = "id")
    @SearchFilter(field = "agencyReference")
    @SearchFilter(field = "agency")
    @ListColumn(value="Boking", field = "id")
    @ListColumn(field = "agencyReference", width = 150)
    @ListColumn(field = "agency", width = 150)
    @SearchFilter(field = "leadName")
    @ListColumn(field = "leadName")
    @SearchFilter(field = "telephone")
    @ListColumn(field = "telephone")
    private File file;


    @ManyToOne
    @Ignored
    private Booking booking;

    @Ignored
    @NotInEditor
    @ListColumn
    @CellStyleGenerator(IconCellStyleGenerator.class)
    private String icon;

    @ListColumn(width = 60)
    @CellStyleGenerator(ConfirmedCellStyleGenerator.class)
    private ServiceConfirmationStatus answer = ServiceConfirmationStatus.CONFIRMED;
    @SameLine
    private String answerText;

    @ListColumn(width = 60)
    @CellStyleGenerator(ValidCellStyleGenerator.class)
    @Output
    private ValidationStatus validationStatus = ValidationStatus.VALID;

    @Output
    @SameLine
    private String validationMessage;

    @ListColumn
    @CellStyleGenerator(ProcessingStatusCellStyleGenerator.class)
    @SearchFilter
    @NotInEditor
    private ProcessingStatus processingStatus = ProcessingStatus.INITIAL;

    public void setProcessingStatus(ProcessingStatus processingStatus) {
        this.processingStatus = processingStatus;
        if (getProcessingStatus() != null) switch (getProcessingStatus()) {
            case INITIAL: setEffectiveProcessingStatus(100); break;
            case DATA_OK: setEffectiveProcessingStatus(200); break;
            case PURCHASEORDERS_READY: setEffectiveProcessingStatus(300); break;
            case PURCHASEORDERS_SENT: setEffectiveProcessingStatus(400); break;
            case PURCHASEORDERS_REJECTED: setEffectiveProcessingStatus(450); break;
            case PURCHASEORDERS_CONFIRMED: setEffectiveProcessingStatus(500); break;
            default: setEffectiveProcessingStatus(0);
        }
    }

    @ListColumn(value = "Active")
    @CellStyleGenerator(CancelledCellStyleGenerator.class)
    private boolean cancelled;

    @ListColumn
    @CellStyleGenerator(NoShowCellStyleGenerator.class)
    @SameLine
    private boolean noShow;

    @ListColumn
    @CellStyleGenerator(LockedCellStyleGenerator.class)
    @SameLine
    private boolean locked;

    @ListColumn
    @CellStyleGenerator(HeldCellStyleGenerator.class)
    @SameLine
    private boolean held;


    private boolean alreadyInvoiced;

    @NotNull
    @ManyToOne
    private Office office;

    @NotNull
    @ManyToOne
    private PointOfSale pos;

    @TextArea
    private String comment;

    @TextArea
    @SameLine
    private String operationsComment;

    @TextArea
    @SameLine
    private String privateComment;

    @Tab("Change log")
    @Output
    private String changeLog;


    @Tab("Price")
    private boolean valueOverrided;
    @SameLine
    private double overridedNetValue;
    @Ignored
    private String overridedNetValueCalculator;
    @SameLine
    private double overridedRetailValue;
    @Ignored
    private String overridedRetailValueCalculator;
    @SameLine
    private double overridedCommissionValue;


    private boolean costOverrided;
    @SameLine
    private double overridedCostValue;
    @Ignored
    private String overridedCostValueCalculator;


    @Output
    @ListColumn
    @CellStyleGenerator(ValuedCellStyleGenerator.class)
    private boolean valued;

    @Sum
    @Output
    @ListColumn
    private double totalNetValue;

    @Output
    @SameLine
    private double totalRetailValue;

    @Output
    @SameLine
    private double totalCommissionValue;

    @Output
    private String priceReport;

    @Output
    private double currentCancellationCost;

    @Output
    @ListColumn
    @CellStyleGenerator(ValuedCellStyleGenerator.class)
    private boolean purchaseValued;

    @Sum
    @Output
    @ListColumn
    private double totalCost;

    @Tab("Charges")
    @OneToMany(mappedBy = "service")
    @Output
    private List<Charge> charges = new ArrayList<>();

    @Tab("Handling")
    @Ignored
    private int effectiveProcessingStatus;

    @Tab("Purchase")
    @ManyToOne
    @QLFilter("x.provider = true")
    private Partner preferredProvider;

    private boolean alreadyPurchased;

    @SameLine
    @Output
    private LocalDateTime alreadyPurchasedDate;

    @SameLine
    private String providerReference;


    @Output
    @ListColumn
    @SearchFilter
    private String providers;

    @Output
    private LocalDateTime sentToProvider;


    @NotInEditor
    @SearchFilter
    @ListColumn(order = true)
    private LocalDate start;

    @Ignored
    @ListColumn
    private LocalDate finish;

    @Ignored
    private LocalDate serviceDateForInvoicing;

    @SearchFilter(value="Purchase Order Id", field = "id")
    @ManyToMany
    @NotInEditor
    private List<PurchaseOrder> purchaseOrders = new ArrayList<>();


    @Output 
    private String signature;

    @Transient
    @Ignored
    private String signatureBefore;

    @Ignored
    private boolean visibleInSummary;

    public void updateProcessingStatus(EntityManager em) {
        ProcessingStatus ps = getProcessingStatus();
        if (isAlreadyPurchased()) {
            ps = ProcessingStatus.PURCHASEORDERS_CONFIRMED;
        } else if (getFinish() != null && !getFinish().isBefore(LocalDate.now())) {
            ps = ProcessingStatus.INITIAL;
            if (isCancelled() && getSentToProvider() == null) ps = ProcessingStatus.PURCHASEORDERS_CONFIRMED;
            else if (isAllMapped(em)) {
                ps = ProcessingStatus.DATA_OK;

                if (getPurchaseOrders().size() > 0) {
                    ps = ProcessingStatus.PURCHASEORDERS_READY;

                    boolean allSent = true;

                    for (PurchaseOrder po : getPurchaseOrders()) if (!po.isCancelled()) if (!po.isSent()) allSent = false;

                    if (allSent) {
                        ps = ProcessingStatus.PURCHASEORDERS_SENT;

                        boolean allConfirmed = true;
                        boolean anyRejected = false;

                        for (PurchaseOrder po : getPurchaseOrders()) if (!po.isCancelled()) {
                            if (!PurchaseOrderStatus.CONFIRMED.equals(po.getStatus())) allConfirmed = false;
                            if (PurchaseOrderStatus.REJECTED.equals(po.getStatus())) anyRejected = true;
                        }

                        if (allConfirmed) ps = ProcessingStatus.PURCHASEORDERS_CONFIRMED;
                        else if (anyRejected) ps = ProcessingStatus.PURCHASEORDERS_REJECTED;

                    }

                }

            }
        } else {
            // finished service. Nothing to do
        }
        setProcessingStatus(ps);
    }

    public boolean isAllMapped(EntityManager em) {
        return true;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new HashMap<>();
        m.put("cancelled", isCancelled());
        String c = getComment();
        if (!Strings.isNullOrEmpty(getOperationsComment())) {
            if (c == null) c = "";
            else if (!"".equals(c)) c += " / ";
            c += getOperationsComment();
        }
        m.put("comment", c);
        if (getPreferredProvider() != null) m.put("preferredprovider", getPreferredProvider().getName());
        m.put("start", getStart());
        m.put("finish", getFinish());
        return m;
    }


    @Action
    public static void sendToProvider(EntityManager em, UserData user, @Selection List<Data> selection, @QLFilter("x.provider = true") Partner provider, String email, @TextArea String postscript) {
        for (Data d : selection) {
            Service s = em.find(Service.class, d.get("_id"));
            s.setAlreadyPurchased(false);
            if (provider != null) s.setPreferredProvider(provider);
            try {
                s.checkPurchase(em, user);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        Map<Partner, SendPurchaseOrdersTask> taskPerProvider = new HashMap<>();
        io.mateu.erp.model.authentication.User u = em.find(io.mateu.erp.model.authentication.User.class, user.getLogin());
        for (Data d : selection) {
            Service s = em.find(Service.class, d.get("_id"));
            if (!s.isCancelled() || s.getSentToProvider() != null) {
                if (provider != null) s.setPreferredProvider(provider);
                for (PurchaseOrder po : s.getPurchaseOrders()) {
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
                                ((SendPurchaseOrdersByEmailTask)t).setTo(email);
                            } else {
                                t.setMethod((po.getProvider().getOrdersSendingMethod() != null)?po.getProvider().getOrdersSendingMethod():PurchaseOrderSendingMethod.EMAIL);
                                ((SendPurchaseOrdersByEmailTask)t).setTo(po.getProvider().getSendOrdersTo());
                            }
                            ((SendPurchaseOrdersByEmailTask)t).setCc(s.getOffice().getEmailCC());
                            t.setPostscript(postscript);
                        }
                    }
                    t.getPurchaseOrders().add(po);
                    po.getSendingTasks().add(t);
                }
            }
        }
//        for (SendPurchaseOrdersTask t : taskPerProvider.values()) {
//            try {
//                t.execute(em, u);
//            } catch (Throwable e) {
//                e.printStackTrace();
//            }
//        }
    }


    public abstract String createSignature();

    public void checkPurchase(EntityManager em, User u) throws Throwable {
        if (getPurchaseOrders().size() == 0 || getSignature() == null || !getSignature().equals(createSignature())) {
            setSignature(createSignature());

            refreshPurchaseOrders(em, u);

        } else {
            throw new Throwable("Nothing changed. No need to purchase again");
        }

    }

    public void refreshPurchaseOrders(EntityManager em, User u) throws Throwable {
        generatePurchaseOrders(em);
        for (PurchaseOrder po : getPurchaseOrders()) {
            if (po.getSignature() == null || !po.getSignature().equals(po.createSignature())) po.setSent(false);
        }
        for (PurchaseOrder po : getPurchaseOrders()) {
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


    @Action("Purchase")
    public void checkPurchase(EntityManager em, UserData user) throws Throwable {
        checkPurchase(em, em.find(io.mateu.erp.model.authentication.User.class, user.getLogin()));
    }


    public void price(EntityManager em, User u) {
        setValued(false);
        setTotalNetValue(0);
        if (!isCancelled() && isValueOverrided()) {
            setTotalNetValue(getOverridedNetValue());
            setValued(true);
            setPriceReport("Used overrided value");
        } else if (isCancelled()) {
            setValued(true);
            setTotalNetValue(0);
            setPriceReport("Value 0 as it is cancelled");
        }
        else {
            try {
                StringWriter sw = new StringWriter();
                setTotalNetValue(rate(em, true, new PrintWriter(sw)));
                setPriceReport(sw.toString());
                setValued(true);
            } catch (Throwable throwable) {
                setPriceReport("" + throwable.getClass().getName() + ":" + throwable.getMessage());
                throwable.printStackTrace();
            }
        }

        try {
            checkPurchase(em, u);
        } catch (Throwable e) {
            String error = "" + e.getClass().getName() + ":" + e.getMessage();
            if (!error.startsWith("java.lang.Throwable") && !error.startsWith("java.lang.Exception")) e.printStackTrace();
            else error = error.substring(error.indexOf(":"));
            System.out.println(error);
        }

        double totalCost = 0;

        if (isCostOverrided()) {
            setTotalCost(getOverridedCostValue());
            setPurchaseValued(true);

            for (PurchaseOrder po : getPurchaseOrders()) {
                if (!po.isCancelled()) {
                    po.setValueOverrided(true);
                    po.setOverridedValue(getOverridedCostValue());
                }
            }

        } else {
            boolean purchaseValued = getPurchaseOrders().size() > 0;

            for (PurchaseOrder po : getPurchaseOrders()) {
                try {
                    po.price(em);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                if (po.isValued()) totalCost += po.getTotal();
                purchaseValued &= po.isValued();
            }
            setTotalCost(Helper.roundOffEuros(totalCost));
            setPurchaseValued(purchaseValued);
        }

    }

    @Action
    public void price(EntityManager em, UserData user) {
        price(em, em.find(io.mateu.erp.model.authentication.User.class, user.getLogin()));
    }

    @Action("Print POs")
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

                        if (!s.isCancelled() && !s.isHeld()) {

                            totalPax += s.getPax();

                            Element es;
                            eg.addContent(es = new Element("service"));

                            es.setAttribute("id", "" + s.getId());
                            es.setAttribute("agency", "" + this.getFile().getAgency().getName());
                            if (this.getFile().getAgencyReference() != null) es.setAttribute("agencyReference", this.getFile().getAgencyReference());
                            es.setAttribute("leadName", "" + this.getFile().getLeadName());
                            String comments = "";
                            if (this.getFile().getComments() != null) comments += this.getFile().getComments();
                            if (s.getComment() != null) comments += s.getComment();
                            if (!Strings.isNullOrEmpty(getOperationsComment())) {
                                if (!"".equals(comments)) comments += " / ";
                                comments += getOperationsComment();
                            }
                            es.setAttribute("comments", comments);
                            es.setAttribute("direction", "" + s.getDirection());
                            es.setAttribute("pax", "" + s.getPax());
                            es.setAttribute("pickup", "" + ((s.getEffectivePickup() != null)?s.getEffectivePickup().getName():s.getPickupText()));
                            if (s.getEffectivePickup() != null && s.getEffectivePickup().getZone().getName() != null) es.setAttribute("pickupResort", s.getEffectivePickup().getZone().getName());
                            if (s.getEffectivePickup() != null && s.getEffectivePickup().getAlternatePointForShuttle() != null && !TransferType.EXECUTIVE.equals(s.getTransferType()) && (TransferType.SHUTTLE.equals(s.getTransferType()) || s.getEffectivePickup().isAlternatePointForNonExecutive())) {
                                es.setAttribute("alternatePickup", "" + s.getEffectivePickup().getAlternatePointForShuttle().getName());
                            }
                            es.setAttribute("dropoff", "" + ((s.getEffectiveDropoff() != null)?s.getEffectiveDropoff().getName():s.getDropoffText()));
                            if (s.getEffectiveDropoff() != null && s.getEffectiveDropoff().getZone().getName() != null) es.setAttribute("dropoffResort", s.getEffectiveDropoff().getZone().getName());
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



    @Badges
    public List<Data> getBadges() {
        List<Data> l = new ArrayList<>();
        l.add(new Data("_css", "brown", "_value", "" + getTotalNetValue()));
        String s = "";
        ProcessingStatus v = getProcessingStatus();
        if (v != null) switch (v) {
            case INITIAL:
            case DATA_OK: s = "azul"; break;
            case PURCHASEORDERS_SENT:
            case PURCHASEORDERS_READY: s = "naranja"; break;
            case PURCHASEORDERS_CONFIRMED: s = "verde"; break;
            case PURCHASEORDERS_REJECTED: s = "rojo"; break;
        }
        l.add(new Data("_css", s, "_value", "" + getProcessingStatus()));
        return l;
    }

    @Links
    public List<MDDLink> getLinks() {
        List<MDDLink> l = new ArrayList<>();
        if (this.getFile() != null) {
            l.add(new MDDLink("File", File.class, ActionType.OPENEDITOR, new Data("_id", this.getFile().getId())));
            l.add(new MDDLink("Tasks", AbstractTask.class, ActionType.OPENLIST, new Data("services.id", getId())));
            l.add(new MDDLink("Purchase orders", PurchaseOrder.class, ActionType.OPENLIST, new Data("services.id", getId())));
        }
        return l;
    }


    public double rate(EntityManager em, boolean sale, PrintWriter report) throws Throwable {
        return rate(em, sale, null, report);
    }

    public abstract double rate(EntityManager em, boolean sale, Partner supplier, PrintWriter report) throws Throwable;


    public void generatePurchaseOrders(EntityManager em) throws Throwable {
        Partner provider = (getPreferredProvider() != null)?getPreferredProvider():findBestProvider(em);
        if (provider == null) throw new Throwable("Preferred provider needed for service " + getId());
        if (isHeld()) throw new Throwable("Service " + getId() + " is held");
        if (isCancelled() && getSentToProvider() == null) throw new Throwable("Cancelled and was never sent");
        if (!ProcessingStatus.PURCHASEORDERS_CONFIRMED.equals(getProcessingStatus())) {
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
                po.setAudit(new Audit());
                po.getServices().add(this);
                getPurchaseOrders().add(po);
                po.setStatus(PurchaseOrderStatus.PENDING);
            }
            po.setOffice(getOffice());
            po.setProvider(provider);
            po.setCurrency(provider.getCurrency());

            if (nueva) em.persist(po);

        }
    }

    public abstract Partner findBestProvider(EntityManager em) throws Throwable;


    @Override
    public String toString() {
        String s = "";
        if (getAudit() != null) s += getAudit();
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
            public double rate(EntityManager em) throws Throwable {
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
        d.put("locator", this.getFile().getId());
        d.put("agency", this.getFile().getAgency().getName());
        d.put("agencyReference", this.getFile().getAgencyReference());
        d.put("status", (isCancelled())?"CANCELLED":"ACTIVE");
        d.put("created", getAudit().getCreated().format(DateTimeFormatter.BASIC_ISO_DATE.ISO_DATE_TIME));
        if (getOffice() != null) d.put("office", getOffice().getName());

        String c = getComment();
        if (!Strings.isNullOrEmpty(getOperationsComment())) {
            if (c == null) c = "";
            else if (!"".equals(c)) c += " / ";
            c += getOperationsComment();
        }
        d.put("comment", c);

        d.put("start", getStart());
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        d.put("serviceDates", "" + ((getStart() != null)?getStart().format(f):"..."));
        d.put("startddmmyyyy", getStart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        double base = Helper.roundOffEuros(getTotalNetValue() / (1d + 10d / 100d));
        double iva = Helper.roundOffEuros(getTotalNetValue() - base);


        d.put("base", base);
        d.put("iva", iva);

        d.put("valued", isValued());
        d.put("total", getTotalNetValue());
        d.put("purchaseValued", isPurchaseValued());
        d.put("totalCost", getTotalCost());

        return d;
    }

    public void cancel(EntityManager em, User u) {
        if (!isCancelled()) {
            setCancelled(true);
            setTotalNetValue(getCurrentCancellationCost());
            setTotalCost(0);
            setTotalCommissionValue(0);
            setTotalRetailValue(0);
            price(em, u);
            try {
                checkPurchase(em, u);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
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
        setValidationStatus(ValidationStatus.VALID);
        setValidationMessage("");
    }

    @PostUpdate
    public void postUpdate() throws Throwable {

        System.out.println("service.postupdate()");

        if (!isPreventAfterSet()) {

            WorkflowEngine.add(new Runnable() {
                @Override
                public void run() {

                    try {
                        Helper.transact(new JPATransaction() {
                            @Override
                            public void run(EntityManager em) throws Throwable {
                                em.find(Service.class, getId()).postUpdate(em);
                            }
                        });
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }

                }
            });

        }

    }

    public void postUpdate(EntityManager em) throws Throwable {

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

        afterSet();
    }

    @PostPersist
    public void afterSet() throws Throwable {

        System.out.println("service.afterset()");

        long finalId = getId();

        WorkflowEngine.add(new Runnable() {
            @Override
            public void run() {

                try {
                    Helper.transact(new JPATransaction() {
                        @Override
                        public void run(EntityManager em) throws Throwable {

                            em.find(Service.class, finalId).afterSetAsService(em);

                        }
                    });
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

            }
        });

    }

    public void afterSetAsService(EntityManager em) {

        System.out.println("service " + getId() + ".afterset");

        if (true || this.getFile().getFinish() == null) {
            for (Service x : this.getFile().getServices()) {
                if (x.getStart() != null) {
                    if (this.getFile().getStart() == null || this.getFile().getStart().isAfter(x.getStart())) this.getFile().setStart(x.getStart());
                    if (this.getFile().getFinish() == null || this.getFile().getFinish().isBefore(x.getStart())) this.getFile().setFinish(x.getStart());
                }

                if (x.getFinish() != null) {
                    if (this.getFile().getStart() == null || this.getFile().getStart().isAfter(x.getFinish())) this.getFile().setStart(x.getFinish());
                    if (this.getFile().getFinish() == null || this.getFile().getFinish().isBefore(x.getFinish())) this.getFile().setFinish(x.getFinish());
                }

                if (this.getFile().getAgency().isOneLinePerBooking()) x.setServiceDateForInvoicing(this.getFile().getFinish());
                else x.setServiceDateForInvoicing(x.getStart());
            }
        }

        if (isAlreadyPurchased() && getAlreadyPurchasedDate() == null) {
            setAlreadyPurchasedDate(LocalDateTime.now());
        } else if (!isAlreadyPurchased() && getAlreadyPurchasedDate() != null) {
            setAlreadyPurchasedDate(null);
        }

        try {
            price(em, getAudit().getModifiedBy());
        } catch (Throwable e) {
            String error = "" + e.getClass().getName() + ":" + e.getMessage();
            if (!error.startsWith("java.lang.Throwable") && !error.startsWith("java.lang.Exception")) e.printStackTrace();
            else error = error.substring(error.indexOf(":"));
            System.out.println(error);
        }

        try {
            checkPurchase(em, getAudit().getModifiedBy());
        } catch (Throwable e) {
            String error = "" + e.getClass().getName() + ":" + e.getMessage();
            if (!error.startsWith("java.lang.Throwable") && !error.startsWith("java.lang.Exception")) e.printStackTrace();
            else error = error.substring(error.indexOf(":"));
            System.out.println(error);
        }

        /*

        boolean laFirmaHaCambiado = getSignature() == null || !getSignature().equals(createSignature());
        setSignature(createSignature());

        if (laFirmaHaCambiado) {
            try {
                price(em, getAudit().getModifiedBy());
            } catch (Throwable e) {
                e.printStackTrace();
            }

            try {
                refreshPurchaseOrders(em, getAudit().getModifiedBy());
            } catch (Throwable e) {
                e.printStackTrace();
            }

        }
        */

        updateProcessingStatus(em);
        validate(em);


        setVisibleInSummary(!isCancelled() || (getSentToProvider() != null && !ProcessingStatus.PURCHASEORDERS_CONFIRMED.equals(getProcessingStatus())));
    }
}
