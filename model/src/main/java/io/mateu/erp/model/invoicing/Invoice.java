package io.mateu.erp.model.invoicing;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.vaadin.icons.VaadinIcons;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.booking.parts.*;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.*;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.partners.Agency;
import io.mateu.erp.model.payments.BookingPaymentAllocation;
import io.mateu.erp.model.payments.InvoicePaymentAllocation;
import io.mateu.erp.model.taxes.VAT;
import io.mateu.erp.model.taxes.VATPercent;
import io.mateu.erp.model.taxes.VATSettlement;
import io.mateu.erp.model.world.Resort;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.User;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Getter
@Setter
@NewNotAllowed
public abstract class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Embedded
    @Output
    private Audit audit;

    @NotNull
    @Output
    private InvoiceType type;


    @NotEmpty
    @Output
    @ListColumn
    @MainSearchFilter
    private String number;

    @NotNull
    @Output
    @ListColumn
    @MainSearchFilter
    private LocalDate issueDate;

    @NotNull
    @Output
    private LocalDate taxDate;

    @NotNull
    @Output
    private LocalDate dueDate;

    @ManyToOne
    @NotNull
    @Output
    @ListColumn
    @MainSearchFilter
    private FinancialAgent issuer;

    @ManyToOne
    @NotNull
    @Output
    @ListColumn
    @MainSearchFilter
    private FinancialAgent recipient;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    @UseLinkToListView
    private List<AbstractInvoiceLine> lines = new ArrayList<>();


    @KPI
    @NotWhenCreating
    @NotNull
    @ListColumn
    private double total;


    @KPI
    @NotWhenCreating
    @NotNull
    @ListColumn
    @ManyToOne
    private Currency currency;

    @KPI
    @ListColumn
    private boolean valid = true;

    @KPI
    private double totalPaid;

    @KPI
    @ListColumn
    private boolean paid;

    @KPI
    private double balance;

    @ManyToOne
    @Output
    private VAT vat;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "invoice")
    @UseLinkToListView
    private List<VATLine> VATLines = new ArrayList<>();


    @ManyToOne
    @Output
    private RebateSettlement rebateSettlement;

    @ManyToOne
    @Output
    private VATSettlement vatSettlement;

    @OneToMany(mappedBy = "invoice")
    @OrderColumn(name = "id")
    @UseLinkToListView
    private List<InvoicePaymentAllocation> payments = new ArrayList<>();



    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (id != 0 && obj != null && obj instanceof Invoice && id == ((Invoice) obj).getId());
    }


    public Invoice() {

    }

    public VAT getVatForResort(Resort resort) {
        return resort.getDestination().getVat() != null?resort.getDestination().getVat():resort.getDestination().getCountry().getVat();
    }


    public Element toXml(EntityManager em) throws Exception {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Element xml = new Element("invoice");
        if (getNumber() != null) xml.setAttribute("number", getNumber());
        else xml.setAttribute("number", "PROFORMA");
        if (getIssueDate() != null) xml.setAttribute("issueDate", getIssueDate().format(df));
        if (getDueDate() != null) xml.setAttribute("dueDate", getDueDate().format(df));

        if (this instanceof IssuedInvoice) {
            if (AppConfig.get(em).getLogo() != null) xml.setAttribute("urllogo", "file:" + AppConfig.get(em).getLogo().toFileLocator().getTmpPath());
            if (AppConfig.get(em).getInvoiceWatermark() != null) xml.setAttribute("watermark", "file:" + AppConfig.get(em).getInvoiceWatermark().toFileLocator().getTmpPath());
        }


        if (getIssuer() != null) {

            FinancialAgent a = getIssuer();

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

        if (getRecipient() != null) {

            FinancialAgent a = getRecipient();

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

        for (AbstractInvoiceLine l : lines) {
            Element el;
            els.addContent(el = new Element("line"));

            if (l.getSubject() != null) el.setAttribute("subject", l.getSubject());

            el.setAttribute("discountPercent", pf.format(l.getDiscountPercent()));
            el.setAttribute("quantity", "" + l.getQuantity());
            el.setAttribute("total", nf.format(l.getTotal()));


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

        }

        List<io.mateu.erp.model.booking.File> files = new ArrayList<>();
        List<Booking> bookings = new ArrayList<>();
        for (AbstractInvoiceLine l : lines) {
            if (l instanceof BookingInvoiceLine) {
                BookingInvoiceLine xl = (BookingInvoiceLine) l;
                if (!bookings.contains(xl.getCharge().getBooking())) bookings.add(xl.getCharge().getBooking());
                if (xl.getCharge().getBooking().getFile() != null && !files.contains(xl.getCharge().getBooking().getFile())) files.add(xl.getCharge().getBooking().getFile());
            }
        }

        double pagado = 0;
        for (io.mateu.erp.model.booking.File file : files) {
            pagado += file.getTotalValue() + file.getBalance();
        }
        for (Booking booking : bookings) {
            pagado += booking.getTotalPaid();
        }
        pagado = Helper.roundEuros(pagado);


        xml.setAttribute("paid", nf.format(pagado));
        xml.setAttribute("pending", nf.format(Helper.roundEuros(getTotal() - pagado)));
        xml.setAttribute("total", nf.format(getTotal()));

        xml.addContent(els = new Element("vats"));
        for (VATLine l : VATLines) {
            Element el;
            els.addContent(el = new Element("vat"));

            if (l.isSpecialRegime()) el.setAttribute("specialRegime", "");
            if (l.isExempt()) el.setAttribute("exempt", "");
            el.setAttribute("percent", pf.format(l.getPercent()));
            el.setAttribute("base", nf.format(l.getBase()));
            if (l.getVat() != null) el.setAttribute("vat", l.getVat().getName());
            el.setAttribute("total", nf.format(l.getTotal()));
            if (l.isSpecialRegime()) {
                if (l.getLegalText() != null) el.setAttribute("text", l.getLegalText());
                el.setAttribute("specialRegimeTotal", nf.format(l.getSpecialRegimeTotal()));
            }
        }




        return xml;
    }


    @PrePersist@PreUpdate
    public void pre() {
        double t = 0;
        for (AbstractInvoiceLine l : getLines()) {
            t += l.getTotal();
        }
        setTotal(Helper.roundEuros(t));
        updateBalance();
    }


    @Override
    public String toString() {
        return number;
    }


    public abstract String getXslfo(EntityManager em);


    @Action(order = 2, icon = VaadinIcons.FILE)
    public URL pdf() throws Throwable {
        return createPdf(Sets.newHashSet(this));
    }

    @Action(order = 2, icon = VaadinIcons.FILE)
    public static URL pdf(Set<Invoice> selection) throws Throwable {
        return createPdf(selection);
    }


    public static URL createPdf(Collection<? extends Invoice> invoices) throws Throwable {
        URL[] url = new URL[1];

        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {

                long t0 = new Date().getTime();

                try {

                    Element xml = new Element("invoices");
                    for (Invoice i : invoices) {
                        xml.addContent(i.toXml(em));
                    }

                    try {
                        String archivo = UUID.randomUUID().toString();

                        File temp = (System.getProperty("tmpdir") == null)?File.createTempFile(archivo, ".pdf"):new File(new File(System.getProperty("tmpdir")), archivo + ".pdf");

                        System.out.println("java.io.tmpdir=" + System.getProperty("java.io.tmpdir"));
                        System.out.println("Temp file : " + temp.getAbsolutePath());

                        FileOutputStream fileOut = new FileOutputStream(temp);
                        //String sxslfo = Resources.toString(Resources.getResource(Contract.class, xslfo), Charsets.UTF_8);
                        String sxml = new XMLOutputter(Format.getPrettyFormat()).outputString(xml);
                        System.out.println("xml=" + sxml);

                        /*

                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder = factory.newDocumentBuilder();
                        Document document = builder.parse(new InputSource(new StringReader(sxml)));

                        // Use a Transformer for output
                        TransformerFactory tFactory = TransformerFactory.newInstance();
                        StreamSource stylesource = new StreamSource(new StringReader(invoices.iterator().next().getXslfo(em)));
                        Transformer transformer = tFactory.newTransformer(stylesource);

                        DOMSource source = new DOMSource(document);
                        StreamResult result = new StreamResult(System.out);
                        transformer.transform(source, result);

                        */

                        fileOut.write(Helper.fop(new StreamSource(new StringReader(invoices.iterator().next().getXslfo(em))), new StreamSource(new StringReader(sxml))));
                        fileOut.close();

                        String baseUrl = System.getProperty("tmpurl");
                        if (baseUrl == null) {
                            url[0] = temp.toURI().toURL();
                        } else url[0] = new URL(baseUrl + "/" + temp.getName());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } catch (Exception e1) {
                    e1.printStackTrace();
                }


            }
        });

        return url[0];
    }

    public void updateBalance() {
        double totalPagado = 0;

        for (InvoicePaymentAllocation pa : getPayments()) {
            totalPagado += pa.getValue();
        }

        setTotalPaid(Helper.roundEuros(totalPagado));
        setBalance(Helper.roundEuros(totalPagado - getTotal()));
        setPaid(Math.abs(balance) < 0.1);
    }

    public void setPayments(List<InvoicePaymentAllocation> payments) {
        this.payments = payments;
        updateBalance();
    }
}
