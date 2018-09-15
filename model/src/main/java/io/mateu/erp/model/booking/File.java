package io.mateu.erp.model.booking;

import com.google.common.base.Strings;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Grid;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.themes.ValoTheme;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.invoicing.BookingCharge;
import io.mateu.erp.model.payments.Payment;
import io.mateu.erp.model.tpv.TPV;
import io.mateu.erp.model.tpv.TPVTransaction;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.interfaces.GridDecorator;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.User;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.importing.TransferBookingRequest;
import io.mateu.erp.model.invoicing.Charge;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.payments.BookingPaymentAllocation;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.app.ActionType;
import io.mateu.mdd.core.app.MDDLink;
import io.mateu.mdd.core.data.Data;
import io.mateu.mdd.core.data.Pair;
import io.mateu.mdd.core.model.config.Template;
import io.mateu.mdd.core.model.util.EmailHelper;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.javamoney.moneta.FastMoney;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.mail.internet.InternetAddress;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.transform.stream.StreamSource;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * holder for file. Basically a file locator associated with a customer, under which we will
 * keep a list of booked services, charges, etc
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Getter
@Setter
@UseIdToSelect
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SearchFilter
    @Order(desc = true, priority = 10)
    @ListColumn
    private long id;

    @Section("Info")
    @Embedded
    @Output
    @SearchFilter(field="created")
    @SearchFilter(field="modified")
    private Audit audit;

    @ManyToOne
    @NotNull
    @SearchFilter
    @QLFilter("x.agency = true")
    @ListColumn
    private Partner agency;

    @NotNull
    @SearchFilter(exactMatch = true)
    @ListColumn
    private String agencyReference;

    @NotNull
    @SearchFilter
    @ListColumn
    private String leadName;

    private String email;

    private String telephone;

    private boolean confirmed;

    @SameLine
    @ListColumn(width = 60)
    @ColumnWidth(80)
    @SearchFilter
    private boolean active = true;




    @Ignored
    @SearchFilter
    @ListColumn
    private LocalDate start;

    @Ignored
    @SameLine
    private LocalDate finish;

    @TextArea
    private String comments;


    @KPI
    @ListColumn
    private double totalNetValue;

    @KPI
    @SameLine
    private double totalRetailValue;

    @KPI
    @SameLine
    private double totalCommissionValue;


    @KPI
    @SameLine
    private double balance;

    @Ignored
    @ManyToOne
    private Currency currency;

    @Transient
    @Ignored
    private transient boolean alreadyCancelled = false;



    @Section("Quotation requests")
    @OneToMany(mappedBy = "file")
    @OrderColumn(name = "orderInBooking")
    @Output
    private List<QuotationRequest> quotationRequests = new ArrayList<>();


    @Section("Bookings")
    @OneToMany(mappedBy = "file")
    @OrderColumn(name = "orderInBooking")
    @Output
    private List<Booking> bookings = new ArrayList<>();

    @Section("Services")
    @OneToMany(mappedBy = "file")
    @OrderColumn(name = "orderInBooking")
    @Output
    private List<Service> services = new ArrayList<>();

    @Section("Charges")
    @OneToMany(mappedBy = "file")
    @Output
    private List<BookingCharge> charges = new ArrayList<>();

    @Section("Payments")
    @OneToMany(mappedBy = "file")
    @OrderColumn(name = "id")
    @Output
    private List<BookingPaymentAllocation> payments = new ArrayList<>();

    @Output
    @OneToMany(mappedBy = "file")
    private List<TPVTransaction> TPVTransactions = new ArrayList<>();


    @Section("Invoicing")
    private String companyName;
    private String vatId;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;


    @Override
    public String toString() {
        return "" + getId() + " / " + ((getAgencyReference() != null)?getAgencyReference():"") + " - " + getLeadName() + " (" + ((getAgency() != null)?getAgency().getName():"No agency") + ") " + ((getComments() != null)?getComments():"");
    }


    @Links
    public List<MDDLink> getLinks() {
        List<MDDLink> l = new ArrayList<>();
        l.add(new MDDLink("Services", Service.class, ActionType.OPENLIST, new Data("file.id", getId())));
        if (getAgency() != null) l.add(new MDDLink("Updates", TransferBookingRequest.class, ActionType.OPENLIST, new Data("customer", new Pair(getAgency().getId(), getAgency().getName()), "agencyReference", getAgencyReference())));
        return l;
    }

    public static File getByAgencyRef(EntityManager em, String agencyRef, Partner age)
    {
        try {
            String jpql = "select x from " + File.class.getName() + " x" +
                    " where x.agencyReference='" + agencyRef + "' and x.agency.id= " + age.getId();
            Query q = em.createQuery(jpql).setFlushMode(FlushModeType.COMMIT);
            List<File> l = q.getResultList();
            File b = (l.size() > 0)?l.get(0):null;
            return b;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String... args) throws Throwable {
        Partner a = new Partner();
        a.setId(1);
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {
                System.out.println(getByAgencyRef(em, "1234", a));
            }
        });

    }

    @PostLoad
    public void beforeSet() throws Throwable {
        setAlreadyCancelled(!isActive());
    }

    @PostPersist@PostUpdate
    public void afterSet() throws Exception, Throwable {

        EntityManager em = Helper.getEMFromThreadLocal();
        
        if (!isActive() && (!isActive()) != isAlreadyCancelled()) {
            cancel(em, getAudit().getModifiedBy());
        }
        
        /*
        
        WorkflowEngine.add(new Runnable() {

            long bookingId = getId();

            @Override
            public void run() {

                try {
                    Helper.transact(new JPATransaction() {
                        @Override
                        public void run(EntityManager em) throws Throwable {

                            File b = em.find(File.class, bookingId);

                            if (b.isCancelled() && b.isCancelled() != b.isWasCancelled()) {
                                b.cancel(em, b.getAudit().getModifiedBy());
                            }

                        }
                    });
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

            }
        });
        */
    }




    @Action(order = 6, confirmationMessage = "Are you sure you want to cancel this file?", style = ValoTheme.BUTTON_DANGER, icon = VaadinIcons.CLOSE)
    @NotWhenCreating
    public void cancel(EntityManager em) {
        cancel(em, em.find(User.class, MDD.getUserData().getLogin()));
    }

    public void cancel(EntityManager em, User u) {
        for (Booking s : getBookings()) {
            s.cancel(em, u);
        }
    }

    public Map<String,Object> getData() {
        Map<String, Object> d = new HashMap<>();

        d.put("start", getStart());
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        d.put("serviceDates", "" + ((getStart() != null)?getStart().format(f):"...") + " - " + ((getFinish() != null)?getFinish().format(f):"..."));
        d.put("startddmmyyyy", getStart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        double base = Helper.roundOffEuros(getTotalNetValue() / (1d + 10d / 100d));
        double iva = Helper.roundOffEuros(getTotalNetValue() - base);


        d.put("base", base);
        d.put("iva", iva);

        List<String> points = new ArrayList<>();
        String comentarios = "";
        int pax = 0;
        boolean allServicesAreValued = true;
        boolean allPurchasesAreValued = true;
        double totalCost = 0;

        boolean todoCancelado = true;

        for (Service s : getServices()) {
            if (s instanceof TransferService) {
                TransferService t = (TransferService) s;
                if (pax < t.getPax()) pax = t.getPax();
                if (!points.contains(t.getPickupText())) points.add(t.getPickupText());
                if (!points.contains(t.getDropoffText())) points.add(t.getDropoffText());
                d.put("transferType", "" + t.getTransferType());
            }
            todoCancelado &= s.isCancelled();
            allServicesAreValued &= s.isValued();
            allPurchasesAreValued &= s.isPurchaseValued();
            if (!Strings.isNullOrEmpty(s.getBooking().getSpecialRequests())) comentarios += s.getBooking().getSpecialRequests();
            if (!Strings.isNullOrEmpty(s.getOperationsComment())) comentarios += s.getOperationsComment();
            totalCost += s.getTotalCost();
        }

        d.put("valued", allServicesAreValued);
        d.put("total", getTotalNetValue());
        d.put("purchaseValued", allPurchasesAreValued);
        d.put("totalCost", totalCost);

        d.put("id", getId());
        d.put("locator", getId());
        d.put("leadName", getLeadName());
        d.put("agency", getAgency().getName());
        d.put("agencyReference", getAgencyReference());
        d.put("status", (todoCancelado)?"CANCELLED":"ACTIVE");
        d.put("created", getAudit().getCreated().format(DateTimeFormatter.BASIC_ISO_DATE.ISO_DATE_TIME));
        d.put("office", "");

        d.put("comments", comentarios);
        d.put("direction", String.join(",", points));
        d.put("pax", pax);

        return d;
    }



    @Action(order = 1, icon = VaadinIcons.ENVELOPES)
    @NotWhenCreating
    public void sendVouchers(String changeEmail, String postscript) throws Throwable {


        Helper.transact(em ->{

            long t0 = new Date().getTime();



            String to = changeEmail;
            if (Strings.isNullOrEmpty(to)) {
                to = getEmail();
            }
            if (Strings.isNullOrEmpty(to)) {
                to = getAgency().getEmail();
            }
            if (Strings.isNullOrEmpty(to)) throw new Exception("No valid email address. Please check the agency " + getAgency().getName() + " and fill the email field.");



            Document xml = new Document(new Element("services"));

            for (Service s : services) {
                xml.getRootElement().addContent(s.toXml());
            }

            System.out.println(Helper.toString(xml.getRootElement()));


            String archivo = UUID.randomUUID().toString();

            java.io.File temp = (System.getProperty("tmpdir") == null)? java.io.File.createTempFile(archivo, ".pdf"):new java.io.File(new java.io.File(System.getProperty("tmpdir")), archivo + ".pdf");


            System.out.println("java.io.tmpdir=" + System.getProperty("java.io.tmpdir"));
            System.out.println("Temp file : " + temp.getAbsolutePath());

            FileOutputStream fileOut = new FileOutputStream(temp);
            //String sxslfo = Resources.toString(Resources.getResource(Contract.class, xslfo), Charsets.UTF_8);
            String sxml = new XMLOutputter(Format.getPrettyFormat()).outputString(xml);
            System.out.println("xml=" + sxml);
            fileOut.write(Helper.fop(new StreamSource(new StringReader(AppConfig.get(em).getXslfoForVoucher())), new StreamSource(new StringReader(sxml))));
            fileOut.close();


            AppConfig appconfig = AppConfig.get(em);


            // Create the email message
            HtmlEmail email = new HtmlEmail();
            //Email email = new HtmlEmail();
            email.setHostName(appconfig.getAdminEmailSmtpHost());
            email.setSmtpPort(appconfig.getAdminEmailSmtpPort());
            email.setAuthenticator(new DefaultAuthenticator(appconfig.getAdminEmailUser(), appconfig.getAdminEmailPassword()));
            //email.setSSLOnConnect(true);
            email.setFrom(appconfig.getAdminEmailFrom());
            if (!Strings.isNullOrEmpty(appconfig.getAdminEmailCC())) email.getCcAddresses().add(new InternetAddress(appconfig.getAdminEmailCC()));

            email.setSubject("File " + getId() + " vouchers");


            String msg = postscript;

            String freemark = appconfig.getVouchersEmailTemplate();

            if (!Strings.isNullOrEmpty(freemark)) {
                Map<String, Object> data = Helper.getGeneralData();
                data.put("postscript", postscript);
                data.put("leadname", getLeadName());
                msg = Helper.freemark(freemark, data);
            }

            email.setMsg(msg);

            email.addTo((!Strings.isNullOrEmpty(System.getProperty("allemailsto")))?System.getProperty("allemailsto"):to);

            java.io.File attachment = temp;
            if (attachment != null) email.attach(attachment);

            email.send();


        });


    }

    @Action(order = 2, icon = VaadinIcons.ENVELOPE)
    @NotWhenCreating
    public void sendEmail(@Help("If blank the postscript will be sent as the email body") Template template, String changeEmail, @Help("If blank, the subject from the templaet will be used") String subject, @TextArea String postscript) throws Throwable {

        String to = changeEmail;
        if (Strings.isNullOrEmpty(to)) {
            to = getEmail();
        }
        if (Strings.isNullOrEmpty(to)) {
            to = getAgency().getEmail();
        }
        if (Strings.isNullOrEmpty(to)) throw new Exception("No valid email address. Please check the agency " + getAgency().getName() + " and fill the email field.");

        if (template != null) {
            Map<String, Object> data = Helper.getGeneralData();
            data.put("postscript", postscript);
            EmailHelper.sendEmail(to, !Strings.isNullOrEmpty(subject) ? subject : template.getSubject(), Helper.freemark(template.getFreemarker(), data), false);
        } else {
            EmailHelper.sendEmail(to, subject, postscript, false);
        }

    }

    @Action(order = 3, icon = VaadinIcons.EURO)
    @NotWhenCreating
    public void sendPaymentEmail(EntityManager em, String toEmail, String subject, String postscript, @NotNull TPV tpv, FastMoney amount) throws Throwable {
        AppConfig appconfig = AppConfig.get(em);


        String to = toEmail;
        if (Strings.isNullOrEmpty(to)) {
            to = getEmail();
        }
        if (Strings.isNullOrEmpty(to)) {
            to = getAgency().getEmail();
        }
        if (Strings.isNullOrEmpty(to)) throw new Exception("No valid email address. Please check the agency " + getAgency().getName() + " and fill the email field.");



        // Create the email message
        HtmlEmail email = new HtmlEmail();
        //Email email = new HtmlEmail();
        email.setHostName(appconfig.getAdminEmailSmtpHost());
        email.setSmtpPort(appconfig.getAdminEmailSmtpPort());
        email.setAuthenticator(new DefaultAuthenticator(appconfig.getAdminEmailUser(), appconfig.getAdminEmailPassword()));
        //email.setSSLOnConnect(true);
        email.setFrom(appconfig.getAdminEmailFrom());
        if (!Strings.isNullOrEmpty(appconfig.getAdminEmailCC())) email.getCcAddresses().add(new InternetAddress(appconfig.getAdminEmailCC()));

        email.setSubject("Payment request for booking file " + getId() + "");


        TPVTransaction t = new TPVTransaction();
        t.setValue(amount.getNumber().doubleValueExact());
        t.setCurrency(em.find(Currency.class, amount.getCurrency().getCurrencyCode()));
        t.setFile(this);
        TPVTransactions.add(t);
        t.setLanguage("es");
        t.setSubject((!Strings.isNullOrEmpty(subject))?subject:"");
        t.setTpv(tpv);
        em.merge(t);


        String msg = postscript;

        String freemark = appconfig.getPaymentEmailTemplate();

        if (!Strings.isNullOrEmpty(freemark)) {
            Map<String, Object> data = Helper.getGeneralData();
            data.put("postscript", postscript);
            data.put("leadname", getLeadName());
            data.put("paymentbutton", t.getBoton(em));
            msg = Helper.freemark(freemark, data);
        }

        email.setMsg(msg);

        email.addTo((!Strings.isNullOrEmpty(System.getProperty("allemailsto")))?System.getProperty("allemailsto"):to);


        email.send();

    }


    @Action(order = 4, icon = VaadinIcons.INVOICE)
    @NotWhenCreating
    public FileInvoiceForm invoice() throws Throwable {
        return new FileInvoiceForm(this);
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
                            if (!((Boolean)((Object[])o)[6])) {
                                s = (s != null)?s + " cancelled":"cancelled";
                            }
                            return s;
                        }
                    });
                });
            }
        };
    }

}
