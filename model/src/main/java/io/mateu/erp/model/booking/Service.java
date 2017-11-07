package io.mateu.erp.model.booking;

import com.google.common.base.Strings;
import io.mateu.erp.model.authentication.Audit;
import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.booking.generic.PriceDetail;
import io.mateu.erp.model.booking.transfer.TransferDirection;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.financials.PurchaseOrderSendingMethod;
import io.mateu.erp.model.mdd.*;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.product.transfer.TransferType;
import io.mateu.erp.model.workflow.*;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.mdd.server.annotations.*;
import io.mateu.ui.mdd.server.annotations.Parameter;
import io.mateu.ui.mdd.server.interfaces.WithTriggers;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import io.mateu.ui.mdd.shared.ActionType;
import io.mateu.ui.mdd.shared.MDDLink;
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

import static io.mateu.ui.core.server.BaseServerSideApp.fop;

/**
 * Created by miguel on 31/1/17.
 */
@Entity
@Getter
@Setter
public abstract class Service implements WithTriggers {

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
    @SearchFilter(value="Booking Id", field = "id")
    @SearchFilter(field = "agencyReference")
    @SearchFilter(field = "agency")
    @ListColumn(value="Boking", field = "id")
    @ListColumn(field = "agencyReference", width = 150)
    @ListColumn(field = "agency", width = 150)
    @SearchFilter(field = "leadName")
    @ListColumn(field = "leadName")
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

    @ListColumn
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
    private String privateComment;

    @Tab("Change log")
    @Output
    private String changeLog;


    @Tab("Price")
    private boolean valueOverrided;
    @SameLine
    private double overridedNetValue;
    @SameLine
    private double overridedRetailValue;
    @SameLine
    private double overridedCommissionValue;


    @Output
    @ListColumn
    @CellStyleGenerator(ValuedCellStyleGenerator.class)
    private boolean valued;

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
    @ListColumn
    @CellStyleGenerator(ValuedCellStyleGenerator.class)
    private boolean purchaseValued;

    @Output
    @ListColumn
    private double totalCost;

    @Tab("Handling")
    @Ignored
    private int effectiveProcessingStatus;

    @Tab("Purchase")
    @ManyToOne
    private Actor preferredProvider;

    private boolean alreadyPurchased;


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
    @OneToMany
    private List<PriceDetail> priceBreakdown = new ArrayList<>();

    @SearchFilter(value="Purchase Order Id", field = "id")
    @ManyToMany
    @NotInEditor
    private List<PurchaseOrder> purchaseOrders = new ArrayList<>();


    @Ignored
    private String signature;

    @Transient
    @Ignored
    private String signatureBefore;

    public void updateProcessingStatus(EntityManager em) {
        if (isAlreadyPurchased()) {
            setProcessingStatus(ProcessingStatus.PURCHASEORDERS_CONFIRMED);
        } else if (getFinish() != null && getFinish().isAfter(LocalDate.now())) {
            setProcessingStatus(ProcessingStatus.INITIAL);
            if (isCancelled() && getSentToProvider() == null) setProcessingStatus(ProcessingStatus.PURCHASEORDERS_CONFIRMED);
            else if (isAllMapped(em)) {
                setProcessingStatus(ProcessingStatus.DATA_OK);

                if (getPurchaseOrders().size() > 0) {
                    setProcessingStatus(ProcessingStatus.PURCHASEORDERS_READY);

                    boolean allSent = true;

                    for (PurchaseOrder po : getPurchaseOrders()) if (!po.isCancelled()) if (!po.isSent()) allSent = false;

                    if (allSent) {
                        setProcessingStatus(ProcessingStatus.PURCHASEORDERS_SENT);

                        boolean allConfirmed = true;
                        boolean anyRejected = false;

                        for (PurchaseOrder po : getPurchaseOrders()) if (!po.isCancelled()) {
                            if (!PurchaseOrderStatus.CONFIRMED.equals(po.getStatus())) allConfirmed = false;
                            if (PurchaseOrderStatus.REJECTED.equals(po.getStatus())) anyRejected = true;
                        }

                        if (allConfirmed) setProcessingStatus(ProcessingStatus.PURCHASEORDERS_CONFIRMED);
                        else if (anyRejected) setProcessingStatus(ProcessingStatus.PURCHASEORDERS_REJECTED);

                    }

                }

            }
        } else {
            // finished service. Nothing to do
        }
    }

    public boolean isAllMapped(EntityManager em) {
        return true;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new HashMap<>();
        m.put("cancelled", isCancelled());
        m.put("comment", getComment());
        if (getPreferredProvider() != null) m.put("preferredprovider", getPreferredProvider().getName());
        m.put("start", getStart());
        m.put("finish", getFinish());
        return m;
    }


    @Action(name = "Repair signature")
    public static void repairSignature(@Selection List<Data> _selection) throws Throwable {
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                if (_selection != null && _selection.size() > 0) {

                    for (Data d : _selection) {
                        Service s = em.find(Service.class, d.get("_id"));
                        //s.validate(em);

//                    if (s instanceof TransferService) {
//                        TransferService t = (TransferService) s;
//                        LocalDate z = t.getFlightTime().toLocalDate();
//                        if (t.getFlightTime().getHour() < 6) z = z.minusDays(1);
//                        t.setStart(z);
//                        t.setFinish(z);
//                    }

                        s.setSignature(s.createSignature());
                    }

                } else {


                    List<Service> l = em.createQuery("select x from " + Service.class.getName() + " x order by x.id").getResultList();

                    for (Service s : l) {
                        s.setSignature(s.createSignature());
                    }

                }

            }
        });
    }

    @Action(name = "Repair bookings")
    public static void repair(UserData user) throws Throwable {
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                System.out.println("***REPARAR BOOKINGS*****");

                Object[][] r = Helper.select("select purchaseorders_id from (select purchaseorders_id, count(*) from service_purchaseorder group by 1 having count(*) > 1) x");

                Helper.transact(new JPATransaction() {
                    @Override
                    public void run(EntityManager em) throws Throwable {
                        for (Object[] l : r) {
                            long id = new Long("" + l[0]);
                            PurchaseOrder po = em.find(PurchaseOrder.class, id);
                            System.out.println("***revisando po " + id + "*****");
                            if (po.getServices().size() > 1) {
                                System.out.println("***po " + id + " tiene más de 1 servicio*****");
                                List<Service> x = new ArrayList<>();
                                for (Service s : po.getServices()) {
                                    if (s instanceof TransferService && TransferDirection.OUTBOUND.equals(((TransferService) s).getDirection())) {
                                        x.add(s);
                                    }
                                }
                                for (Service s : x) {
                                    System.out.println("***corrigiendo servicio " + s.getId() + "*****");
                                    po.getServices().remove(s);
                                    s.getPurchaseOrders().remove(po);
                                    s.checkPurchase(em, user);
                                }
                            }
                        }
                    }
                });

            }
        });
    }

    @Action(name = "Send to provider")
    public static void sendToProvider(EntityManager em, UserData user, @Selection List<Data> selection, @Parameter(name = "Provider") Actor provider, @Parameter(name = "Email") String email, @Parameter(name = "Postscript") @TextArea String postscript) {
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
        Map<Actor, SendPurchaseOrdersTask> taskPerProvider = new HashMap<>();
        User u = em.find(User.class, user.getLogin());
        for (Data d : selection) {
            Service s = em.find(Service.class, d.get("_id"));
            if (provider != null) s.setPreferredProvider(provider);
            for (PurchaseOrder po : s.getPurchaseOrders()) {
                SendPurchaseOrdersTask t = taskPerProvider.get(po.getProvider());
                if (t == null) {
                    if (PurchaseOrderSendingMethod.QUOONAGENT.equals(provider.getOrdersSendingMethod())) {
                        taskPerProvider.put(po.getProvider(), t = new SendPurchaseOrdersToAgentTask());
                        em.persist(t);
                        t.setOffice(s.getOffice());
                        t.setProvider(po.getProvider());
                        t.setStatus(TaskStatus.PENDING);
                        t.setAudit(new Audit(u));
                        ((SendPurchaseOrdersToAgentTask)t).setAgent(provider.getAgent());
                        t.setPostscript(postscript);
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
                try {
                    po.afterSet(em, false);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
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
        if (false && !isAlreadyPurchasedBefore() && isAlreadyPurchased()) {
            setSignature(createSignature());
        } else if (getPurchaseOrders().size() == 0 || getSignature() == null || !getSignature().equals(createSignature())) {
            setSignature(createSignature());

            generatePurchaseOrders(em);
            for (PurchaseOrder po : getPurchaseOrders()) {
                if (po.getSignature() == null || !po.getSignature().equals(po.createSignature())) po.setSent(false);
            }
            for (PurchaseOrder po : getPurchaseOrders()) {
                if (!isAlreadyPurchased() && !po.isSent() && po.getProvider() != null && po.getProvider().isAutomaticOrderSending()) {
                    try {
                        po.send(em, u);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            String ps = "";
            for (PurchaseOrder po : getPurchaseOrders()) {
                if (!"".equals(ps)) ps += ",";
                ps += po.getProvider().getName();
            }
            setProviders(ps);

        } else {
            throw new Throwable("Nothing changed. No need to purchase again");
        }

    }


    @Action(name = "Purchase")
    public void checkPurchase(EntityManager em, UserData user) throws Throwable {
        checkPurchase(em, em.find(User.class, user.getLogin()));
    }


    public void price(EntityManager em, User u) {
        setValued(false);
        setTotalNetValue(0);
        if (isValueOverrided()) {
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
            e.printStackTrace();
        }

        double totalCost = 0;
        boolean purchaseValued = getPurchaseOrders().size() > 0;
        for (PurchaseOrder po : getPurchaseOrders()) {
            if (isCancelled() || po.isCancelled()) {
                po.setValued(true);
                po.setTotal(0);
            } else {
                try {
                    po.price(em);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            if (po.isValued()) totalCost += po.getTotal();
            purchaseValued &= po.isValued();
        }
        setTotalCost(Helper.roundOffEuros(totalCost));
        setPurchaseValued(purchaseValued);
    }

    @Action(name = "Price")
    public void price(EntityManager em, UserData user) {
        price(em, em.find(User.class, user.getLogin()));
    }

    @Action(name = "Print POs")
    public URL printOrders(EntityManager em) throws Throwable {

        Document xml = new Document();
        Element arrel = new Element("root");
        xml.addContent(arrel);

        arrel.setAttribute("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        String[] xslfo = {""};

        AppConfig appconfig = AppConfig.get(em);

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
                            es.setAttribute("agency", "" + s.getBooking().getAgency().getName());
                            if (s.getBooking().getAgencyReference() != null) es.setAttribute("agencyReference", s.getBooking().getAgencyReference());
                            es.setAttribute("leadName", "" + s.getBooking().getLeadName());
                            String comments = "";
                            if (s.getBooking().getComments() != null) comments += s.getBooking().getComments();
                            if (s.getComment() != null) comments += s.getComment();
                            es.setAttribute("comments", comments);
                            es.setAttribute("direction", "" + s.getDirection());
                            es.setAttribute("pax", "" + s.getPax());
                            es.setAttribute("pickup", "" + ((s.getEffectivePickup() != null)?s.getEffectivePickup().getName():s.getPickupText()));
                            if (s.getEffectivePickup() != null && s.getEffectivePickup().getCity().getName() != null) es.setAttribute("pickupResort", s.getEffectivePickup().getCity().getName());
                            if (TransferType.SHUTTLE.equals(s.getTransferType()) && s.getEffectivePickup() != null && s.getEffectivePickup().getAlternatePointForShuttle() != null) {
                                es.setAttribute("alternatePickup", "" + s.getEffectivePickup().getAlternatePointForShuttle().getName());
                            }
                            es.setAttribute("dropoff", "" + ((s.getEffectiveDropoff() != null)?s.getEffectiveDropoff().getName():s.getDropoffText()));
                            if (s.getEffectiveDropoff() != null && s.getEffectiveDropoff().getCity().getName() != null) es.setAttribute("dropoffResort", s.getEffectiveDropoff().getCity().getName());
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

        File temp = (System.getProperty("tmpdir") == null)?File.createTempFile(archivo, ".pdf"):new File(new File(System.getProperty("tmpdir")), archivo + ".pdf");


        System.out.println("java.io.tmpdir=" + System.getProperty("java.io.tmpdir"));
        System.out.println("Temp file : " + temp.getAbsolutePath());

        FileOutputStream fileOut = new FileOutputStream(temp);
        String sxml = new XMLOutputter(Format.getPrettyFormat()).outputString(xml);
        System.out.println("xslfo=" + xslfo[0]);
        System.out.println("xml=" + sxml);
        fileOut.write(fop(new StreamSource(new StringReader(xslfo[0])), new StreamSource(new StringReader(sxml))));
        fileOut.close();

        String baseUrl = System.getProperty("tmpurl");
        if (baseUrl == null) {
            return temp.toURI().toURL();
        }
        return new URL(baseUrl + "/" + temp.getName());
    }

    @Action(name = "Show signature")
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
        l.add(new MDDLink("Booking", Booking.class, ActionType.OPENEDITOR, new Data("_id", getBooking().getId())));
        l.add(new MDDLink("Tasks", AbstractTask.class, ActionType.OPENLIST, new Data("services.id", getId())));
        l.add(new MDDLink("Purchase orders", PurchaseOrder.class, ActionType.OPENLIST, new Data("services.id", getId())));
        return l;
    }


    public double rate(EntityManager em, boolean sale, PrintWriter report) throws Throwable {
        return rate(em, sale, null, report);
    }

    public abstract double rate(EntityManager em, boolean sale, Actor supplier, PrintWriter report) throws Throwable;


    public void generatePurchaseOrders(EntityManager em) throws Throwable {
        Actor provider = (getPreferredProvider() != null)?getPreferredProvider():findBestProvider(em);
        if (provider == null) throw new Throwable("Preferred provider needed for service " + getId());
        if (isHeld()) throw new Throwable("Service " + getId() + " is held");
        if (isCancelled() && getSentToProvider() == null) throw new Throwable("Cancelled and was never sent");
        if (!ProcessingStatus.PURCHASEORDERS_CONFIRMED.equals(getProcessingStatus())) {
            PurchaseOrder po = null;
            if (getPurchaseOrders().size() > 0) {
                po = getPurchaseOrders().get(getPurchaseOrders().size() - 1);
                if (!getPreferredProvider().equals(po.getProvider())) {
                    po.cancel(em);
                    po = null;
                }
            }
            if (po == null) {
                po = new PurchaseOrder();
                em.persist(po);
                po.setAudit(new Audit());
                po.getServices().add(this);
                getPurchaseOrders().add(po);
                po.setStatus(PurchaseOrderStatus.PENDING);
            }
            po.setOffice(getOffice());
            po.setProvider(getPreferredProvider());
            po.setCurrency(getPreferredProvider().getCurrency());
        }
    }

    public abstract Actor findBestProvider(EntityManager em) throws Throwable;


    @Override
    public String toString() {
        String s = "";
        if (getAudit() != null) s += getAudit();
        return s;
    }


    public static void main(String... args) {

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
        d.put("locator", getBooking().getId());
        d.put("agency", getBooking().getAgency().getName());
        d.put("agencyReference", getBooking().getAgencyReference());
        d.put("status", (isCancelled())?"CANCELLED":"ACTIVE");
        d.put("created", getAudit().getCreated().format(DateTimeFormatter.BASIC_ISO_DATE.ISO_DATE_TIME));
        d.put("office", getOffice().getName());

        d.put("start", getStart());
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

    public void cancel(EntityManager em) {
        setCancelled(true);
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

    @PreUpdate
    public void preStore() {
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


    @Override
    public void afterSet(EntityManager em, boolean isNew) throws Throwable {
        try {
            price(em, getAudit().getModifiedBy());
            checkPurchase(em, getAudit().getModifiedBy());
        } catch (Throwable e) {
            e.printStackTrace();
        }

        updateProcessingStatus(em);
        validate(em);
    }
}
