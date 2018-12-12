package io.mateu.erp.model.invoicing;

import com.vaadin.icons.VaadinIcons;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.freetext.FreeTextService;
import io.mateu.erp.model.booking.parts.FreeTextBooking;
import io.mateu.erp.model.booking.parts.HotelBooking;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.Amount;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.erp.model.financials.FinancialAgent;
import io.mateu.erp.model.financials.RebateSettlement;
import io.mateu.erp.model.taxes.VATSettlement;
import io.mateu.mdd.core.model.authentication.User;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
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
    private String number;

    @NotNull
    @Output
    @ListColumn
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
    private FinancialAgent issuer;

    @ManyToOne
    @NotNull
    @Output
    @ListColumn
    private FinancialAgent recipient;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    @Output
    private List<AbstractInvoiceLine> lines = new ArrayList<>();


    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="value", column=@Column(name="total_value"))
            , @AttributeOverride(name="date", column=@Column(name="total_date"))
            , @AttributeOverride(name="officeChangeRate", column=@Column(name="total_offchangerate"))
            , @AttributeOverride(name="officeValue", column=@Column(name="total_offvalue"))
            , @AttributeOverride(name="nucChangeRate", column=@Column(name="total_nuchangerate"))
            , @AttributeOverride(name="nucValue", column=@Column(name="total_nucvalue"))
    })
    @AssociationOverrides({
            @AssociationOverride(name="currency", joinColumns = @JoinColumn(name = "total_currency"))
    })
    @KPI
    @NotWhenCreating
    @NotNull
    @ListColumn
    private Amount total;

    @KPI
    @ListColumn
    private boolean valid = true;

    @KPI
    @ListColumn
    private boolean paid;


    @Output
    private double retainedPercent;

    @Output
    private double retainedTotal;


    @ManyToOne
    @Output
    private RebateSettlement rebateSettlement;

    @ManyToOne
    @Output
    private VATSettlement vatSettlement;


    public Invoice() {

    }

    public Invoice(User u, Collection<? extends Charge> charges, boolean proforma, FinancialAgent issuer, FinancialAgent recipient, String invoiceNumber) throws Throwable {

        if (charges == null || charges.size() == 0) throw new Exception("Can not create invoices from an empty list of charges");

        setRecipient(recipient);
        setIssuer(issuer);
        setNumber(invoiceNumber);

        boolean inicializar = true;


        double total = 0;

        for (Charge c : charges) {

            if (inicializar) {

                setType((ChargeType.SALE.equals(c.getType()))?InvoiceType.ISSUED:InvoiceType.RECEIVED);

                setAudit(new Audit(u));

                setTotal(new Amount(FastMoney.of(0, c.getTotal().getCurrency().getIsoCode())));

                setIssueDate(LocalDate.now());


                if (this instanceof IssuedInvoice) {
                    if (c.getPartner().getFinancialAgent() == null) throw new Exception("If you want to create proformas or invoices you must set the financial agent for the agency " + c.getPartner().getName());
                    if (c.getPartner().getCompany() == null) throw new Exception("If you want to create proformas or invoices you must set the company for the agency " + c.getPartner().getName());
                    if (c.getPartner().getCompany().getFinancialAgent() == null) throw new Exception("If you want to create proformas or invoices you must set the financial agent for the company " + c.getPartner().getCompany().getName());

                    setRecipient((ChargeType.SALE.equals(c.getType()))?c.getPartner().getFinancialAgent():c.getPartner().getCompany().getFinancialAgent());
                    setIssuer((ChargeType.PURCHASE.equals(c.getType()))?c.getPartner().getFinancialAgent():c.getPartner().getCompany().getFinancialAgent());

                    if (!proforma) {
                        if (InvoiceType.ISSUED.equals(type)) {
                            ((IssuedInvoice)this).setSerial(c.getPartner().getCompany().getBillingSerial());
                            setNumber(((IssuedInvoice)this).getSerial().getPrefix() + "" + ((IssuedInvoice)this).getSerial().getNextNumber());
                        } else {
                            setNumber("PROFORMA");
                        }
                    }
                }

                setIssueDate(LocalDate.now());
                setTaxDate(LocalDate.now());


                inicializar = false;
            }


            if (c instanceof BookingCharge) getLines().add(new BookingInvoiceLine(this, (BookingCharge) c));
            else if (c instanceof PurchaseCharge) getLines().add(new PurchaseOrderInvoiceLine(this, (PurchaseCharge) c));
            if (!proforma) {
                c.setInvoice(this);
            }

            total += c.getTotal().getValue();
        }


        setTotal(new Amount(FastMoney.of(total, getTotal().getCurrency().getIsoCode())));

        setRetainedPercent(0);
    }


    public Element toXml(EntityManager em) throws Exception {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Element xml = new Element("invoice");
        if (getNumber() != null) xml.setAttribute("number", getNumber());
        if (getIssueDate() != null) xml.setAttribute("issueDate", getIssueDate().format(df));
        if (getDueDate() != null) xml.setAttribute("dueDate", getDueDate().format(df));

        if (this instanceof IssuedInvoice) {
            if (AppConfig.get(em).getLogo() != null) xml.setAttribute("urllogo", AppConfig.get(em).getLogo().toFileLocator().getTmpPath());
            if (AppConfig.get(em).getInvoiceWatermark() != null) xml.setAttribute("watermark", AppConfig.get(em).getInvoiceWatermark().toFileLocator().getTmpPath());
        }


        if (getIssuer() != null) {

            FinancialAgent a = getIssuer();

            Element d;
            xml.addContent(d = new Element("issuer"));

            if (a.getName() != null) d.setAttribute("name", a.getName());
            if (a.getBusinessName() != null) d.setAttribute("businessName", a.getBusinessName());
            if (a.getVatIdentificationNumber() != null) d.setAttribute("vatid", a.getVatIdentificationNumber());
            if (a.getAddress() != null) d.setAttribute("address", a.getAddress());
            if (a.getCity() != null) d.setAttribute("city", a.getCity());
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
            if (a.getCity() != null) d.setAttribute("city", a.getCity());
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

                if (xl.getBooking() != null) {
                    Booking b = xl.getBooking();
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

            xml.setAttribute("paid", nf.format(0));
            xml.setAttribute("pending", nf.format(getTotal().getValue()));

        }




        return xml;
    }


    @PrePersist
    public void prePersist() {
        if (number == null) number = UUID.randomUUID().toString();
    }


    @Override
    public String toString() {
        return number;
    }


    public abstract String getXslfo(EntityManager em);


    @Action(order = 2, icon = VaadinIcons.FILE)
    public URL pdf() throws Throwable {
        URL[] url = new URL[1];


        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {

                long t0 = new Date().getTime();

                try {

                    Element xml = toXml(em);

                    try {
                        String archivo = UUID.randomUUID().toString();

                        File temp = (System.getProperty("tmpdir") == null)?File.createTempFile(archivo, ".pdf"):new File(new File(System.getProperty("tmpdir")), archivo + ".pdf");

                        System.out.println("java.io.tmpdir=" + System.getProperty("java.io.tmpdir"));
                        System.out.println("Temp file : " + temp.getAbsolutePath());

                        FileOutputStream fileOut = new FileOutputStream(temp);
                        //String sxslfo = Resources.toString(Resources.getResource(Contract.class, xslfo), Charsets.UTF_8);
                        String sxml = new XMLOutputter(Format.getPrettyFormat()).outputString(xml);
                        System.out.println("xml=" + sxml);
                        fileOut.write(Helper.fop(new StreamSource(new StringReader(getXslfo(em))), new StreamSource(new StringReader(sxml))));
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

}
