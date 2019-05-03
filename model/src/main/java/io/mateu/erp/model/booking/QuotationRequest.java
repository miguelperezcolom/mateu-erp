package io.mateu.erp.model.booking;

import com.google.common.base.Strings;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Grid;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.themes.ValoTheme;
import io.mateu.erp.model.booking.parts.*;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.financials.FinancialAgent;
import io.mateu.erp.model.invoicing.*;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.partners.Agency;
import io.mateu.erp.model.payments.*;
import io.mateu.erp.model.product.transfer.TransferPointType;
import io.mateu.erp.model.workflow.AbstractTask;
import io.mateu.erp.model.workflow.SendEmailTask;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.interfaces.GridDecorator;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.common.Resource;
import io.mateu.mdd.core.model.config.Template;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.workflow.WorkflowEngine;
import lombok.Getter;
import lombok.Setter;
import org.javamoney.moneta.FastMoney;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.xml.transform.stream.StreamSource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

@Entity
@Getter
@Setter
public class QuotationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Section("Info")
    @Embedded
    @Output
    private Audit audit;

    @Output
    @ManyToOne
    private File file;


    @NotNull
    @ManyToOne
    @ListColumn
    private Agency agency;

    @ManyToOne@NotNull
    private PointOfSale pos;

    @ListColumn@KPI@ColumnWidth(70)
    private boolean active = true;

    @ListColumn@KPI@ColumnWidth(95)
    private boolean confirmed;

    @ListColumn
    private String title;

    @NotNull
    @ListColumn
    @ColumnWidth(90)
    private Currency currency;

    @KPI
    @ListColumn
    @Money
    private double total;

    @KPI
    @ListColumn
    @Money
    private double totalCost;

    @KPI
    @ListColumn
    @Money
    private double totalMarkup;

    @KPI
    @ListColumn
    @Money
    private double balance;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rq")
    @NotInlineEditable
    private List<QuotationRequestHotel> hotels = new ArrayList<>();

    public String getHotelsHtml() {
        String h = "<div class='lines'>";
        for (QuotationRequestHotel l : hotels) {
            h += "<div class='line" + (l.isActive() ? "" : " cancelled") + "'>";
            h += l.toHtml();
            h += "</div>";
        }
        h += "</div>";

        return h;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rq")
    @NotInlineEditable
    private List<QuotationRequestTransfer> transfers = new ArrayList<>();

    public String getTransfersHtml() {
        String h = "<div class='lines'>";
        for (QuotationRequestTransfer l : transfers) {
            h += "<div class='line" + (l.isActive() ? "" : " cancelled") + "'>";
            h += l.toHtml();
            h += "</div>";
        }
        h += "</div>";

        return h;
    }


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rq")
    @NotInlineEditable
    private List<QuotationRequestExcursion> excursions = new ArrayList<>();

    public String getExcursionsHtml() {
        String h = "<div class='lines'>";
        for (QuotationRequestHotel l : hotels) {
            h += "<div class='line" + (l.isActive() ? "" : " cancelled") + "'>";
            h += l.toHtml();
            h += "</div>";
        }
        h += "</div>";

        return h;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rq")
    @NotInlineEditable
    private List<QuotationRequestGeneric> generics = new ArrayList<>();

    public String getGenericsHtml() {
        String h = "<div class='lines'>";
        for (QuotationRequestHotel l : hotels) {
            h += "<div class='line" + (l.isActive() ? "" : " cancelled") + "'>";
            h += l.toHtml();
            h += "</div>";
        }
        h += "</div>";

        return h;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rq")
    @NotInlineEditable
    private List<QuotationRequestLine> lines = new ArrayList<>();

    public String getLinesHtml() {
        String h = "<div class='lines'>";
        for (QuotationRequestLine l : lines) {
            h += "<div class='line" + (l.isActive() ? "" : " cancelled") + "'>";
            h += l.toHtml();
            h += "</div>";
        }
        h += "</div>";

        return h;
    }


    @Section("Operation")
    @ListColumn
    private LocalDate date;

    @ListColumn
    private LocalDate optionDate;

    @TextArea
    private String text;

    @TextArea
    private String privateComments;

    @UseLinkToListView(addEnabled = false, deleteEnabled = false)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quotationRequest")
    private List<QuotationRequestComment> comments = new ArrayList<>();

    @NotNull@Ignored
    private QuotationRequestAnswer answer = QuotationRequestAnswer.PENDING;

    @UseLinkToListView(addEnabled = false, deleteEnabled = false)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quotationRequest")
    private List<QuotationRequestPaymentAllocation> payments = new ArrayList<>();

    @UseLinkToListView
    @OneToMany(cascade = CascadeType.ALL)
    private List<AbstractTask> tasks = new ArrayList<>();

    @Ignored
    private LocalDateTime readTime;

    @Ignored
    private String reader;

    @Ignored
    private LocalDateTime answerTime;

    @Ignored
    private String answerText;

    @Ignored
    private FastMoney answerPrice;

    @Ignored
    private transient boolean alreadyConfirmed;

    @Section("Contact")

    private String name;

    private String email;

    private String telephone;

    @Ignored
    private boolean forcePre = false;



    public void updateTotal() {
        double t = 0;
        double c = 0;
        for (QuotationRequestLine line : lines) if (line.isActive()) {
            t += line.getTotalSale();
            c += line.getTotalCost();
        }
        for (QuotationRequestHotel line : hotels) if (line.isActive()) {
            t += line.getTotalSale();
            c += line.getTotalCost();
        }
        for (QuotationRequestTransfer line : transfers) if (line.isActive()) {
            t += line.getTotalSale();
            c += line.getTotalCost();
        }
        for (QuotationRequestExcursion line : excursions) if (line.isActive()) {
            t += line.getTotalSale();
            c += line.getTotalCost();
        }
        for (QuotationRequestGeneric line : generics) if (line.isActive()) {
            t += line.getTotalSale();
            c += line.getTotalCost();
        }
        setTotal(Helper.roundEuros(t));
        setTotalCost(Helper.roundEuros(c));
        setTotalMarkup(Helper.roundEuros(t - c));
        double p = 0;
        for (QuotationRequestPaymentAllocation a : payments) {
            p += a.getValue();
        }
        setBalance(Helper.roundEuros(p - t));
    }

    @Action(saveAfter = true, order = 1, confirmationMessage = "Are you sure you want to cancel this quotation?", style = ValoTheme.BUTTON_DANGER, icon = VaadinIcons.CLOSE)
    public void cancel() {
        setActive(false);
    }

    public boolean isCancelVisible() {
        return isActive() && !isConfirmed();
    }

    @Action(saveAfter = true, order = 2, confirmationMessage = "Are you sure you want to confirm this quotation?", style = ValoTheme.BUTTON_FRIENDLY, icon = VaadinIcons.CHECK)
    public void confirm() {
        setConfirmed(true);
    }

    public boolean isConfirmVisible() {
        return isActive() && !isConfirmed();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj != null && obj instanceof QuotationRequest && id == ((QuotationRequest) obj).getId());
    }

    @Override
    public String toString() {
        return !Strings.isNullOrEmpty(title)?title:"Quotation request " + id;
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

                            if (o instanceof QuotationRequest) {
                                if (!((QuotationRequest)o).isActive()) s = (s != null)?s + " cancelled":"cancelled";
                            } else {
                                if (!((Boolean)((Object[])o)[3])) {
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







    @Action(order = 3, icon = VaadinIcons.EDIT, saveAfter = true)
    @NotWhenCreating
    public void addComment(@NotEmpty String text) {
        if (!Strings.isNullOrEmpty(text)) {
            QuotationRequestComment c = new QuotationRequestComment();
            c.setQuotationRequest(this);
            c.setComment(text);
            getComments().add(c);
        }
    }



    @Action(order = 3, icon = VaadinIcons.ENVELOPE, saveAfter = true)
    @NotWhenCreating
    public void sendEmail(@Help("If blank the postscript will be sent as the email body") Template template, String changeEmail, @Help("If blank, the subject from the templaet will be used") String subject, @TextArea String postscript, boolean excludeProforma) throws Throwable {


        Helper.transact(em ->{

            long t0 = new Date().getTime();

            AppConfig appconfig = AppConfig.get(em);

            String to = changeEmail;
            if (Strings.isNullOrEmpty(to)) {
                to = getEmail();
            }
            if (Strings.isNullOrEmpty(to)) {
                to = getAgency().getEmail();
            }
            if (Strings.isNullOrEmpty(to)) throw new Exception("No valid email address. Please check the agency " + getAgency().getName() + " and fill the email field.");


            SendEmailTask t;
            tasks.add(t = new SendEmailTask());

            t.setTo(to);
            t.setAudit(new Audit(MDD.getCurrentUser()));

            String msg = postscript;

            String freemark = appconfig.getVouchersEmailTemplate();

            if (template != null) {
                Map<String, Object> data = io.mateu.mdd.core.util.Helper.getGeneralData();
                data.put("postscript", postscript);
                t.setDescription("Send email from template " + template.getName());
                t.setSubject(!Strings.isNullOrEmpty(subject) ? subject : template.getSubject());
                msg = io.mateu.mdd.core.util.Helper.freemark(template.getFreemarker(), data);
            } else {
                t.setDescription("Send email from void template");
                t.setSubject(subject);
                msg = postscript;
            }

            if (!excludeProforma) {

                t.getAttachments().add(new Resource(createProforma(em)));
            }

            t.setMessage(msg);

        });

    }
    @Action(order = 0, icon = VaadinIcons.MAP_MARKER)
    public BookingMap map() {
        return new BookingMap(this);
    }


    @Action(icon = VaadinIcons.FILE, order = 50)
    public URL proforma(EntityManager em) throws Exception {
        return new URL(new Resource(createProforma(em)).toFileLocator().getUrl());
    }

    @Action(order = 5, icon = VaadinIcons.EURO, saveAfter = true)
    @NotWhenCreating
    public void enterPayment(EntityManager em, @NotNull Account account, @NotNull MethodOfPayment methodOfPayment, @NotNull Currency currency, double amount) throws Throwable {
        if (getAgency().getFinancialAgent() == null) throw  new Exception("Missing financial agent for agency " + getAgency().getName() + ". Please fill");
        if (amount != 0) {
            Payment p = new Payment();
            p.setAccount(account);
            p.setDate(LocalDate.now());
            p.setAgent(getAgency().getFinancialAgent());

            PaymentLine l;
            p.getLines().add(l = new PaymentLine());
            l.setPayment(p);
            l.setMethodOfPayment(methodOfPayment);
            l.setCurrency(currency);
            l.setValue(amount);


            QuotationRequestPaymentAllocation a;
            p.getBreakdown().add(a = new QuotationRequestPaymentAllocation());
            a.setPayment(p);
            a.setQuotationRequest(this);
            getPayments().add(a);
            a.setValue(amount);

            em.persist(p);

            setPayments(new ArrayList<>(getPayments()));

        }
    }


    private java.io.File createProforma(EntityManager em) throws IOException {
        String archivo = UUID.randomUUID().toString();
        java.io.File temp = (System.getProperty("tmpdir") == null)? java.io.File.createTempFile(archivo, ".pdf"):new java.io.File(new java.io.File(System.getProperty("tmpdir")), archivo + ".pdf");

        createProforma(em, temp);

        return temp;
    }

    public void createProforma(EntityManager em, java.io.File f) {

        long t0 = new Date().getTime();

        try {

            Element xml = new Element("quotationRequests");
            xml.addContent(toXmlForProforma(em));

            try {
                String archivo = UUID.randomUUID().toString();

                System.out.println("java.io.tmpdir=" + System.getProperty("java.io.tmpdir"));
                System.out.println("File : " + f.getAbsolutePath());

                FileOutputStream fileOut = new FileOutputStream(f);
                //String sxslfo = Resources.toString(Resources.getResource(Contract.class, xslfo), Charsets.UTF_8);
                String sxml = new XMLOutputter(Format.getPrettyFormat()).outputString(xml);
                System.out.println("xml=" + sxml);
                fileOut.write(Helper.fop(new StreamSource(new StringReader(AppConfig.get(em).getXslfoForQuotationRequest())), new StreamSource(new StringReader(sxml))));
                fileOut.close();

            } catch (IOException e) {
                e.printStackTrace();
            }


        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    private Element toXmlForProforma(EntityManager em) throws Exception {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DecimalFormat pf = new DecimalFormat("#####0.00");
        DecimalFormat nf = new DecimalFormat("##,###,###,###,##0.00");



        Element xml = new Element("quotationRequest");

        if (title != null) xml.setAttribute("title", title);
        if (date != null) xml.setAttribute("date", date.format(DateTimeFormatter.ISO_DATE));
        else if (audit != null && audit.getModified() != null) xml.setAttribute("date", audit.getModified().format(DateTimeFormatter.ISO_DATE));
        if (optionDate != null) xml.setAttribute("optionDate", optionDate.format(DateTimeFormatter.ISO_DATE));
        if (text != null) xml.setAttribute("text", text);

        xml.setAttribute("total", nf.format(total));
        xml.setAttribute("paid", nf.format(Helper.roundEuros(total + balance)));
        xml.setAttribute("pending", nf.format(Helper.roundEuros(-1d * balance)));

        if (AppConfig.get(em).getLogo() != null) xml.setAttribute("urllogo", "file:" + AppConfig.get(em).getLogo().toFileLocator().getTmpPath());


        if (getAgency().getCompany().getFinancialAgent() != null) {

            FinancialAgent a = getAgency().getCompany().getFinancialAgent();

            Element d;
            xml.addContent(d = new Element("issuer"));

            if (a.getName() != null) d.setAttribute("name", a.getName());
            if (a.getBusinessName() != null) d.setAttribute("businessName", a.getBusinessName());
            if (a.getVatIdentificationNumber() != null) d.setAttribute("vatid", a.getVatIdentificationNumber());
            if (a.getAddress() != null) d.setAttribute("address", a.getAddress());
            if (a.getCity() != null) d.setAttribute("resort", a.getCity());
            if (a.getPostalCode() != null) d.setAttribute("zip", a.getPostalCode());
            if (a.getTelephone() != null) d.setAttribute("telephone", a.getTelephone());
            if (a.getFax() != null) d.setAttribute("fax", a.getFax());
            if (a.getEmail() != null) d.setAttribute("email", a.getEmail());
            if (a.getCountry() != null) d.setAttribute("country", a.getCountry());
            if (a.getState() != null) d.setAttribute("state", a.getState());

        }

        if (getAgency().getFinancialAgent() != null) {

            FinancialAgent a = getAgency().getFinancialAgent();

            Element d;
            xml.addContent(d = new Element("recipient"));

            if (a == null || a.isDirectSale()) {
                if (name != null) d.setAttribute("name", name);
                if (telephone != null) d.setAttribute("telephone", telephone);
                if (email != null) d.setAttribute("email", email);
            } else {
                if (a.getName() != null) d.setAttribute("name", a.getName());
                if (a.getBusinessName() != null) d.setAttribute("businessName", a.getBusinessName());
                if (a.getVatIdentificationNumber() != null) d.setAttribute("vatid", a.getVatIdentificationNumber());
                if (a.getAddress() != null) d.setAttribute("address", a.getAddress());
                if (a.getCity() != null) d.setAttribute("resort", a.getCity());
                if (a.getPostalCode() != null) d.setAttribute("zip", a.getPostalCode());
                if (a.getTelephone() != null) d.setAttribute("telephone", a.getTelephone());
                if (a.getFax() != null) d.setAttribute("fax", a.getFax());
                if (a.getEmail() != null) d.setAttribute("email", a.getEmail());
                if (a.getCountry() != null) d.setAttribute("country", a.getCountry());
                if (a.getState() != null) d.setAttribute("state", a.getState());
            }

        }


        Element els;
        xml.addContent(els = new Element("lines"));

        for (QuotationRequestLine l : lines) {
            els.addContent(l.toXml());
        }

        xml.addContent(els = new Element("hotels"));

        for (QuotationRequestHotel l : hotels) {
            els.addContent(l.toXml());
        }

        xml.addContent(els = new Element("transfers"));

        for (QuotationRequestTransfer l : transfers) {
            els.addContent(l.toXml());
        }

        xml.addContent(els = new Element("excursions"));

        for (QuotationRequestExcursion l : excursions) {
            els.addContent(l.toXml());
        }

        xml.addContent(els = new Element("generics"));

        for (QuotationRequestGeneric l : generics) {
            els.addContent(l.toXml());
        }

        return xml;
    }

    private Element toXmlForInvoice(EntityManager em) throws Exception {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Element xml = new Element("invoice");
        /*
        if (getNumber() != null) xml.setAttribute("number", getNumber());
        if (getIssueDate() != null) xml.setAttribute("issueDate", getIssueDate().format(df));
        if (getDueDate() != null) xml.setAttribute("dueDate", getDueDate().format(df));
        */

        if (AppConfig.get(em).getLogo() != null) xml.setAttribute("urllogo", "file:" + AppConfig.get(em).getLogo().toFileLocator().getTmpPath());
        if (AppConfig.get(em).getInvoiceWatermark() != null) xml.setAttribute("watermark", "file:" + AppConfig.get(em).getInvoiceWatermark().toFileLocator().getTmpPath());


        if (getAgency().getCompany().getFinancialAgent() != null) {

            FinancialAgent a = getAgency().getCompany().getFinancialAgent();

            Element d;
            xml.addContent(d = new Element("issuer"));

            if (a.getName() != null) d.setAttribute("name", a.getName());
            if (a.getBusinessName() != null) d.setAttribute("businessName", a.getBusinessName());
            if (a.getVatIdentificationNumber() != null) d.setAttribute("vatid", a.getVatIdentificationNumber());
            if (a.getAddress() != null) d.setAttribute("address", a.getAddress());
            if (a.getCity() != null) d.setAttribute("resort", a.getCity());
            if (a.getPostalCode() != null) d.setAttribute("zip", a.getPostalCode());
            if (a.getTelephone() != null) d.setAttribute("telephone", a.getTelephone());
            if (a.getFax() != null) d.setAttribute("fax", a.getFax());
            if (a.getEmail() != null) d.setAttribute("email", a.getEmail());
            if (a.getCountry() != null) d.setAttribute("country", a.getCountry());
            if (a.getState() != null) d.setAttribute("state", a.getState());

        }

        if (getAgency().getFinancialAgent() != null) {

            FinancialAgent a = getAgency().getFinancialAgent();

            Element d;
            xml.addContent(d = new Element("recipient"));

            if (a.getName() != null) d.setAttribute("name", a.getName());
            if (a.getBusinessName() != null) d.setAttribute("businessName", a.getBusinessName());
            if (a.getVatIdentificationNumber() != null) d.setAttribute("vatid", a.getVatIdentificationNumber());
            if (a.getAddress() != null) d.setAttribute("address", a.getAddress());
            if (a.getCity() != null) d.setAttribute("resort", a.getCity());
            if (a.getPostalCode() != null) d.setAttribute("zip", a.getPostalCode());
            if (a.getTelephone() != null) d.setAttribute("telephone", a.getTelephone());
            if (a.getFax() != null) d.setAttribute("fax", a.getFax());
            if (a.getEmail() != null) d.setAttribute("email", a.getEmail());
            if (a.getCountry() != null) d.setAttribute("country", a.getCountry());
            if (a.getState() != null) d.setAttribute("state", a.getState());

        }


        DecimalFormat pf = new DecimalFormat("#####0.00");
        DecimalFormat nf = new DecimalFormat("##,###,###,###,##0.00");

        Element els;
        xml.addContent(els = new Element("lines"));

        for (QuotationRequestLine l : lines) {
            Element el;
            els.addContent(el = new Element("line"));

            if (l.getText() != null) el.setAttribute("subject", l.getText());

            //el.setAttribute("discountPercent", pf.format(l.getDiscountPercent()));
            el.setAttribute("quantity", "" + l.getUnits());
            el.setAttribute("total", nf.format(l.getTotalSale()));

            /*
            if (l instanceof BookingInvoiceLine) {
                BookingInvoiceLine xl = (BookingInvoiceLine) l;

                Booking b = xl.getCharge().getBooking();
                el.setAttribute("id", "" + b.getId());
                if (b.getLeadName() != null) el.setAttribute("leadName", b.getLeadName());
                if (b.getAgencyReference() != null) el.setAttribute("reference", b.getAgencyReference());
                if (b.getStart() != null) el.setAttribute("start", df.format(b.getStart()));
                if (b.getEnd() != null) el.setAttribute("end", df.format(b.getEnd()));
                if (b instanceof HotelBooking) {
                    HotelBooking hb = (HotelBooking) b;
                    if (hb.getHotel() != null && hb.getHotel().getName() != null)
                        el.setAttribute("service", hb.getHotel().getName());
                } else if (b instanceof FreeTextBooking) {
                    FreeTextBooking hb = (FreeTextBooking) b;
                    if (hb.getServiceDescription() != null) el.setAttribute("service", hb.getServiceDescription());
                }

            }
            */

            xml.setAttribute("paid", nf.format(0));
            xml.setAttribute("pending", nf.format(getTotal()));

        }

        xml.addContent(els = new Element("vats"));
        /*
        for (VATLine l : VATLines) {
            Element el;
            els.addContent(el = new Element("vat"));

            if (l.isSpecialRegime()) el.setAttribute("specialRegime", "");
            if (l.isExempt()) el.setAttribute("exempt", "");
            el.setAttribute("percent", pf.format(l.getPercent()));
            el.setAttribute("base", nf.format(l.getBase()));
            el.setAttribute("vat", nf.format(l.getVat()));
            el.setAttribute("total", nf.format(l.getTotal()));

        }
        */




        return xml;
    }

    @PostLoad
    public void postLoad() {
        alreadyConfirmed = file != null;
    }

    @PrePersist@PreUpdate
    public void pre() {
        if (alreadyConfirmed) throw new Error("This quotation request has already been related to a File. It can not be modified");
    }

    @PostUpdate@PostPersist
    public void post() {
            WorkflowEngine.add(() -> {

                try {
                    Helper.transact(em -> {

                        QuotationRequest r = em.find(QuotationRequest.class, getId());

                        r.updateTotal();


                        if (r.isConfirmed()) {
                            r.build(em);
                        }

                    });
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

            });
    }

    private void build(EntityManager em) {

        DecimalFormat nf = new DecimalFormat("##,###,###,###,##0.00");

        if (getFile() == null) {

            AppConfig c = AppConfig.get(em);

            File f = new File();
            f.setAudit(new Audit(MDD.getCurrentUser()));
            f.setAgency(getAgency());
            f.setLeadName(getTitle());
            f.setCurrency(getCurrency());

            for (QuotationRequestHotel qrl : getHotels()) {
                HotelBooking b;
                f.getBookings().add(b = new HotelBooking());
                b.setFile(f);
                b.setAgency(getAgency());
                b.setCurrency(getCurrency());
                b.setLeadName(getTitle());
                b.setConfirmed(true);
                b.setAudit(new Audit(MDD.getCurrentUser()));
                b.setCostOverrided(true);
                b.setOverridedBillingConcept(c.getBillingConceptForHotel());
                b.setValueOverrided(true);
                b.setPos(getPos());
                b.setAgencyReference("");
                b.setAvailable(true);
                b.setAdults(0);
                b.setChildren(0);
                b.setEmail(getEmail());
                b.setOverridedValue(qrl.getTotalSale());
                b.setOverridedCostCurrency(getCurrency());
                b.setOverridedCost(qrl.getTotalCost());
                b.setTelephone(getTelephone());

                b.setHotel(qrl.getHotel());

                String s = "";

                int pos = 1;
                for (QuotationRequestHotelLine hl : qrl.getLines()) {
                    int n = hl.getStart() != null && hl.getEnd() != null?(int) (DAYS.between(hl.getStart(), hl.getEnd()) -1):0;

                    HotelBookingLine hbl;
                    b.getLines().add(hbl = new HotelBookingLine());
                    hbl.setBooking(b);
                    hbl.setRoom(hl.getRoom());
                    hbl.setBoard(hl.getBoard());
                    hbl.setStart(hl.getStart());
                    hbl.setEnd(hl.getEnd());
                    hbl.setRooms(hl.getNumberOfRooms());
                    hbl.setActive(hl.isActive());
                    hbl.setAdultsPerRoom(hl.getAdultsPerRoom());
                    hbl.setChildrenPerRoom(hl.getChildrenPerRoom());
                    hbl.setAges(hl.getAges());

                    if (!"".equals(s)) s += "\n";
                    s += "Line " + pos++ + ": ";
                    if (hl.isCostOverrided()) {
                        String x = "";
                        if (hl.getCostPerRoom() != 0) {
                            if (!"".equals(x)) x += ", ";
                            x += hl.getCostPerRoom() + " per room";
                        }
                        if (hl.getCostPerAdult() != 0) {
                            if (!"".equals(x)) x += ", ";
                            x += hl.getCostPerAdult() + " per adult";
                        }
                        if (hl.getCostPerChild() != 0) {
                            if (!"".equals(x)) x += ", ";
                            x += hl.getCostPerChild() + " per child";
                        }
                        x += ". Nights: " + n + ". Total: " + nf.format(hl.getTotalCost()) + "";
                        s += x;
                    } else {
                        s += "Prices as per contract";
                    }

                }

                if (qrl.getAdultTaxPerNight() != 0) {
                    if (!"".equals(s)) s += "\n";
                    s += nf.format(qrl.getAdultTaxPerNight()) + " per adult tax";
                }
                if (qrl.getChildTaxPerNight() != 0) {
                    if (!"".equals(s)) s += "\n";
                    s += nf.format(qrl.getChildTaxPerNight()) + " per child tax";
                }
                if (qrl.getTotalTax() != 0) {
                    if (!"".equals(s)) s += "\n";
                    s += "Total TAX: " + nf.format(qrl.getAdultTaxPerNight());
                    if (!"".equals(s)) s += "\n";
                    s += "TOTAL COST BEFORE TAX: " + nf.format(Helper.roundEuros(qrl.getTotalCost() - qrl.getTotalTax()));
                }

                if (!"".equals(s)) s += "\n";
                s += "TOTAL COST: " + nf.format(qrl.getTotalCost());

                b.setCommentsForProvider(s);

                b.setSpecialRequests(qrl.getSpecialRequests());

                em.persist(b);
            }
            for (QuotationRequestTransfer qrl : getTransfers()) {
                TransferBooking b;
                f.getBookings().add(b = new TransferBooking());
                b.setFile(f);
                b.setAgency(getAgency());
                b.setCurrency(getCurrency());
                b.setLeadName(getTitle());
                b.setConfirmed(true);
                b.setAudit(new Audit(MDD.getCurrentUser()));
                b.setCostOverrided(true);
                b.setOverridedBillingConcept(c.getBillingConceptForHotel());
                b.setValueOverrided(true);
                b.setPos(getPos());
                b.setAgencyReference("");
                b.setAvailable(true);
                b.setAdults(0);
                b.setChildren(0);
                b.setEmail(getEmail());
                b.setOverridedValue(qrl.getTotalSale());
                b.setOverridedCostCurrency(getCurrency());
                b.setOverridedCost(qrl.getTotalCost());
                b.setTelephone(getTelephone());

                b.setTransferType(qrl.getTransferType());
                b.setOrigin(qrl.getOrigin());
                b.setDestination(qrl.getDestination());
                if (TransferPointType.AIRPORT.equals(qrl.getDestination().getType())) {
                    b.setArrivalFlightTime(qrl.getFlightDate());
                    b.setArrivalFlightNumber(qrl.getFlightNumber());
                    b.setArrivalFlightOrigin(qrl.getFlightOriginOrDestination());
                } else {
                    b.setDepartureFlightTime(qrl.getFlightDate());
                    b.setDepartureFlightNumber(qrl.getFlightNumber());
                    b.setDepartureFlightDestination(qrl.getFlightOriginOrDestination());
                }
                b.setAdults(qrl.getPax());
                em.persist(b);
            }
            for (QuotationRequestExcursion qrl : getExcursions()) {
                ExcursionBooking b;
                f.getBookings().add(b = new ExcursionBooking());
                b.setFile(f);
                b.setAgency(getAgency());
                b.setCurrency(getCurrency());
                b.setLeadName(getTitle());
                b.setConfirmed(true);
                b.setAudit(new Audit(MDD.getCurrentUser()));
                b.setCostOverrided(true);
                b.setOverridedBillingConcept(c.getBillingConceptForHotel());
                b.setValueOverrided(true);
                b.setPos(getPos());
                b.setAgencyReference("");
                b.setAvailable(true);
                b.setAdults(0);
                b.setChildren(0);
                b.setEmail(getEmail());
                b.setOverridedValue(qrl.getTotalSale());
                b.setOverridedCostCurrency(getCurrency());
                b.setOverridedCost(qrl.getTotalCost());
                b.setTelephone(getTelephone());

                b.setExcursion(qrl.getExcursion());
                b.setVariant(qrl.getVariant());
                b.setShift(qrl.getShift());
                b.setAdults(qrl.getAdults());
                b.setChildren(qrl.getChildren());
                b.setStart(qrl.getDate());
                b.setEnd(qrl.getDate());
                em.persist(b);
            }
            for (QuotationRequestGeneric qrl : getGenerics()) {
                GenericBooking b;
                f.getBookings().add(b = new GenericBooking());
                b.setFile(f);
                b.setAgency(getAgency());
                b.setCurrency(getCurrency());
                b.setLeadName(getTitle());
                b.setConfirmed(true);
                b.setAudit(new Audit(MDD.getCurrentUser()));
                b.setCostOverrided(true);
                b.setOverridedBillingConcept(c.getBillingConceptForHotel());
                b.setValueOverrided(true);
                b.setPos(getPos());
                b.setAgencyReference("");
                b.setAvailable(true);
                b.setAdults(0);
                b.setChildren(0);
                b.setEmail(getEmail());
                b.setOverridedValue(qrl.getTotalSale());
                b.setOverridedCostCurrency(getCurrency());
                b.setOverridedCost(qrl.getTotalCost());
                b.setTelephone(getTelephone());

                b.setProduct(qrl.getProduct());
                b.setVariant(qrl.getVariant());
                b.setUnits(qrl.getUnits());
                b.setAdults(qrl.getAdults());
                b.setChildren(qrl.getChildren());
                b.setStart(qrl.getStart());
                b.setEnd(qrl.getEnd());
                b.setOffice(b.getProduct().getOffice());
                em.persist(b);
            }
            for (QuotationRequestLine qrl : getLines()) {
                FreeTextBooking b;
                f.getBookings().add(b = new FreeTextBooking());
                b.setFile(f);
                b.setAgency(getAgency());
                b.setCurrency(getCurrency());
                b.setLeadName(getTitle());
                b.setConfirmed(true);
                b.setAudit(new Audit(MDD.getCurrentUser()));
                b.setCostOverrided(true);
                b.setOverridedBillingConcept(qrl.getBillingConcept());
                b.setStart(qrl.getStart());
                b.setEnd(qrl.getEnd());
                b.setOffice(qrl.getOffice());
                b.setProductLine(qrl.getProductLine());
                b.setServiceDescription(qrl.getText());
                b.setValueOverrided(true);
                b.setPos(getPos());
                b.setAgencyReference("");
                b.setAvailable(true);
                b.setAdults(0);
                b.setChildren(0);
                b.setEmail(getEmail());
                b.setOverridedValue(qrl.getTotalSale());
                b.setOverridedCostCurrency(getCurrency());
                b.setOverridedCost(qrl.getTotalCost());
                b.setProvider(qrl.getProvider());
                b.setTelephone(getTelephone());
                em.persist(b);
            }

            f.setQuotationRequest(this);
            setFile(f);

            em.persist(f);

        }

    }

}
