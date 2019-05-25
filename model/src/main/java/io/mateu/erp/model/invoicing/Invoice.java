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
    @ListColumn
    private boolean paid;

    @KPI
    private double balance;

    @Output
    private double retainedPercent;

    @Output
    private double retainedTotal;

    @ManyToOne
    @Output
    private VAT vat;

    @Output
    private boolean specialRegime;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "invoice")
    @Output
    private List<VATLine> VATLines = new ArrayList<>();


    @ManyToOne
    @Output
    private RebateSettlement rebateSettlement;

    @ManyToOne
    @Output
    private VATSettlement vatSettlement;

    @OneToMany(mappedBy = "invoice")
    @OrderColumn(name = "id")
    @UseLinkToListView(addEnabled = true, deleteEnabled = true)
    private List<InvoicePaymentAllocation> payments = new ArrayList<>();



    public Invoice() {

    }

    public Invoice(User u, Collection<? extends BookingCharge> charges, boolean proforma, FinancialAgent issuer, FinancialAgent recipient, String invoiceNumber) throws Throwable {

        if (charges == null || charges.size() == 0) throw new Exception("Can not create invoices from an empty list of charges");

        setRecipient(recipient);
        setIssuer(issuer);
        setNumber(invoiceNumber);

        boolean inicializar = true;


        double total = 0;

        Map<VAT, Map<Double, Double>> vats = new HashMap<>();

        Map<BillingConcept, Double> vatPercents = new HashMap<>();
        if (issuer.getVat() != null) {
            for (VATPercent vp : issuer.getVat().getPercents()) {
                vatPercents.put(vp.getBillingConcept(), vp.getPercent());
            }
        }


        double totalExento = 0;
        Map<VAT, Double> totalRegimenEspecial = new HashMap<>();

        Map<Booking, Boolean> specialRegimeValuesPerBooking = new HashMap<>();
        Map<Booking, Boolean> includesHotelOrTransportPerBooking = new HashMap<>();
        Map<Booking, Boolean> specialRegimeProvidersPerBooking = new HashMap<>();
        for (BookingCharge c : charges) {
            specialRegimeValuesPerBooking.put(c.getBooking(), specialRegimeValuesPerBooking.getOrDefault(c.getBooking(), c.getBooking().isSpecialRegime() || (c.getBooking().getFile() != null && c.getBooking().getFile().isSpecialRegime())) || c.getBillingConcept().isSpecialRegime());
            includesHotelOrTransportPerBooking.put(c.getBooking(), includesHotelOrTransportPerBooking.getOrDefault(c.getBooking(), c.getBooking().isHotelOrTransportIncluded() || (c.getBooking().getFile() != null && c.getBooking().getFile().isHotelOrTransportIncluded())) || c.getBillingConcept().isHotelIncluded() || c.getBillingConcept().isTransportIncluded());
        }
        specialRegimeValuesPerBooking.keySet().forEach(b -> {
            boolean hay = false;
            Set<Service> services = Sets.newHashSet(b.getServices());
            if (b.getFile() != null) b.getFile().getBookings().forEach(bx -> services.addAll(bx.getServices()));
            for (Service service : services) {
                if (service.getProvider() != null && service.getProvider().getFinancialAgent() != null) hay |= service.getProvider().getFinancialAgent().isSpecialRegime();
                else hay = true;
            }
            specialRegimeProvidersPerBooking.put(b, hay);
        });


        includesHotelOrTransportPerBooking.keySet().forEach(b -> {
            if (!specialRegimeValuesPerBooking.get(b)) specialRegimeValuesPerBooking.put(b, issuer.getVat() == null || specialRegimeProvidersPerBooking.get(b));
        });

        for (BookingCharge c : charges) {

            if (inicializar) {

                setType((ChargeType.SALE.equals(c.getType()))?InvoiceType.ISSUED:InvoiceType.RECEIVED);

                setAudit(new Audit(u));

                setTotal(0);
                setCurrency(c.getCurrency());

                setIssueDate(LocalDate.now());
                setDueDate(LocalDate.now());


                if (this instanceof IssuedInvoice) {
                    if (c.getAgency().getFinancialAgent() == null) throw new Exception("If you want to create proformas or invoices you must set the financial agent for the agency " + c.getAgency().getName());
                    if (c.getAgency().getCompany() == null) throw new Exception("If you want to create proformas or invoices you must set the company for the agency " + c.getAgency().getName());
                    if (c.getAgency().getCompany().getFinancialAgent() == null) throw new Exception("If you want to create proformas or invoices you must set the financial agent for the company " + c.getAgency().getCompany().getName());

                    setRecipient(c.getAgency().getFinancialAgent());
                    setIssuer(c.getAgency().getCompany().getFinancialAgent());

                    if (!proforma) {
                        if (InvoiceType.ISSUED.equals(type)) {
                            ((IssuedInvoice)this).setSerial(c.getAgency().getCompany().getBillingSerial());
                            if (((IssuedInvoice)this).getSerial() == null) throw new Exception("Missing invoice serial. Please set at company " + c.getAgency().getCompany().getName());
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

            getLines().add(new BookingInvoiceLine(this, (BookingCharge) c));
            if (!proforma) {
                c.setInvoice(this);
            }

            total += c.getTotal();


            VAT vat = null;
            if (LocalizationRule.CUSTOMER.equals(c.getBillingConcept().getLocalizationRule())) vat = recipient.getVat();
            else if (LocalizationRule.SERVICE.equals(c.getBillingConcept().getLocalizationRule())) {
                if (c.getBooking() instanceof HotelBooking) vat = getVatForResort(((HotelBooking) c.getBooking()).getHotel().getResort());
                else if (c.getBooking() instanceof TransferBooking) vat = getVatForResort(((TransferBooking) c.getBooking()).getOrigin().getResort());
                else if (c.getBooking() instanceof ExcursionBooking) vat = getVatForResort(((ExcursionBooking) c.getBooking()).getExcursion().getResort());
                else if (c.getBooking() instanceof CircuitBooking) vat = getVatForResort(((CircuitBooking) c.getBooking()).getCircuit().getResort());
                else if (c.getBooking() instanceof GenericBooking) vat = getVatForResort(((GenericBooking) c.getBooking()).getProduct().getResort());
                else if (c.getBooking() instanceof FreeTextBooking) vat = getVatForResort(((FreeTextBooking) c.getBooking()).getOffice().getResort());
                else vat = issuer.getVat();
            } else vat = issuer.getVat();

            if (issuer.getVat() != null && vatPercents.containsKey(c.getBillingConcept())) {
                if (specialRegimeValuesPerBooking.get(c.getBooking())) {
                    double antes = totalRegimenEspecial.getOrDefault(issuer.getVat(), 0d);
                    totalRegimenEspecial.put(issuer.getVat(), antes + c.getTotal());
                } else {
                    Map<Double, Double> m = vats.get(issuer.getVat());
                    if (m == null) vats.put(issuer.getVat(), m = new HashMap<>());
                    double p = vatPercents.get(c.getBillingConcept());
                    double v = m.containsKey(p)?m.get(p):0;
                    m.put(p, v + c.getTotal());
                }
            } else {
                totalExento += c.getTotal();
            }

        }


        for (VAT v : vats.keySet()) {

            for (double p : vats.get(v).keySet()) {
                VATLine l;
                getVATLines().add(l = new VATLine());

                l.setInvoice(this);
                l.setPercent(p);
                l.setTotal(Helper.roundEuros(vats.get(v).get(p)));
                l.setBase(Helper.roundEuros(100d * (l.getTotal() / (100d + p))));
                l.setVat(v);
            }

        }

        totalExento = Helper.roundEuros(totalExento);
        if (totalExento != 0) {
            VATLine l;
            getVATLines().add(l = new VATLine());
            l.setInvoice(this);
            l.setPercent(0);
            l.setTotal(0);
            l.setBase(totalExento);
            l.setVat(null);
            l.setExempt(true);
        }

        tener en cuenta coste, derivado de las líneas de cargo de compra asociadas con esta factura

        totalRegimenEspecial.keySet().forEach(v -> {
            double t = Helper.roundEuros(totalRegimenEspecial.get(v));
            if (t != 0) {
                VATLine l;
                getVATLines().add(l = new VATLine());
                l.setInvoice(this);
                l.setPercent(0);
                l.setTotal(0);
                l.setBase(t);
                l.setVat(v);
                l.setSpecialRegime(true);
            }
        });


        setTotal(total);

        setRetainedPercent(0);

    }

    private VAT getVatForResort(Resort resort) {
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
            el.setAttribute("vat", nf.format(l.getVat()));
            el.setAttribute("total", nf.format(l.getTotal()));

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
        double p = 0;
        for (InvoicePaymentAllocation payment : payments) {
            p += payment.getValue();
        }
        setBalance(Helper.roundEuros(total - p));
        setPaid(getBalance() <= 0);
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

}
