package io.mateu.erp.model.booking.transfer;

import com.google.common.base.Strings;
import io.mateu.common.model.authentication.Audit;
import io.mateu.erp.model.booking.PurchaseOrder;
import io.mateu.erp.model.booking.PurchaseOrderStatus;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.booking.ValidationStatus;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.importing.TransferBookingRequest;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.partners.Actor;
import io.mateu.erp.model.product.ContractType;
import io.mateu.erp.model.product.transfer.*;
import io.mateu.erp.model.workflow.AbstractTask;
import io.mateu.erp.model.workflow.SMSTask;
import io.mateu.erp.model.workflow.SendEmailTask;
import io.mateu.ui.core.client.views.AbstractListView;
import io.mateu.ui.core.shared.AsyncCallback;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.mdd.server.ERPServiceImpl;
import io.mateu.ui.mdd.server.annotations.*;
import io.mateu.ui.mdd.server.annotations.Parameter;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import io.mateu.ui.mdd.server.workflow.WorkflowEngine;
import io.mateu.ui.mdd.shared.ActionType;
import io.mateu.ui.mdd.shared.MDDLink;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.mail.internet.InternetAddress;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static io.mateu.ui.core.server.BaseServerSideApp.fop;

/**
 * Created by miguel on 25/2/17.
 */
@Entity
@Getter
@Setter
public class TransferService extends Service {

    @Tab("Service")
    @NotNull
    @SearchFilter
    @ListColumn
    private TransferType transferType;

    @Sum
    @NotNull
    @ListColumn
    private int pax;

    @Output
    @SearchFilter
    @ListColumn
    private TransferDirection direction;

    @ManyToOne
    private Vehicle preferredVehicle;

    @Separator("Pickup")
    private String pickupText;
    @ManyToOne
    @SameLine
    private TransferPoint pickup;
    @ManyToOne
    @Output
    @SearchFilter
    @SameLine
    private TransferPoint effectivePickup;

    public void setEffectivePickup(TransferPoint p) {
        this.effectivePickup = p;
    }


    @Separator("Dropoff")
    private String dropoffText;
    @ManyToOne
    @SameLine
    private TransferPoint dropoff;
    @ManyToOne
    @Output
    @SearchFilter
    @SameLine
    private TransferPoint effectiveDropoff;

    public void setEffectiveDropoff(TransferPoint effectiveDropoff) {
        this.effectiveDropoff = effectiveDropoff;
    }

    @Separator("Flight")
    private String flightNumber;
    @NotNull
    @ListColumn
    @SameLine
    private LocalDateTime flightTime;
    @SameLine
    private String flightOriginOrDestination;

    @Separator("Pickup info")
    @ListColumn
    private LocalDateTime pickupTime;
    @Output
    @SameLine
    private LocalDateTime importedPickupTime;


    @Output
    @ListColumn
    private LocalDateTime pickupConfirmedByTelephone;
    @Output
    @SameLine
    @ListColumn
    private LocalDateTime pickupConfirmedByWeb;

    @Output
    @ListColumn
    private LocalDateTime pickupConfirmedByEmailToHotel;
    @Output
    @SameLine
    @ListColumn
    private LocalDateTime pickupConfirmedBySMS;

    @Ignored
    private boolean arrivalNoShow;

    @Ignored
    @ManyToOne
    private TransferPoint airport;

    @ManyToOne
    @Ignored
    private TransferService returnTransfer;

    @ManyToMany
    @Ignored
    private List<AbstractTask> tasks = new ArrayList<>();

    @ManyToOne
    @Ignored
    private TransferBookingRequest transferBookingRequest;


    /*
    private int bikes;
    private int golfBags;
    */

    @Action(name = "Save and return")
    public Data saveAndReturn(UserData user, Data _data) throws Throwable {
        ERPServiceImpl s = new ERPServiceImpl();
        Data data = (Data) s.set(user, TransferService.class.getName(), TransferService.class.getName(), _data);
        Data aux = _data.get("pickupText");
        _data.set("pickupText", _data.get("dropoffText"));
        _data.set("dropoffText", aux);
        aux = _data.get("pickup");
        _data.set("pickup", _data.get("dropoff"));
        _data.set("dropoff", aux);
        _data.remover("flightNumber");
        _data.remover("flightTime");
        _data.remover("flightOriginOrDestination");
        _data.remover("pickupTime");
        _data.remover("pickupConfirmed");
        _data.remover("pickupConfirmedThrough");
        _data.remover("_id");
        //_data.set("_id", null);
        return _data;
    }

    @Action(name = "Test miguel")
    public void testMiguel(UserData user, Data _data) throws Throwable {
        _data.set("office", null);
        ERPServiceImpl s = new ERPServiceImpl();
        Data data = (Data) s.set(user, TransferService.class.getName(), TransferService.class.getName(), _data);
    }



    @Links
    public List<MDDLink> getLinks() {
        List<MDDLink> l = super.getLinks();

        if (getBooking() != null) {

            TransferService r = null;
            for (Service s : getBooking().getServices()) {
                if (s.getId() != getId() && s instanceof TransferService) {
                    r = (TransferService) s;
                    break;
                }
            }
            if (r != null) l.add(new MDDLink((TransferDirection.OUTBOUND.equals(r.getDirection()))?"Outbound":"Inbound", TransferService.class, ActionType.OPENEDITOR, new Data("_id", r.getId())));

        }



        return l;
    }


    @Action(name = "Price")
    public static void price(UserData user, @Selection List<Data> _selection) throws Throwable {
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {
                for (Data d : _selection) {
                    TransferService s = em.find(TransferService.class, d.get("_id"));
                    s.price(em, user);
                }
            }
        });
    }


    @Action(name = "Work list")
    public static URL workList(UserData user, EntityManager em, Data parameters) throws Throwable {
        AbstractListView lv = (AbstractListView) Class.forName("io.mateu.ui.mdd.client.MDDJPACRUDView").getDeclaredConstructor(Data.class).newInstance((Data) new ERPServiceImpl().getMetadaData(user, em, TransferService.class, null));

        return listToPdf(parameters, lv);
    }

    public static URL listToPdf(Data parameters, AbstractListView view)throws Throwable {


        Document xml = new Document();
        Element arrel = new Element("root");
        xml.addContent(arrel);

        arrel.setAttribute("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));


        Element params;
        arrel.addContent(params = new Element("params"));
        for (String k : parameters.getPropertyNames()) if (!"_data".equals(k)) {
            if (parameters.get(k) != null) params.addContent(new Element(k).setText("" + parameters.get(k)));
        }


        String[] xslfo = {""};

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                AppConfig appconfig = AppConfig.get(em);

                xslfo[0] = appconfig.getXslfoForTransfersList();

                arrel.setAttribute("businessName", appconfig.getBusinessName());


                Data[] r = new Data[1];

                view.getForm().setData(parameters);

                view.rpc(parameters, new AsyncCallback<Data>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                    }

                    @Override
                    public void onSuccess(Data result) {
                        r[0] = result;
                    }
                });


                LinkedHashMap<LocalDate, LinkedHashMap<TransferDirection, LinkedHashMap<TransferType, List<TransferService>>>> ss = new LinkedHashMap<>();

                List<Data> data = r[0].getList("_data");
                for (Data d : data) {

                    TransferService s = em.find(TransferService.class, d.get("_id"));

                    if (!s.isCancelled() && !s.isHeld()) {

                        LinkedHashMap<TransferDirection, LinkedHashMap<TransferType, List<TransferService>>> sf = ss.get(s.getStart());
                        if (sf == null) ss.put(s.getStart(), sf = new LinkedHashMap<>());

                        LinkedHashMap<TransferType, List<TransferService>> sd = sf.get(s.getDirection());
                        if (sd == null) sf.put(s.getDirection(), sd = new LinkedHashMap<>());

                        List<TransferService> st = sd.get(s.getTransferType());
                        if (st == null) sd.put(s.getTransferType(), st = new ArrayList<TransferService>());

                        st.add(s);

                    }
                }

                for (LocalDate f : ss.keySet()) for (TransferDirection d: ss.get(f).keySet()) for (TransferType t : ss.get(f).get(d).keySet()) {

                    Element eg;
                    arrel.addContent(eg = new Element("group"));
                    eg.setAttribute("date", "" + f);
                    eg.setAttribute("direction", "" + d);
                    eg.setAttribute("type", "" + t);


                    List<TransferService> ls = ss.get(f).get(d).get(t);

                    Collections.sort(ls, new Comparator<TransferService>() {
                        @Override
                        public int compare(TransferService o1, TransferService o2) {
                            if (TransferDirection.OUTBOUND.equals(d)) {
                                if (o1.getPickupTime() != null && o2.getPickupTime() != null) return o1.getPickupTime().compareTo(o2.getPickupTime());
                                if (o1.getPickupTime() != null && o2.getPickupTime() == null) return -1;
                                if (o1.getPickupTime() == null && o2.getPickupTime() != null) return 1;
                                if (o1.getFlightTime() != null && o2.getFlightTime() != null) return o1.getFlightTime().compareTo(o2.getFlightTime());
                            } else {
                                if (o1.getFlightTime() != null && o2.getFlightTime() != null) return o1.getFlightTime().compareTo(o2.getFlightTime());
                            }
                            return 0;
                        }
                    });


                    int totalPax = 0;
                    for (TransferService s : ls) {

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
                            if (!Strings.isNullOrEmpty(s.getOperationsComment())) {
                                if (!"".equals(comments)) comments += " / ";
                                comments += s.getOperationsComment();
                            }

                            es.setAttribute("comments", comments);
                            es.setAttribute("direction", "" + s.getDirection());
                            es.setAttribute("pax", "" + s.getPax());
                            es.setAttribute("pickup", "" + ((s.getEffectivePickup() != null)?s.getEffectivePickup().getName():s.getPickupText()));
                            if (s.getEffectivePickup() != null && s.getEffectivePickup().getCity().getName() != null) es.setAttribute("pickupResort", s.getEffectivePickup().getCity().getName());
                            if (s.getEffectivePickup() != null && s.getEffectivePickup().getAlternatePointForShuttle() != null && !TransferType.EXECUTIVE.equals(s.getTransferType()) && (TransferType.SHUTTLE.equals(s.getTransferType()) || s.getEffectivePickup().isAlternatePointForNonExecutive())) {
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
        });


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

    @Action(name = "Send pickup times")
    public static void sendPickupTimes(EntityManager em, Data parameters, @Parameter(name = "Email")@NotNull String toEmail, @Parameter(name = "Msg") String msg) throws Throwable {
        AbstractListView view = (AbstractListView) Class.forName("io.mateu.ui.mdd.client.MDDJPACRUDView").getDeclaredConstructor(Data.class).newInstance((Data) new ERPServiceImpl().getMetadaData(null, em, TransferService.class, null));

        Office office = null;
        
        Data[] r = new Data[1];

        view.getForm().setData(parameters);

        view.rpc(parameters, new AsyncCallback<Data>() {
            @Override
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
            }

            @Override
            public void onSuccess(Data result) {
                r[0] = result;
            }
        });


        LinkedHashMap<LocalDate, LinkedHashMap<TransferDirection, LinkedHashMap<TransferType, List<TransferService>>>> ss = new LinkedHashMap<>();

        List<Object[]> hoja = new ArrayList<>();
        hoja.add(new Object[] {
                "po"
                , "ref"
                , "lead name"
                , "pax"
                , "pickup"
                , "pickup resort"
                , "dropoff"
                , "dropoff resort"
                , "transfer type"
                , "vehicle"
                , "flight number"
                , "flight date"
                , "flight time"
                , "flight origin/destination"
                , "pickup date"
                , "pickup time"
                , "comments"
                , "comments"
                , "agency"
                , "agency ref"
        });

        List<Data> data = r[0].getList("_data");
        for (Data d : data) {

            TransferService s = em.find(TransferService.class, d.get("_id"));

            if (!s.isCancelled() && !s.isHeld() && TransferDirection.OUTBOUND.equals(s.getDirection())) {
                
                if (office == null) office = s.getOffice();

                Long poId = null;
                if (s.getPurchaseOrders().size() > 0) poId = s.getPurchaseOrders().get(0).getId();

                hoja.add(new Object[] {
                        poId
                        , s.getId()
                        , s.getBooking().getLeadName()
                        , s.getPax()
                        , (s.getEffectivePickup() != null)?s.getEffectivePickup().getName():s.getPickupText()
                        , (s.getEffectivePickup() != null && s.getEffectivePickup().getCity().getName() != null)?s.getEffectivePickup().getCity().getName():""
                        , (s.getEffectiveDropoff() != null)?s.getEffectiveDropoff().getName():s.getDropoffText()
                        , (s.getEffectiveDropoff() != null && s.getEffectiveDropoff().getCity().getName() != null)?s.getEffectiveDropoff().getCity().getName():""
                        , "" + s.getTransferType()
                        , (s.getPreferredVehicle() != null)?s.getPreferredVehicle().getName():""
                        , s.getFlightNumber()
                        , s.getFlightTime().toLocalDate()
                        , s.getFlightTime().toLocalTime()
                        , s.getFlightOriginOrDestination()
                        , (s.getPickupTime() != null)?s.getPickupTime().toLocalDate():null
                        , (s.getPickupTime() != null)?s.getPickupTime().toLocalTime():null
                        , s.getBooking().getComments()
                        , s.getComment()
                        , s.getBooking().getAgency().getName()
                        , s.getBooking().getAgencyReference()
                });

            }
        }

        File excel = Helper.writeExcel(new Object[][][] {
                hoja.toArray(new Object[0][0])
        });


        AppConfig appconfig = AppConfig.get(em);

        // Create the attachment
//                EmailAttachment attachment = new EmailAttachment();
//                attachment.setPath("mypictures/john.jpg");
//                attachment.setDisposition(EmailAttachment.ATTACHMENT);
//                attachment.setDescription("Picture of John");
//                attachment.setName("John");

        // Create the email message
        HtmlEmail email = new HtmlEmail();
        //Email email = new HtmlEmail();
        email.setHostName((office != null)?office.getEmailHost():appconfig.getAdminEmailSmtpHost());
        email.setSmtpPort((office != null)?office.getEmailPort():appconfig.getAdminEmailSmtpPort());
        email.setAuthenticator(new DefaultAuthenticator((office != null)?office.getEmailUsuario():appconfig.getAdminEmailUser(), (office != null)?office.getEmailPassword():appconfig.getAdminEmailPassword()));
        //email.setSSLOnConnect(true);
        email.setFrom((office != null)?office.getEmailFrom():appconfig.getAdminEmailFrom());
        if (!Strings.isNullOrEmpty((office != null)?office.getEmailCC():appconfig.getAdminEmailCC())) email.getCcAddresses().add(new InternetAddress((office != null)?office.getEmailCC():appconfig.getAdminEmailCC()));

        email.setSubject("PICKUP TIMES");
        if (msg != null) email.setMsg(msg);
        email.addTo((!Strings.isNullOrEmpty(System.getProperty("allemailsto")))?System.getProperty("allemailsto"):toEmail);

        if (excel != null) email.attach(excel);


        email.send();

    }

    @PrePersist@PreUpdate
    public void preSet() throws Error {
        if ((getPickupText() == null || "".equals(getPickupText().trim())) && getPickup() == null) throw new Error("Pickup is required");
        if ((getDropoffText() == null || "".equals(getDropoffText().trim())) && getDropoff() == null) throw new Error("Dropoff is required");

        LocalDate s = getFlightTime().toLocalDate();
        if (getFlightTime().getHour() < 6) s = s.minusDays(1);
        setStart(s);
        setFinish(s);
    }

    @PostUpdate@PostPersist
    public void afterSet() throws Throwable {

        System.out.println("transferservice.afterset()");

        long finalId = getId();

        if (!isPreventAfterSet()) {
            WorkflowEngine.add(new Runnable() {
                @Override
                public void run() {

                    try {
                        Helper.transact(new JPATransaction() {
                            @Override
                            public void run(EntityManager em) throws Throwable {

                                em.find(TransferService.class, finalId).afterSet(em);

                            }
                        });
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }

                }
            });
        }


    }

    public void afterSet(EntityManager em) throws Throwable {

        System.out.println("transferservice " + getId() + ".afterset");

        setAndMapTransferPoints(em);

        TransferDirection d = TransferDirection.POINTTOPOINT;
        if (getEffectivePickup() != null && (TransferPointType.AIRPORT.equals(getEffectivePickup().getType()) || TransferPointType.PORT.equals(getEffectivePickup().getType()))) {
            d = TransferDirection.INBOUND;
            setAirport(getEffectivePickup());
        }
        else if (getEffectiveDropoff() != null && (TransferPointType.AIRPORT.equals(getEffectiveDropoff().getType()) || TransferPointType.PORT.equals(getEffectiveDropoff().getType()))) {
            d = TransferDirection.OUTBOUND;
            setAirport(getEffectiveDropoff());
        }
        if (getAirport() == null && getOffice() != null) {
            setAirport(getOffice().getDefaultAirportForTransfers());
        }

        setDirection(d);

        afterSetAsService(em);

    }

    @Override
    public void validate(EntityManager em) {
        super.validate(em);
        if (ValidationStatus.VALID.equals(getValidationStatus())) {
            if (getEffectivePickup() == null) {
                addValidationMessage("Missing pickup or not mapped");
                setValidationStatus(ValidationStatus.INVALID);
            }
            if (getEffectiveDropoff() == null) {
                addValidationMessage("Missing dropoff or not mapped");
                setValidationStatus(ValidationStatus.INVALID);
            }
            if (getPickupTime() != null && getPickupTime().plusHours(2).isAfter(getFlightTime())) {
                addValidationMessage("Pickup time less than 2 hours before flight time");
                if (ValidationStatus.VALID.equals(getValidationStatus())) setValidationStatus(ValidationStatus.WARNING);
            }
        }
    }

    private void addValidationMessage(String s) {
        if (!Strings.isNullOrEmpty(s)) {
            String x = getValidationMessage();
            if (x == null) x = "";
            if (!"".equals(x)) x += ". ";
            setValidationMessage(x + s);
        }
    }

    @Override
    public boolean isAllMapped(EntityManager em) {
        return getEffectivePickup() != null && getEffectiveDropoff() != null;
    }

    @Override
    public String createSignature() {
        String s = "error when serializing";
        try {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("leadName", getBooking().getLeadName());
            m.put("flightTime", (getFlightTime() != null)?getFlightTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")):"");
            m.put("flightNumber", getFlightNumber());
            m.put("pax", getPax());
            m.put("pickup", "" + getEffectivePickup());
            m.put("dropoff", "" + getEffectiveDropoff());
            m.put("transferType", getTransferType());
            m.put("preferredVehicle", "" + getPreferredVehicle());
            //m.put("pickupTime", getPickupTime());
            m.put("comment", "" + getComment());
            //m.put("held", "" + isHeld());
            m.put("cancelled", "" + isCancelled());
            s = Helper.toJson(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    @Override
    public double rate(EntityManager em, boolean sale, Actor supplier, PrintWriter report) throws Throwable {

        if (isCancelled()) return 0;

        // verificamos que tenemos lo que necesitamos para valorar
        setAndMapTransferPoints(em);

        if (getEffectivePickup() == null) throw new Throwable("Missing pickup. " + getPickupText() + " is not mapped.");
        if (getEffectiveDropoff() == null) throw new Throwable("Missing dropoff. " + getDropoffText() + " is not mapped.");

        // seleccionamos los contratos válidos
        List<Contract> contracts = new ArrayList<>();
        for (Contract c : (List<Contract>) em.createQuery("select x from " + Contract.class.getName() + " x").setFlushMode(FlushModeType.COMMIT).getResultList()) {
            boolean ok = true;
            ok &= (sale && ContractType.SALE.equals(c.getType())) || (!sale     && ContractType.PURCHASE.equals(c.getType()));
            ok &= c.getTargets().size() == 0 || c.getTargets().contains(getBooking().getAgency());
            ok &= supplier == null || supplier.equals(c.getSupplier());
            ok &= getTransferType().equals(c.getTransferType());
            ok &= c.getValidFrom().isBefore(getStart()) || c.getValidFrom().equals(getStart());
            ok &= c.getValidTo().isAfter(getFinish()) || c.getValidTo().equals(getFinish());
            LocalDate created = (getAudit() != null && getAudit().getCreated() != null)?getAudit().getCreated().toLocalDate():LocalDate.now();
            ok &= c.getBookingWindowFrom() == null || c.getBookingWindowFrom().isBefore(created) || c.getBookingWindowFrom().equals(created);
            ok &= c.getBookingWindowTo() == null || c.getBookingWindowTo().isAfter(created) || c.getBookingWindowTo().equals(created);
            if (ok) contracts.add(c);
        }
        if (contracts.size() == 0) throw new Exception("No valid contract");

        List<Contract> propietaryContracts = contracts.stream().filter((c) -> c.getTargets().size() > 0).collect(Collectors.toList());

        if (propietaryContracts.size() > 0) contracts = propietaryContracts;

        List<Price> prices = new ArrayList<>();
        for (Contract c : contracts) for (Price p : c.getPrices()) {
            boolean ok = true;
            ok &= ((p.getOrigin().getCities().contains(getEffectivePickup().getCity()) || p.getOrigin().getPoints().contains(getEffectivePickup()))
                    && (p.getDestination().getCities().contains(getEffectiveDropoff().getCity()) || p.getDestination().getPoints().contains(getEffectiveDropoff())))
                    ||
                    ((p.getOrigin().getCities().contains(getEffectiveDropoff().getCity()) || p.getOrigin().getPoints().contains(getEffectiveDropoff()))
                            && (p.getDestination().getCities().contains(getEffectivePickup().getCity()) || p.getDestination().getPoints().contains(getEffectivePickup())));
            ok &= p.getVehicle().getMinPax() <= getPax();
            ok &= p.getVehicle().getMaxPax() >= getPax();
            if (ok) prices.add(p);
        }
        if (prices.size() == 0) throw new Exception("No valid price in selectable contracts");

        // valoramos con cada uno de ellos y nos quedamos con el precio más económico
        double value = Double.MAX_VALUE;
        Price bestPrice = null;
        for (Price p : prices) {
            double v = p.getPrice();
            if (PricePer.PAX.equals(p.getPricePer())) {
                int pax = getPax();
                if (p.getContract().getMinPaxPerBooking() > pax) pax = p.getContract().getMinPaxPerBooking();
                v = pax * p.getPrice();
            }
            if (v < value) {
                value = v;
                bestPrice = p;
            }
        }

        if (bestPrice != null) {
            report.print("Used price from " + bestPrice.getOrigin().getName() + " to " + bestPrice.getDestination().getName() + " in " + bestPrice.getVehicle().getName() + " from contract " + bestPrice.getContract().getTitle());
        }

        return value;
    }

    @Override
    public Actor findBestProvider(EntityManager em) throws Throwable {
        // verificamos que tenemos lo que necesitamos para valorar

        setAndMapTransferPoints(em);

        if (getEffectivePickup() == null) throw new Throwable("Missing pickup. " + getPickupText() + " is not mapped.");
        if (getEffectiveDropoff() == null) throw new Throwable("Missing dropoff. " + getDropoffText() + " is not mapped.");

        // seleccionamos los contratos válidos
        List<Contract> contracts = new ArrayList<>();
        for (Contract c : (List<Contract>) em.createQuery("select x from " + Contract.class.getName() + " x").setFlushMode(FlushModeType.COMMIT).getResultList()) {
            boolean ok = true;
            ok &= ContractType.PURCHASE.equals(c.getType());
            ok &= c.getTargets().size() == 0 || c.getTargets().contains(getBooking().getAgency());
            ok &= getTransferType().equals(c.getTransferType());
            ok &= !c.getValidFrom().isAfter(getStart());
            ok &= c.getValidTo().isAfter(getFinish());
            LocalDate created = (getAudit() != null && getAudit().getCreated() != null)?getAudit().getCreated().toLocalDate():LocalDate.now();
            ok &= c.getBookingWindowFrom() == null || c.getBookingWindowFrom().isBefore(created) || c.getBookingWindowFrom().equals(created);
            ok &= c.getBookingWindowTo() == null || c.getBookingWindowTo().isAfter(created) || c.getBookingWindowTo().equals(created);
            if (ok) contracts.add(c);
        }
        if (contracts.size() == 0) throw new Exception("No valid contract");

        List<Price> prices = new ArrayList<>();
        for (Contract c : contracts) for (Price p : c.getPrices()) {
            boolean ok = true;
            ok &= ((p.getOrigin().getCities().contains(getEffectivePickup().getCity()) || p.getOrigin().getPoints().contains(getEffectivePickup()))
                    && (p.getDestination().getCities().contains(getEffectiveDropoff().getCity()) || p.getDestination().getPoints().contains(getEffectiveDropoff())))
                    ||
                    ((p.getOrigin().getCities().contains(getEffectiveDropoff().getCity()) || p.getOrigin().getPoints().contains(getEffectiveDropoff()))
                            && (p.getDestination().getCities().contains(getEffectivePickup().getCity()) || p.getDestination().getPoints().contains(getEffectivePickup())));
            ok &= p.getVehicle().getMinPax() <= getPax();
            ok &= p.getVehicle().getMaxPax() >= getPax();
            if (ok) prices.add(p);
        }
        if (prices.size() == 0) throw new Exception("No valid price in selectable contracts");

        // valoramos con cada uno de ellos y nos quedamos con el precio más económico
        double value = Double.MAX_VALUE;
        Actor provider = null;
        for (Price p : prices) {
            double v = p.getPrice();
            if (PricePer.PAX.equals(p.getPricePer())) v = getPax() * p.getPrice();
            if (v < value) {
                value = v;
                provider = p.getContract().getSupplier();
            }
        }

        return provider;
    }

    public Map<String,Object> getData() {
        Map<String, Object> d = super.getData();

        d.put("id", getId());
        d.put("locator", getBooking().getId());
        d.put("leadName", getBooking().getLeadName());
        d.put("agency", getBooking().getAgency().getName());
        d.put("agencyReference", getBooking().getAgencyReference());
        d.put("status", (isCancelled())?"CANCELLED":"ACTIVE");
        d.put("created", getAudit().getCreated().format(DateTimeFormatter.BASIC_ISO_DATE.ISO_DATE_TIME));
        d.put("office", getOffice().getName());

        d.put("comments", getComment());
        d.put("direction", "" + getDirection());
        d.put("pax", getPax());

        TransferPoint p = getEffectivePickup();
        if (p != null && p.getAlternatePointForShuttle() != null && !TransferType.EXECUTIVE.equals(getTransferType()) && (TransferType.SHUTTLE.equals(getTransferType()) || p.isAlternatePointForNonExecutive())) {
            p = p.getAlternatePointForShuttle();
            d.put("alternatePickup", "" + p.getName());

            d.put("pickupPointInfo", "" + p.getName());
        } else {
            d.put("pickupPointInfo", "" + ((p != null)?getEffectivePickup().getName():getPickupText()));
        }

        d.put("pickup", "" + ((p != null)?getEffectivePickup().getName():getPickupText()));
        d.put("pickupResort", "" + ((getEffectivePickup() != null)?getEffectivePickup().getCity().getName():""));
        d.put("dropoff", "" + ((getEffectiveDropoff() != null)?getEffectiveDropoff().getName():getDropoffText()));
        d.put("dropoffResort", "" + ((getEffectiveDropoff() != null)?getEffectiveDropoff().getCity().getName():""));
        d.put("providers", getProviders());
        d.put("pickupDate", (getPickupTime() != null)?getPickupTime().format(DateTimeFormatter.ofPattern("E dd MMM")):"");
        d.put("pickupDate_es", (getPickupTime() != null)?getPickupTime().format(DateTimeFormatter.ofPattern("E dd MMM", new Locale("es", "ES"))):"");
        d.put("pickupTime", (getPickupTime() != null)?getPickupTime().format(DateTimeFormatter.ofPattern("HH:mm")):"");
        d.put("transferType", "" + getTransferType());
        d.put("flight", getFlightNumber());
        d.put("flightDate", getFlightTime().format(DateTimeFormatter.ofPattern("yyyy-MMM-dd")));
        d.put("flightDate_es", getFlightTime().format(DateTimeFormatter.ofPattern("yyyy-MMM-dd", new Locale("es", "ES"))));
        d.put("flightTime", getFlightTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        d.put("flightOriginOrDestination", getFlightOriginOrDestination());
        d.put("preferredVehicle", (getPreferredVehicle() != null)?getPreferredVehicle().getName():"");

        return d;
    }

    private void setAndMapTransferPoints(EntityManager em) {
        TransferPoint p = null;
        if (getPickup() != null) p = getPickup();

        TransferPoint d = null;
        if (getDropoff() != null) d = getDropoff();


        if (getPickup() == null) p = TransferPointMapping.getTransferPoint(em, getPickupText(), this);
        if (getDropoff() == null) d = TransferPointMapping.getTransferPoint(em, getDropoffText(), this);

        setEffectivePickup(p);
        setEffectiveDropoff(d);

    }



    @Subtitle
    public String getSubitle() {
        String s = super.toString();
        TransferService r = null;
        if (getBooking() != null) for (Service sv : getBooking().getServices()) {
            if (sv.getId() != getId() && sv instanceof TransferService) {
                r = (TransferService) sv;
                break;
            }
        }
        s += ". ";
        if (r != null) {
            if (TransferDirection.OUTBOUND.equals(r.getDirection())) s += "Returns " + r.getFlightTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")) + ". ";
            else  s += "Arrives " + r.getFlightTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")) + ". ";
        } else s += "This is the only service in this booking. ";
        if (isArrivalNoShow()) {
            s += "Arrival was no show. ";
        }
        return s;
    }

    @Action(name = "Inform pickup time")
    public static void informPickupTimeBatch(UserData user, EntityManager em, @Selection List<Data> selection) throws Throwable {
        for (Data d : selection) {
            TransferService s = em.find(TransferService.class, d.get("_id"));

            s.informPickupTime(user, em);

        }
    }


    @Action(name = "Mark as purchased")
    public static void markAsConfirmed(UserData user, EntityManager em, @Selection List<Data> selection) throws Throwable {
        for (Data d : selection) {
            TransferService s = em.find(TransferService.class, d.get("_id"));

            s.setAlreadyPurchased(true);

        }
    }

    @Action(name = "Inform pickup time")
    public void informPickupTime(UserData user, EntityManager em) throws Throwable {
        if (getPickupTime() != null) {
            long tel = 0;
            try {
                tel = Long.parseLong(getBooking().getTelephone().replaceAll("[\\(\\)\\+]", ""));
            } catch (Exception e) {

            }
            if (tel > 0 && AppConfig.get(em).isClickatellEnabled() && !Strings.isNullOrEmpty(AppConfig.get(em).getClickatellApiKey())) {
                SMSTask t = new SMSTask(tel, Helper.freemark((("" + tel).startsWith("34"))?AppConfig.get(em).getPickupSmsTemplateEs():AppConfig.get(em).getPickupSmsTemplate(), getData()));
                getTasks().add(t);
                t.getServices().add(this);
                t.setAudit(new Audit(em.find(io.mateu.erp.model.authentication.User.class, user.getLogin())));
                //t.run(em, em.find(User.class, user.getLogin()));
                setPickupConfirmedBySMS(LocalDateTime.now());
                em.persist(t);
            }

            if (getEffectivePickup() != null && !Strings.isNullOrEmpty(getEffectivePickup().getEmail())) {
                TransferPoint p = getEffectivePickup();
                if (p.getAlternatePointForShuttle() != null && !TransferType.EXECUTIVE.equals(getTransferType()) && (TransferType.SHUTTLE.equals(getTransferType()) || p.isAlternatePointForNonExecutive())) {
                    p = p.getAlternatePointForShuttle();
                }
                SendEmailTask t = new SendEmailTask();
                t.setOffice(getOffice());
                t.setAudit(new Audit(em.find(io.mateu.erp.model.authentication.User.class, user.getLogin())));
                t.setCc(getOffice().getEmailCC());
                t.setMessage(Helper.freemark(AppConfig.get(em).getPickupEmailTemplate(), getData()));
                t.setSubject("TRANSFER PICKUP INFORMATION FOR " + getBooking().getLeadName());
                t.setTo(getEffectivePickup().getEmail());
                //t.run(em, em.find(User.class, user.getLogin()));
                getTasks().add(t);
                t.getServices().add(this);
                em.persist(t);
                setPickupConfirmedByEmailToHotel(LocalDateTime.now());
            }
        }
    }

    @Action(name = "Send email to hotel")
    public void sendEmailToHotel(UserData user, EntityManager em) throws Throwable {
        if (getPickupTime() != null) {

            if (getEffectivePickup() != null && !Strings.isNullOrEmpty(getEffectivePickup().getEmail())) {
                TransferPoint p = getEffectivePickup();
                if (p.getAlternatePointForShuttle() != null && !TransferType.EXECUTIVE.equals(getTransferType()) && (TransferType.SHUTTLE.equals(getTransferType()) || p.isAlternatePointForNonExecutive())) {
                    p = p.getAlternatePointForShuttle();
                }
                SendEmailTask t = new SendEmailTask();
                t.setOffice(getOffice());
                t.setAudit(new Audit(em.find(io.mateu.erp.model.authentication.User.class, user.getLogin())));
                t.setCc(getOffice().getEmailCC());
                t.setMessage(Helper.freemark(AppConfig.get(em).getPickupEmailTemplate(), getData()));
                t.setSubject("TRANSFER PICKUP INFORMATION FOR " + getBooking().getLeadName());
                t.setTo(getEffectivePickup().getEmail());
                //t.run(em, em.find(User.class, user.getLogin()));
                getTasks().add(t);
                t.getServices().add(this);
                em.persist(t);
                setPickupConfirmedByEmailToHotel(LocalDateTime.now());
            } else throw new Exception("No effective pickup or missing email for it. Please set before sending the email");
        } else throw new Exception("No pickup time. Please set before sending the email");
    }

    @Action(name = "Send SMS")
    public void sendSMS(UserData user, EntityManager em) throws Throwable {
        if (getPickupTime() != null) {
            long tel = 0;
            try {
                tel = Long.parseLong(getBooking().getTelephone().replaceAll("[\\(\\)\\+]", ""));
            } catch (Exception e) {

            }
            if (tel > 0 && AppConfig.get(em).isClickatellEnabled() && !Strings.isNullOrEmpty(AppConfig.get(em).getClickatellApiKey())) {
                SMSTask t = new SMSTask(tel, Helper.freemark((("" + tel).startsWith("34"))?AppConfig.get(em).getPickupSmsTemplateEs():AppConfig.get(em).getPickupSmsTemplate(), getData()));
                getTasks().add(t);
                t.setAudit(new Audit(em.find(io.mateu.erp.model.authentication.User.class, user.getLogin())));
                //t.run(em, em.find(User.class, user.getLogin()));
                setPickupConfirmedBySMS(LocalDateTime.now());
                em.persist(t);
            } else throw new Exception("No telephone or clickatell api. Please set before sending the sms");
        } else throw new Exception("No pickup time. Please set before sending the sms");
    }

    @Action(name = "Test Email")
    public void testEmail(UserData user, EntityManager em, @Parameter(name = "your email") String email) throws Throwable {
        if (getPickupTime() != null) {

            AppConfig appconfig = AppConfig.get(em);

            if (getEffectivePickup() != null) {
                TransferPoint p = getEffectivePickup();
                if (p.getAlternatePointForShuttle() != null && !TransferType.EXECUTIVE.equals(getTransferType()) && (TransferType.SHUTTLE.equals(getTransferType()) || p.isAlternatePointForNonExecutive())) {
                    p = p.getAlternatePointForShuttle();
                }
                SendEmailTask t = new SendEmailTask();
                t.setOffice(getOffice());
                t.setAudit(new Audit(em.find(io.mateu.erp.model.authentication.User.class, user.getLogin())));
                t.setCc(getOffice().getEmailCC());
                t.setMessage(Helper.freemark(AppConfig.get(em).getPickupEmailTemplate(), getData()));
                t.setSubject("TRANSFER PICKUP INFORMATION FOR " + getBooking().getLeadName());
                t.setTo(email);
                //t.run(em, em.find(User.class, user.getLogin()));
                getTasks().add(t);
                t.getServices().add(this);
                em.persist(t);
                setPickupConfirmedByEmailToHotel(LocalDateTime.now());
            } else throw new Exception("No effective pickup. Please set before sending the email");
        } else throw new Exception("No pickup time. Please set before sending the email");
    }

    @Action(name = "Test SMS")
    public void testPickupTime(UserData user, EntityManager em, @Parameter(name = "mobile nr. (34628...)") String sms) throws Throwable {
        if (getPickupTime() != null) {

            AppConfig appconfig = AppConfig.get(em);

            long tel = 0;
            try {
                tel = Long.parseLong(sms.replaceAll("[\\(\\)\\+]", ""));
            } catch (Exception e) {

            }
            if (tel > 0 && !Strings.isNullOrEmpty(appconfig.getClickatellApiKey())) {
                SMSTask t = new SMSTask(tel, Helper.freemark((("" + tel).startsWith("34"))?AppConfig.get(em).getPickupSmsTemplateEs():AppConfig.get(em).getPickupEmailTemplate(), getData()));
                getTasks().add(t);
                t.getServices().add(this);
                t.setAudit(new Audit(em.find(io.mateu.erp.model.authentication.User.class, user.getLogin())));
                //t.run(em, em.find(User.class, user.getLogin()));
                em.persist(t);
            } else throw new Exception("No telephone or clickatell api. Please set before sending the sms");
        } else throw new Exception("No pickup time. Please set before sending the sms");
    }


    @Action(name = "Solo miguel")
    public static void repair() throws Throwable {

        List<Long> ids = new ArrayList<>();

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

//                for (TransferService s : (List<TransferService>) em.createQuery("select x from " + TransferService.class.getName() + " x where x.audit.created >= :f").setFlushMode(FlushModeType.COMMIT).setParameter("f", LocalDateTime.of(2018, 4, 14, 0, 0)).getResultList()) {
                for (TransferService s : (List<TransferService>) em.createQuery("select x from " + TransferService.class.getName() + " x").setFlushMode(FlushModeType.COMMIT).getResultList()) {
                    ids.add(s.getId());
                }
            }
        });


        for (long id : ids) {
            try {
                Helper.transact(new JPATransaction() {
                    @Override
                    public void run(EntityManager em) throws Throwable {
                        TransferService s = em.find(TransferService.class, id);
                        s.setPreventAfterSet(true);
                        s.setSignature(s.createSignature());
                    }
                });
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }



        ids.clear();

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                //for (PurchaseOrder s : (List<PurchaseOrder>) em.createQuery("select x from " + PurchaseOrder.class.getName() + " x where (x.audit.created >= :f or x.sentTime > :f)").setFlushMode(FlushModeType.COMMIT).setParameter("f", LocalDateTime.of(2018, 4, 14, 0, 0)).getResultList()) {
                for (PurchaseOrder s : (List<PurchaseOrder>) em.createQuery("select x from " + PurchaseOrder.class.getName() + " x").setFlushMode(FlushModeType.COMMIT).getResultList()) {
                    ids.add(s.getId());
                }
            }
        });


        for (long id : ids) {
            try {
                Helper.transact(new JPATransaction() {
                    @Override
                    public void run(EntityManager em) throws Throwable {
                        PurchaseOrder s = em.find(PurchaseOrder.class, id);
                        if (PurchaseOrderStatus.CONFIRMED.equals(s.getStatus())) {
                            s.setPreventAfterSet(true);
                            s.setSignature(s.createSignature());
                        }
                    }
                });
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

    }

}
