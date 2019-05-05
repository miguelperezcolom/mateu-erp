package io.mateu.erp.model.booking;

import com.google.common.base.Strings;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Grid;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.themes.ValoTheme;
import io.mateu.erp.model.booking.parts.HotelBooking;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.*;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.invoicing.BookingCharge;
import io.mateu.erp.model.invoicing.Charge;
import io.mateu.erp.model.invoicing.ChargeType;
import io.mateu.erp.model.invoicing.ExtraBookingCharge;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.partners.Agency;
import io.mateu.erp.model.partners.CommissionAgent;
import io.mateu.erp.model.partners.Provider;
import io.mateu.erp.model.payments.*;
import io.mateu.erp.model.product.AbstractContract;
import io.mateu.erp.model.revenue.ProductLine;
import io.mateu.erp.model.tpv.TPV;
import io.mateu.erp.model.tpv.TPVTRANSACTIONSTATUS;
import io.mateu.erp.model.tpv.TPVTransaction;
import io.mateu.erp.model.workflow.AbstractTask;
import io.mateu.erp.model.workflow.SendEmailTask;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.interfaces.GridDecorator;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.User;
import io.mateu.mdd.core.model.common.Resource;
import io.mateu.mdd.core.model.config.Template;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.workflow.WorkflowEngine;
import lombok.Getter;
import lombok.Setter;
import org.javamoney.moneta.FastMoney;
import org.jdom2.Document;
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
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

@Entity
@Getter@Setter
@UseIdToSelect
@Table(indexes = { @Index(name = "i_booking_deprecated", columnList = "deprecated") })
public abstract class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Order(desc = true, priority = 10)
    @ListColumn
    private long id;

    @Version
    private int version;


    @Section("Service")
    @Embedded
    private Audit audit;

    @ListColumn
    @ManyToOne
    @ColumnWidth(100)
    @NoChart@SearchFilter
    private File file;

    @ManyToOne
    @NotNull
    @ListColumn
    @ColumnWidth(200)
    @NoChart
    @MainSearchFilter
    private Agency agency;

    public void setAgency(Agency agency) {
        this.agency = agency;
        setCurrency(agency.getCurrency());
    }

    @NotNull
    @MainSearchFilter
    @ListColumn
    @ColumnWidth(120)
    private String agencyReference;

    @ColumnWidth(200)
    @ListColumn
    @MainSearchFilter
    private String leadName;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "booking")
    @UseLinkToListView
    private List<Passenger> passengers = new ArrayList<>();

    private String email;

    private String telephone;

    @ListColumn
    @NotInEditor
    @HtmlCol
    @ColumnWidth(100)
    private String icons;


    @Output
    private boolean documentationSent;


    @KPI
    @ListColumn
    @ColumnWidth(80)
    private boolean confirmed = false;

    @NotWhenCreating
    @KPI
    @ListColumn
    @ColumnWidth(60)
    private boolean active = true;

    @NotWhenCreating
    @KPI
    @ListColumn
    @ColumnWidth(60)
    private boolean available;

    @Ignored
    private boolean deprecated;

    @NotNull
    @ListColumn
    @ColumnWidth(132)
    private LocalDate start;
    @Column(name = "_end")
    @SameLine
    @NotNull
    @ListColumn
    @ColumnWidth(132)
    private LocalDate end;

    private int adults;
    @SameLine
    private int children;
    @SameLine
    private int[] ages;

    @TextArea
    private String specialRequests;


    @TextArea
    private String commentsForProvider;


    @Section("Sale")
    @TextArea
    @SameLine
    private String privateComments;


    @NotWhenEditing
    private transient boolean confirmNow = true;


    @ListColumn
    @ColumnWidth(156)
    @NotInEditor
    private String description;

    @ManyToOne
    @NotNull
    @ListColumn
    @SearchFilter
    @ColumnWidth(156)
    private PointOfSale pos;


    private LocalDateTime formalizationDate;

    private LocalDateTime expiryDate;

    private boolean locked;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "booking", orphanRemoval = true)
    @UseLinkToListView
    private List<BookingDueDate> dueDates = new ArrayList<>();



    @KPI
    @ListColumn
    @Money
    @Sum
    private double totalValue;

    @KPI
    @ListColumn
    @Money
    @Sum
    private double totalNetValue;

    @KPI
    @SameLine
    @ListColumn
    @Money
    @Sum
    private double totalCost;

    @KPI
    @SameLine
    @ListColumn
    @Money
    @Sum
    private double totalCommission;

    @KPI
    @SameLine
    @ListColumn
    @Money
    @Sum
    private double totalMarkup;

    @KPI
    @SameLine
    @ListColumn
    @Money
    @Sum
    private double totalPaid;

    @KPI
    @SameLine
    @ListColumn
    @Money
    @Sum
    private double balance;


    public void setBalance(double balance) {
        this.balance = balance;
        if (totalPaid > 0) {
            setConfirmed(true);
            setConfirmNow(true);
        }
    }

    @KPI
    @ManyToOne@NotNull
    private Currency currency;

    public void setCurrency(Currency currency) {
        this.currency = currency;
        if (currency != null) setCurrencyExchange(currency.getExchangeRateToNucs());
    }

    @KPI
    private double currencyExchange;

    @KPI
    private double valueInNucs;

    @KPI
    private double costInNucs;



    private boolean valueOverrided;
    @SameLine
    private double overridedValue;

    public boolean isOverridedValueVisible() {
        return valueOverrided;
    }

    @ManyToOne
    private Provider provider;

    private boolean costOverrided;
    @SameLine
    private double overridedCost;

    @SameLine
    @ManyToOne
    private Currency overridedCostCurrency;

    public boolean isOverridedCostVisible() {
        return costOverrided;
    }


    @ManyToOne
    private BillingConcept overridedBillingConcept;

    public boolean isOverridedBillingConceptVisible() {
        return valueOverrided || costOverrided;
    }

    @ManyToOne
    private AbstractContract contract;

    @Output
    private String priceReport;

    //@ListColumn
    //@CellStyleGenerator(ValidCellStyleGenerator.class)
    @Output
    //@ColumnWidth(120)
    private ValidationStatus validationStatus = ValidationStatus.VALID;

    @Output
    @SameLine
    private String validationMessage;

    @KPI
    private boolean valued;

    @KPI
    private boolean costValued;

    @KPI
    private boolean paid;

    private boolean alreadyInvoiced;

    private boolean alreadyPurchased;


    @UseLinkToListView
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    @NotWhenCreating
    @OrderColumn(name = "_orderInBooking")
    private List<Service> services = new ArrayList<>();

    @NotWhenCreating
    @UseLinkToListView(fields = "text,nucs,invoice")
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingCharge> serviceCharges = new ArrayList<>();

    @NotWhenCreating
    @UseLinkToListView(fields = "text,nucs,invoice", addEnabled = true, deleteEnabled = true)
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<ExtraBookingCharge> extraCharges = new ArrayList<>();

    @OneToMany(mappedBy = "booking")
    @OrderColumn(name = "id")
    @UseLinkToListView(addEnabled = true, deleteEnabled = true)
    private List<BookingPaymentAllocation> payments = new ArrayList<>();

    @OneToMany(mappedBy = "booking", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @UseLinkToListView
    private List<TPVTransaction> TPVTransactions = new ArrayList<>();

    @NotWhenCreating
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CancellationTerm> cancellationTerms = new ArrayList<>();


    @ManyToOne
    @SearchFilter
    private CommissionAgent commissionAgent;

    private boolean nonCommissionable;

    @Embedded
    private BookingInvoiceData invoiceData;

    private String agentName;


    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name="booking_task",
            joinColumns=@JoinColumn(name="bookings_ID"),
            inverseJoinColumns=@JoinColumn(name="tasks_ID"))
    @SearchFilter(field = "id")
    @Caption("Task Id")
    @UseLinkToListView
    private List<AbstractTask> tasks = new ArrayList<>();


    @UseLinkToListView
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    @NotWhenCreating
    private List<BookingChange> changes = new ArrayList<>();


    @UseLinkToListView
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotWhenCreating
    private List<BookingCommission> commissions = new ArrayList<>();


    @Ignored
    private String signature;

    @Ignored
    private String changesControlSignature;

    @Ignored
    private String priceSignature;

    @Ignored
    private String docSignature;

    @Ignored
    private LocalDateTime changeRecordRqTime = LocalDateTime.now();

    @Ignored
    private LocalDateTime updateRqTime = LocalDateTime.now();

    @Ignored
    private LocalDateTime priceUpdateRqTime = LocalDateTime.now();

    @Ignored
    private boolean forcePre = false;

    @Ignored
    private String qrCode;

    public String createChangeControlSignature() {
        try {
            return Helper.toJson(getChangeControlData());
        } catch (Exception e) {
            return "" + e.getClass().getSimpleName() + ": " + e.getMessage();
        }
    }

    public Map<String,String> getChangeControlData() {
        Map<String,String> data = new HashMap<>();

        if (getFile() != null) data.put("File", "" + getFile().getId());
        if (getAgency() != null) data.put("Agency", "" + getAgency().getName());
        if (getAgencyReference() != null) data.put("Agency ref.", getAgencyReference());
        if (getLeadName() != null) data.put("Lead name", getLeadName());
        passengers.forEach(p -> data.put("Passenger " + passengers.indexOf(p), p.toString()));
        if (getEmail() != null) data.put("Email", getEmail());
        if (getTelephone() != null) data.put("Tel.", getTelephone());
        data.put("Confirmed", isConfirmed()?"Yes":"No");
        data.put("Active", isActive()?"Yes":"No (Cancelled)");
        if (getStart() != null) data.put("Start", "" + getStart());
        if (getEnd() != null) data.put("End", "" + getEnd());
        data.put("Adults", "" + getAdults());
        data.put("Children", "" + getChildren());
        if (getAges() != null) data.put("Ages", "" + Arrays.toString(getAges()));
        if (getSpecialRequests() != null) data.put("Special reqs.", getSpecialRequests());
        if (getPrivateComments() != null) data.put("Private comms.", getPrivateComments());
        if (getPos() != null) data.put("POS", getPos().getName());
        if (getFormalizationDate() != null) data.put("Formalization date", "" + getFormalizationDate());
        if (getExpiryDate() != null) data.put("Expiry date", "" + getExpiryDate());
        data.put("Locked", isLocked()?"Yes":"No");
        dueDates.forEach(p -> data.put("Due date " + dueDates.indexOf(p), p.toString()));
        data.put("Total value", "" + totalValue);
        data.put("Total net", "" + totalNetValue);
        data.put("Total cost", "" + totalCost);
        data.put("Total paid", "" + totalPaid);
        data.put("Balance", "" + balance);
        data.put("Currency exchange", "" + currencyExchange);
        if (getCurrency() != null) data.put("Currency", getCurrency().getIsoCode());
        data.put("Value overrided", isValueOverrided()?"Yes":"No");
        if (isValueOverrided()) data.put("Overrided value", "" + getOverridedValue());
        if (getProvider() != null) data.put("Provider", getProvider().getName());
        data.put("Cost overrided", isCostOverrided()?"Yes":"No");
        if (isCostOverrided()) data.put("Overrided cost", "" + getOverridedCost());
        if (isCostOverrided() || isValueOverrided()) data.put("Overrided billing concept", getOverridedBillingConcept() != null?getOverridedBillingConcept().getName():"None");
        if (getContract() != null) data.put("Contract", getContract().getTitle());
        data.put("Valued", isValued()?"Yes":"No");
        data.put("Paid", isPaid()?"Yes":"No");
        data.put("Already invoiced", isAlreadyInvoiced()?"Yes":"No");
        data.put("Already purchased", isAlreadyPurchased()?"Yes":"No");
        data.put("Validation", getValidationStatus().name());
        extraCharges.forEach(p -> data.put("Extra charge " + extraCharges.indexOf(p), p.toChangeControlString()));
        payments.forEach(p -> data.put("Payment " + payments.indexOf(p), p.toString()));
        cancellationTerms.forEach(p -> data.put("Cancellation term " + cancellationTerms.indexOf(p), p.toString()));
        if (getCommissionAgent() != null) data.put("Commission agent", getCommissionAgent().getName());
        if (getInvoiceData() != null) data.put("Invoice date", getInvoiceData().toString());
        if (getAgentName() != null) data.put("Agent name", getAgentName());
        data.put("Non commissionable", isNonCommissionable()?"Yes":"No");

        completeChangeSignatureData(data);

        return data;
    }

    protected abstract void completeChangeSignatureData(Map<String,String> data);


    @Override
    public String toString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return "" + getId() + " - " + (getAgency() != null?getAgency().getName():"No agency") + " - " + getLeadName() + ": " + Helper.capitalize(getClass().getSimpleName().replaceAll("Booking", "")) + " from " + ((start != null)?start.format(dtf):"-") + " to " + ((end != null)?end.format(dtf):"-");
    }

    public Map<String,Object> getData(EntityManager em) throws Throwable {
        Map<String, Object> d = Helper.getGeneralData();

        DecimalFormat df = new DecimalFormat("###,###,###,###,##0.00");

        d.put("us", getAgency().getCompany().getName());
        d.put("bookingemail", getPos().getOffice().getEmail());


        d.put("start", getStart());
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        d.put("serviceDates", "" + ((getStart() != null)?getStart().format(f):"...") + " - " + ((getEnd() != null)?getEnd().format(f):"..."));
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
            todoCancelado &= !s.isActive();
            allServicesAreValued &= s.isSaleValued();
            //todo: revisar!
            /*
            allPurchasesAreValued &= s.isPurchaseValued();
            if (!Strings.isNullOrEmpty(s.getBooking().getSpecialRequests())) comentarios += s.getBooking().getSpecialRequests();
            if (!Strings.isNullOrEmpty(s.getOperationsComment())) comentarios += s.getOperationsComment();
            totalCost += s.getTotalCost();
            */
        }

        d.put("valued", isValued());
        d.put("total", getTotalNetValue());
        d.put("purchaseValued", allPurchasesAreValued);
        d.put("totalCost", totalCost);

        d.put("id", getId());
        d.put("locator", getId());
        d.put("leadName", getLeadName());
        d.put("telephone", getTelephone());
        d.put("email", getEmail());
        //d.put("agency", getAgency().getName());
        //d.put("agencyReference", getAgencyReference());
        d.put("status", (todoCancelado)?"CANCELLED":"ACTIVE");
        d.put("created", getAudit().getCreated().format(DateTimeFormatter.BASIC_ISO_DATE.ISO_DATE_TIME));
        if (getFormalizationDate() != null) d.put("formalization", getFormalizationDate().toString());
        else d.put("formalization", getAudit().getCreated().format(DateTimeFormatter.BASIC_ISO_DATE.ISO_DATE_TIME));
        d.put("office", "");

        d.put("comments", comentarios);
        d.put("direction", String.join(",", points));
        d.put("pax", pax);


    //todo: recuperar
        //d.put("availstatus", active?(confirmed?(available?"CONFIRMADA":"ON REQUEST"):"REALIZADA"):"CANCELADA");
        d.put("availstatus", active?(confirmed?"CONFIRMADA":"REALIZADA"):"CANCELADA");
        d.put("paymentstatus", active?(paid?"PAGADA":"PENDIENTE DE PAGO"):"--");


        d.put("paymentamounttxt", df.format(getTotalNetValue()) + " &euro;");

        if (agency.getCompany() != null) {
            d.put("bankname", agency.getCompany().getBankName());
            d.put("bankaddress", agency.getCompany().getBankAddress());
            d.put("recipient", agency.getCompany().getRecipient());
            d.put("accountnumber", agency.getCompany().getAccountNumber());
            d.put("swift", agency.getCompany().getSwift());
        }

        d.put("agency", agency.getName());
        if (agency.getFinancialAgent() != null && agency.getFinancialAgent().isDirectSale()) {
            d.put("agencyaddress", agency.getName());
            d.put("agencyvatid", agency.getName());
            d.put("agentname", agentName);
            d.put("agencyemail", agency.getName());
            d.put("agencytelephone", agency.getName());
        } else if (getInvoiceData() != null) {
            d.put("agencyaddress", getInvoiceData().getCompanyName());
            d.put("agencyvatid", getInvoiceData().getVatId());
            d.put("agentname", agentName);
            d.put("agencyemail", getEmail());
            d.put("agencytelephone", getTelephone());
        }

        if (getTPVTransactions().size() > 0 && TPVTRANSACTIONSTATUS.PENDING.equals(getTPVTransactions().get(0).getStatus())) {
            d.put("paymentlink", getTPVTransactions().get(0).getBoton(em));
        }


        d.put("servicedata", getServiceDataHtml());

        d.put("productdata", getProductDataHtml());

        d.put("totalretail", df.format(getTotalValue()) + " &euro;");
        d.put("totalnet", df.format(getTotalNetValue()) + " &euro;");
        if (agency.getFinancialAgent() != null) d.put("paymentmethod", agency.getFinancialAgent().getMethodOfPayment());
        d.put("payments", getPaymentsDataHtml(em));

        d.put("paymentremarks", getPaymentRemarksHtml());

        d.put("bookingterms", getBookingTermsHtml());

        d.put("cancellationterms", getCancellationTermsHtml());

        return d;
    }

    public String getCancellationTermsHtml() {
        String h = "";

        h += "<p>Sin condiciones especiales.</p>";

        if (false) h +=
                "                <p>- Devolución total del depósito cuando la anulación se realice con más de 20 días de antelación sobre la fecha de entrada.</p>\n" +
                "                <p>- Pérdida del depósito cuando la anulación se realice durante los 20 días anteriores a la fecha de entrada.</p>\n";

        return h;
    }

    public String getBookingTermsHtml() {
        String h = "";

        h += "<p>Sin condiciones especiales.</p>";

        if (false) h +=
                "<p>Usted está reservando directamente con el Hotel.</p>\n" +
                "                <p>- Usted autoriza al Hotel Voramar a realizar un cobro, en concepto de depósito, mediante la tarjeta de crédito facilitada.</p>\n" +
                "                <p>- El pago de la parte pendiente de la reserva se ralizará una vez lleguen al hotel.</p>\n" +
                "                <p>- Esta es una confirmación de rserva para habitación estándard (twin bedroom), si desea un nº de habitación concreto o posición, lamentamos no poder garantizarla, sin embargo haremos lo posible para asignarla si tenemos disponibilidad el día de su entrada.</p>\n" +
                "                <p>- Check-in a partir de las 14:00 horas, chak-out máximo a las 12:00 horas.</p>";

        return h;
    }

    public String getPaymentRemarksHtml() {
        String h = "";

        if (getPos().getTpv() != null) h += "<p>Pago a realizar online utilizando " + getPos().getTpv().getName() + ".</p>";
        else h += "<p>Sin forma de pago concreta.</p>";

        if (false) h =
                "                        <p>Tasa turística no incluida en el precio de la habitación.</p>\n" +
                "\n" +
                "                        <p>Se cargará la tasa en el momento del check-in.</p>\n" +
                "\n" +
                "                        <p>Menores de 16 años, exentos de dicha tasa.</p>\n" +
                "\n" +
                "                        <p>Del 1 de Noviembre al 30 de Abril, la tasa turística es de 0,55 € por persona por noche.</p>\n" +
                "\n" +
                "                        <p>Del 1 de Mayo al 31 de Octubre, la tasa turística es de 2,2 € por persona por noche.</p>\n" +
                "\n" +
                "                        <p>A partir del 9º día de estancia este impuesto se reduce al 50%</p>\n";

        return h;
    }

    public String getPaymentsDataHtml(EntityManager em) {

        DecimalFormat df = new DecimalFormat("###,###,###,###,##0.00");

        String h = "";

        h += "                            <tr><td>Total a pagar ahora</td><td align='right'>" + df.format(getTotalValue()) + " &euro;</td></tr>\n";

        if (false) h +=
                "                            <tr><td>Depósito a pagar ahora</td><td align='right'>1.152,00 &euro;</td></tr>\n" +
                "                            <tr><td>A pagar el 13/07/2018</td><td align='right'>2.567,00 &euro;</td></tr>\n";

        return h;
    }

    public String getProductDataHtml() {
        return "";
    }

    public String getServiceDataHtml() {
        return "";
    }


    public static Booking getByAgencyRef(EntityManager em, String agencyRef, Agency agency)
    {
        try {
            String jpql = "select x from " + Booking.class.getName() + " x" +
                    " where x.agencyReference='" + agencyRef + "' and x.agency.id= " + agency.getId();
            Query q = em.createQuery(jpql).setFlushMode(FlushModeType.COMMIT);
            List<Booking> l = q.getResultList();
            Booking b = (l.size() > 0)?l.get(0):null;
            return b;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }


    @Action(order = 1)
    public static void searchAvailable() {

    }

    @Action(order = 0, icon = VaadinIcons.MAP_MARKER)
    public BookingMap map() {
        return new BookingMap(this);
    }

    @Action(order = 1, icon = VaadinIcons.ENVELOPES, saveBefore = true, saveAfter = true)
    @NotWhenCreating
    public void sendBooked(EntityManager em, String changeEmail, String postscript) throws Throwable {

        long t0 = new Date().getTime();

        AppConfig appconfig = AppConfig.get(em);

        String to = changeEmail;
        if (Strings.isNullOrEmpty(to)) {
            if (getAgency().getFinancialAgent() != null && getAgency().getFinancialAgent().isDirectSale()) {
                to = getEmail();
            } else {
                to = getAgency().getEmail();
            }
        }
        if (Strings.isNullOrEmpty(to)) throw new Exception("No valid email address. Please check the agency " + getAgency().getName() + " and fill the email field.");


        SendEmailTask t;
        tasks.add(t = new SendEmailTask());

        if (getPos() != null && getPos().getOffice() != null) {
            t.setOffice(getPos().getOffice());
        }
        String status = "received";
        if (isConfirmed()) status = "confirmed";
        if (!isActive()) status = "cancelled";
        t.setSubject("Booking " + getId() + " " + status);
        t.setTo(to);
        t.setAudit(new Audit(MDD.getCurrentUser()));
        t.setDescription("Send booked email");
        t.getBookings().add(this);

        String msg = postscript;

        String freemark = appconfig.getBookedEmailTemplate();

        if (!Strings.isNullOrEmpty(freemark)) {
            Map<String, Object> data = getData(em);
            data.put("postscript", postscript);
            msg = Helper.freemark(freemark, data);
        }

        t.setMessage(msg);

        if (confirmed && active) {
            adjuntarVouchers(em, t);
        }

        setDocumentationSent(true);

    }

    @Action(order = 2, icon = VaadinIcons.ENVELOPES, saveBefore = true, saveAfter = true)
    @NotWhenCreating
    public void sendVouchers(String changeEmail, String postscript) throws Throwable {


        Helper.transact(em ->{

            long t0 = new Date().getTime();

            AppConfig appconfig = AppConfig.get(em);

            String to = changeEmail;
            if (Strings.isNullOrEmpty(to)) {
                if (getAgency().getFinancialAgent() != null && getAgency().getFinancialAgent().isDirectSale()) {
                    to = getEmail();
                } else {
                    to = getAgency().getEmail();
                }
            }
            if (Strings.isNullOrEmpty(to)) throw new Exception("No valid email address. Please check the agency " + getAgency().getName() + " and fill the email field.");


            SendEmailTask t;
            tasks.add(t = new SendEmailTask());

            if (getPos() != null && getPos().getOffice() != null) {
                t.setOffice(getPos().getOffice());
            }
            t.setSubject("Vouchers for booking " + getId() + "");
            t.setTo(to);
            t.setAudit(new Audit(MDD.getCurrentUser()));
            t.setDescription("Send vouchers email");
            t.getBookings().add(this);

            String msg = postscript;

            String freemark = appconfig.getVouchersEmailTemplate();

            if (!Strings.isNullOrEmpty(freemark)) {
                Map<String, Object> data = getData(em);
                msg = Helper.freemark(freemark, data);
            }

            t.setMessage(msg);


            adjuntarVouchers(em, t);


            // fin crear vouchers

            em.merge(this);


        });


    }

    private void adjuntarVouchers(EntityManager em, SendEmailTask t) throws Exception {
        // creamos vouchers

        if (getServices().stream().filter(s -> s.isActive()).count() > 0) {
            String archivo = UUID.randomUUID().toString();
            java.io.File temp = (System.getProperty("tmpdir") == null)? java.io.File.createTempFile(archivo, ".pdf"):new java.io.File(new java.io.File(System.getProperty("tmpdir")), archivo + ".pdf");

            writeVouchers(em, temp);


            if (temp != null) t.getAttachments().add(new Resource(temp));
        }

    }

    public void writeVouchers(EntityManager em, java.io.File temp) throws Exception {

        Document xml = new Document(new Element("services"));

        if (AppConfig.get(em).getLogo() != null) xml.getRootElement().setAttribute("urllogo", "file:" + AppConfig.get(em).getLogo().toFileLocator().getTmpPath());

        services.stream().filter(s -> s.isActive()).forEach(s -> {
            xml.getRootElement().addContent(s.toXml(true));
        });

        System.out.println(Helper.toString(xml.getRootElement()));

        System.out.println("java.io.tmpdir=" + System.getProperty("java.io.tmpdir"));
        System.out.println("Temp file : " + temp.getAbsolutePath());

        FileOutputStream fileOut = new FileOutputStream(temp);
        //String sxslfo = Resources.toString(Resources.getResource(Contract.class, xslfo), Charsets.UTF_8);
        String sxml = new XMLOutputter(Format.getPrettyFormat()).outputString(xml);
        System.out.println("xml=" + sxml);
        fileOut.write(Helper.fop(new StreamSource(new StringReader(AppConfig.get(em).getXslfoForVoucher())), new StreamSource(new StringReader(sxml))));
        fileOut.close();

    }

    @Action(order = 3, icon = VaadinIcons.ENVELOPE, saveBefore = true, saveAfter = true)
    @NotWhenCreating
    public void sendEmail(@Help("If blank the postscript will be sent as the email body") Template template, String changeEmail, @Help("If blank, the subject from the templaet will be used") String subject, @TextArea String postscript) throws Throwable {


        Helper.transact(em ->{

            long t0 = new Date().getTime();

            AppConfig appconfig = AppConfig.get(em);

            String to = changeEmail;
            if (Strings.isNullOrEmpty(to)) {
                if (getAgency().getFinancialAgent() != null && getAgency().getFinancialAgent().isDirectSale()) {
                    to = getEmail();
                } else {
                    to = getAgency().getEmail();
                }
            }
            if (Strings.isNullOrEmpty(to)) throw new Exception("No valid email address. Please check the agency " + getAgency().getName() + " and fill the email field.");


            SendEmailTask t;
            tasks.add(t = new SendEmailTask());

            if (getPos() != null && getPos().getOffice() != null) {
                t.setOffice(getPos().getOffice());
            }
            t.setTo(to);
            t.setAudit(new Audit(MDD.getCurrentUser()));
            t.getBookings().add(this);

            String msg = postscript;

            String freemark = appconfig.getVouchersEmailTemplate();

            if (template != null) {
                Map<String, Object> data = Helper.getGeneralData();
                data.put("postscript", postscript);
                data.put("leadname", getLeadName());
                t.setDescription("Send email from template " + template.getName());
                t.setSubject(!Strings.isNullOrEmpty(subject) ? subject : template.getSubject());
                msg = Helper.freemark(template.getFreemarker(), data);
            } else {
                t.setDescription("Send email from void template");
                t.setSubject(subject);
                msg = postscript;
            }

            t.setMessage(msg);

            // fin crear vouchers



            em.merge(this);


        });

    }

    @Action(order = 6, confirmationMessage = "Are you sure you want to confirm this booking?", style = ValoTheme.BUTTON_FRIENDLY, icon = VaadinIcons.CHECK)
    @NotWhenCreating
    public void confirm(EntityManager em) {
        setConfirmed(true);
        em.merge(this);
    }

    public boolean isConfirmVisible() {
        return !isConfirmed();
    }


    /*
    @Action(order = 6, confirmationMessage = "Are you sure you want to unconfirm this booking?", style = ValoTheme.BUTTON_DANGER, icon = VaadinIcons.CLOSE)
    @NotWhenCreating
    public void unconfirm(EntityManager em) {
        setConfirmed(false);
        em.merge(this);
    }

    public boolean isUnconfirmVisible() {
        return isConfirmed();
    }
    */


    @Action(order = 7, confirmationMessage = "Are you sure you want to cancel this booking?", style = ValoTheme.BUTTON_DANGER, icon = VaadinIcons.CLOSE, saveAfter = true)
    @NotWhenCreating
    public void cancel(EntityManager em) {
        setActive(false);
    }


    public boolean isCancelVisible() {
        return isActive();
    }


    @Action(order = 7, confirmationMessage = "Are you sure you want to uncancel this booking?", style = ValoTheme.BUTTON_FRIENDLY, icon = VaadinIcons.CHECK, saveAfter = true)
    @NotWhenCreating
    public void uncancel(EntityManager em) throws Exception {
        setActive(true);
    }

    public boolean isUncancelVisible() {
        return !isActive();
    }



    @Action(order = 4, icon = VaadinIcons.EURO, saveBefore = true, saveAfter = true)
    @NotWhenCreating
    public void sendPaymentEmail(@NotEmpty String changeEmail, String subject, String postscript, @NotNull TPV tpv, FastMoney amount) throws Throwable {



        Helper.transact(em ->{

            long t0 = new Date().getTime();

            AppConfig appconfig = AppConfig.get(em);

            String to = changeEmail;
            if (Strings.isNullOrEmpty(to)) {
                if (getAgency().getFinancialAgent() != null && getAgency().getFinancialAgent().isDirectSale()) {
                    to = getEmail();
                } else {
                    to = getAgency().getEmail();
                }
            }
            if (Strings.isNullOrEmpty(to)) throw new Exception("No valid email address. Please check the agency " + getAgency().getName() + " and fill the email field.");


            SendEmailTask t;
            tasks.add(t = new SendEmailTask());

            if (getPos() != null && getPos().getOffice() != null) {
                t.setOffice(getPos().getOffice());
            }
            t.setSubject(subject);
            t.setTo(to);
            t.setAudit(new Audit(MDD.getCurrentUser()));
            t.setDescription("Send payment email for " + amount);
            t.getBookings().add(this);


            TPVTransaction tt = new TPVTransaction();
            tt.setValue(amount.getNumber().doubleValueExact());
            tt.setCurrency(em.find(Currency.class, amount.getCurrency().getCurrencyCode()));
            tt.setBooking(this);
            TPVTransactions.add(tt);
            tt.setLanguage("es");
            tt.setSubject((!Strings.isNullOrEmpty(subject))?subject:"Payment for booking " + getId() + " for " + amount);
            tt.setTpv(tpv);
            em.merge(t);

            String msg = postscript;

            String freemark = appconfig.getPaymentEmailTemplate();

            if (!Strings.isNullOrEmpty(freemark)) {
                Map<String, Object> data = Helper.getGeneralData();
                data.put("postscript", postscript);
                data.put("leadname", getLeadName());
                data.put("paymentbutton", tt.getBoton(em));
                msg = Helper.freemark(freemark, data);
            }

            t.setMessage(msg);

            em.merge(this);

        });


    }

    @Action(order = 5, icon = VaadinIcons.EURO, saveBefore = true, saveAfter = true)
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


            BookingPaymentAllocation a;
            p.getBreakdown().add(a = new BookingPaymentAllocation());
            a.setPayment(p);
            a.setBooking(this);
            getPayments().add(a);
            a.setValue(amount);

            em.persist(p);

        }
    }

    @Action(order = 6, icon = VaadinIcons.INVOICE)
    @NotWhenCreating
    public BookingInvoiceForm invoice() throws Throwable {
        return new BookingInvoiceForm(this);
    }



    @PrePersist@PreUpdate
    public void pre() throws Error {
        try {

            complete();

            if (isValueOverrided() || isValueOverrided()) {
                if (isValueOverrided() && overridedValue == 0) throw new Exception("Overrided value is required. Please fill");
                if (isCostOverrided() && overridedCost == 0) throw new Exception("Overrided cost is required. Please fill");
                if (isCostOverrided() && getOverridedCostCurrency() == null) throw new Exception("Overrided cost currency is required. Please fill");
                if (overridedBillingConcept == null) throw new Exception("Billing concept is required. Please fill");
            }
            validate();

            setDescription(getParticularDescription());

            if (currencyExchange == 0) {
                currencyExchange = currency.getExchangeRateToNucs();
            }

            setValueInNucs(Helper.roundEuros(totalNetValue * currencyExchange));
            setCostInNucs(Helper.roundEuros(totalCost * currencyExchange));
            setTotalMarkup(Helper.roundEuros(totalNetValue - totalCost));


            if (getChangesControlSignature() != null && !getChangesControlSignature().equals(createChangeControlSignature())) {
                setChangeRecordRqTime(LocalDateTime.now());
                System.out.println("change control signature changed from ");
                System.out.println("" + getChangesControlSignature());
                System.out.println("to ");
                System.out.println("" + createChangeControlSignature());
                System.out.println("====================== ");
            }

            if (isConfirmed() && (getSignature() == null || !getSignature().equals(createSignature()))) {
                setUpdateRqTime(LocalDateTime.now());
                System.out.println("service signature changed from ");
                System.out.println("" + getSignature());
                System.out.println("to ");
                System.out.println("" + createSignature());
                System.out.println("====================== ");
            }

            if (getPriceSignature() == null || !getPriceSignature().equals(createPriceSignature())) {
                setPriceUpdateRqTime(LocalDateTime.now());
                System.out.println("price signature changed from ");
                System.out.println("" + getPriceSignature());
                System.out.println("to ");
                System.out.println("" + createPriceSignature());
                System.out.println("====================== ");
            }

            if (getDocSignature() == null || !getDocSignature().equals(createDocSignature())) {
                setDocumentationSent(false);
                System.out.println("doc signature changed from ");
                System.out.println("" + getDocSignature());
                System.out.println("to ");
                System.out.println("" + createDocSignature());
                System.out.println("====================== ");
            }

        } catch (Throwable e) {
            throw new Error(e);
        }

        setForcePre(false);
    }

    private void recordChanges() {
        try {
            Map<String, Object> old = Helper.fromJson(changesControlSignature);
            Map<String, String> current = getChangeControlData();

            List<String> keys = new ArrayList<>(old.keySet());
            current.keySet().forEach(k -> {
                if (!keys.contains(k)) keys.add(k);
            });

            Collections.sort(keys);

            for (String k : keys) {
                if (!old.getOrDefault(k, "Not present").equals(current.getOrDefault(k, "Not present"))) {
                    BookingChange c;
                    getChanges().add(c = new BookingChange());
                    c.setBooking(this);
                    c.setKey(k);
                    c.setOldValue("" + old.getOrDefault(k, "Not present"));
                    c.setNewValue(current.getOrDefault(k, "Not present"));
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @PostPersist@PostUpdate
    public void post() {
        System.out.println("Booking " + getId() + ".post(" + changeRecordRqTime + "," + updateRqTime + "," + priceUpdateRqTime + ")");
        if (changeRecordRqTime != null || updateRqTime != null || priceUpdateRqTime != null) WorkflowEngine.add(new Runnable() {
            @Override
            public void run() {
                System.out.println("Booking " + getId() + ".post(" + changeRecordRqTime + "," + updateRqTime + "," + priceUpdateRqTime + ").run()");
                if (changeRecordRqTime != null || updateRqTime != null || priceUpdateRqTime != null) {
                    try {
                        Helper.transact(em -> {
                            Booking b = em.find(Booking.class, getId());

                            boolean somethingHappened = false;
                            if (b.getUpdateRqTime() != null) {

                                b.build(em);

                                b.setSignature(b.createSignature());
                                b.setUpdateRqTime(null);

                                somethingHappened = true;
                            }
                            if (b.getPriceUpdateRqTime() != null) {

                                b.price(em);

                                b.setPriceSignature(b.createPriceSignature());
                                b.setPriceUpdateRqTime(null);

                                somethingHappened = true;
                            }
                            if (somethingHappened) {
                                b.getAgency().setUpdatePending(true);
                                b.getPos().setUpdatePending(true);
                                if (b.getFile() != null) b.getFile().setUpdateRqTime(LocalDateTime.now());
                            }

                            b.summarize(em);

                            if (b.getChangeRecordRqTime() != null || somethingHappened) {
                                if (b.getChangesControlSignature() != null && !b.getChangesControlSignature().equals(b.createChangeControlSignature())) {
                                    b.recordChanges();
                                }
                                b.setChangesControlSignature(createChangeControlSignature());
                                b.setChangeRecordRqTime(null);
                            }

                            if (b.getPos().getTpv() != null && b.getTPVTransactions().size() == 0) {
                                TPVTransaction t;
                                b.getTPVTransactions().add(t = new TPVTransaction());
                                t.setBooking(b);
                                t.setTpv(b.getPos().getTpv());
                                t.setStatus(TPVTRANSACTIONSTATUS.PENDING);
                                t.setCreated(LocalDateTime.now());
                                t.setLanguage("es");
                                t.setCurrency(b.getCurrency());
                                t.setValue(b.getTotalValue());
                                t.setSubject("Booking " + b.getId());

                                em.persist(t);
                            }

                            b.setForcePre(false);

                        });


                        try {
                            Helper.transact(em -> {
                                Booking b = em.find(Booking.class, getId());

                                if (b.getAgency().isDocumentationRequired() && !b.isDocumentationSent()) {
                                    try {
                                        b.sendBooked(em, null, null);
                                        b.setDocSignature(b.createDocSignature());
                                    } catch (Throwable t) {
                                        t.printStackTrace();
                                    }
                                }

                            });
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }

                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }
        });
    }

    public void complete() {

        if (getId() == 0 && confirmNow) {
            setConfirmed(true);
        }

        if (getCurrency() == null) {
            setCurrency(getAgency().getCurrency());
            setCurrencyExchange(getCurrency().getExchangeRateToNucs());
        }

    }

    public void summarize(EntityManager em) {

        System.out.println("booking " + getId() + ".summarize");

        //todo: actualizar estado, balances, etc

        if (true) {
            boolean allAvailable = true;
            for (Service x : services) {
                if (x.getStart() != null) {
                    if (start == null || start.isAfter(x.getStart())) start = x.getStart();
                    if (end == null || end.isBefore(x.getStart())) end = x.getStart();
                }

                if (x.getFinish() != null) {
                    if (start == null || start.isAfter(x.getFinish())) start = x.getFinish();
                    if (end == null || end.isBefore(x.getFinish())) end = x.getFinish();
                }

                x.setServiceDateForInvoicing(x.getStart());
                if (!x.isAvailable()) allAvailable = false;
            }
            setAvailable(allAvailable);
            updateTotals(em);
        }

    }


    public String getParticularDescription() {
        return Helper.capitalize(getClass().getSimpleName());
    }


    //@Action(order = 8, icon = VaadinIcons.SPLIT)
    @NotWhenCreating
    public void build(EntityManager em) {
        if (confirmed) {
            generateServices(em);
            services.forEach(s -> {
                if (s.isAlreadyPurchasedBefore() != s.isAlreadyPurchased()) {
                    s.setAlreadyPurchased(alreadyPurchased);
                    if (s.isAlreadyPurchased() && s.getAlreadyPurchasedDate() == null) s.setAlreadyPurchasedDate(LocalDateTime.now());
                    else if (!s.isAlreadyPurchased() && s.getAlreadyPurchasedDate() != null) s.setAlreadyPurchasedDate(null);
                }
                if (s.getSignature() == null || !s.getSignature().equals(s.createSignature())) s.setUpdateRqTime(LocalDateTime.now());
            });
        }
    }

    public abstract void validate() throws Exception;

    public abstract void generateServices(EntityManager em);


    @Action(order = 7, icon = VaadinIcons.EURO)
    @NotWhenCreating
    public void price(EntityManager em) throws Throwable {

        System.out.println("Booking " + getId() + ".price()");

        List<PriceBreakdownItem> breakdown = new ArrayList<>();

        if (isValueOverrided()) {
            if (overridedBillingConcept == null) throw new Exception("Billing concept is required. Please fill");
            setTotalValue(overridedValue);
            setValued(true);
            services.forEach(s -> {
                s.setTotalSale(Helper.roundEuros(getOverridedValue() / services.size()));
            });
            breakdown.add(new PriceBreakdownItem(overridedBillingConcept, getDescription(), overridedValue));
        } else {
            setValued(false);
            setTotalValue(0);
            setTotalNetValue(0);
            priceServices(em, breakdown);
            if (!isValued()) {
                services.forEach(s -> s.rateSale(em));
                boolean v = getServices().size() > 0;
                if (v) for (Service service : getServices()) v &= service.isSaleValued();
                double t = 0;
                if (v) for (Service service : getServices()) t += service.getTotalSale();
                setValued(v);
                setTotalValue(Helper.roundEuros(t));
            }
        }

        if (isCostOverrided()) {
            if (overridedBillingConcept == null) throw new Exception("Billing concept is required. Please fill");
            setTotalCost(overridedCost);
            setCostValued(true);
            services.forEach(s -> {
                s.setCostOverrided(isCostOverrided());
                s.setOverridedCostValue(Helper.roundEuros(getOverridedCost() / services.size()));
                s.rateCost(em);
            });
        } else {
            services.forEach(s -> s.rateCost(em));
            boolean v = getServices().size() > 0;
            if (v) for (Service service : getServices()) v &= service.isCostValued();
            setCostValued(v);
        }

        updateCharges(em, breakdown);

        updateTotals(em);

        updateCommissions(em);
    }

    private void updateCommissions(EntityManager em) {
        boolean settled = false;
        double tc = 0;
        for (BookingCommission c : commissions) if (c.getSettlement() != null) settled = true;
        if (!settled) {
            commissions.clear();
            if (commissionAgent != null && isValued() && isCostValued() && totalValue != 0) {
                List<CommissionTerms> lx = AppConfig.get(em).getCommissionTerms();
                if (lx.size() > 0) {
                    CommissionTerms t = lx.get(0);
                    for (CommissionTermsLine l : t.getLines()) {
                        if (l.getAgent().equals(getCommissionAgent()) && l.getProductLine().equals(getEffectiveProductLine())) {
                            BookingCommission com;
                            commissions.add(com = new BookingCommission());
                            com.setBooking(this);
                            com.setAgent(commissionAgent);
                            com.setTotal(Helper.roundEuros((totalValue - totalCost) * l.getPercent() / 100d));
                            tc += com.getTotal();
                        }
                    }
                }
            }
        }
        setTotalCommission(Helper.roundEuros(tc));
    }

    protected abstract ProductLine getEffectiveProductLine();


    public List<BookingCharge> getCharges() {
        List<BookingCharge> l = new ArrayList<>();
        l.addAll(getServiceCharges());
        l.addAll(getExtraCharges());
        return l;
    }


    public synchronized void askForUpdate() {
        updateRqTime = LocalDateTime.now();
    }

    public void updateTotals(EntityManager em) {

        double total = 0;
        double totalNeto = 0;
        double totalCoste = 0;
        double totalPagado = 0;

        for (Charge c : getCharges()) {
            System.out.println("**************************updateTotalsxcharge");
            if (ChargeType.SALE.equals(c.getType())) {
                total += c.getTotal();
                totalNeto += c.getTotal();
            } else {
                //totalCoste += c.getTotal().getValue();
            }
        }

        if (isCostOverrided()) {
            totalCoste = getOverridedCost() * getOverridedCostCurrency().getExchangeRateToNucs() / getCurrency().getExchangeRateToNucs();
        } else {
            for (Service s : getServices()) {
                totalCoste += s.getTotalCost();
            }
        }

        for (BookingPaymentAllocation pa : getPayments()) {
            totalPagado += pa.getValue();
        }


        setTotalValue(Helper.roundEuros(total));
        setTotalNetValue(Helper.roundEuros(totalNeto));
        setTotalCost(Helper.roundEuros(totalCoste));

        setTotalPaid(Helper.roundEuros(totalPagado));
        setBalance(Helper.roundEuros(totalPagado - totalNeto));


        setValueInNucs(Helper.roundEuros(totalNeto * getCurrency().getExchangeRateToNucs()));
        setCostInNucs(Helper.roundEuros(totalCoste * getCurrency().getExchangeRateToNucs()));

        setPaid(getBalance() >= 0);

        updateCancellationTerms(em);

        updateDueDates(em);

        getAgency().setUpdatePending(true);
        getPos().setUpdatePending(true);

        if (getFile() != null) getFile().updateTotals();

    }

    private void updateDueDates(EntityManager em) {
        dueDates.clear();
        if (agency.getFinancialAgent() != null && agency.getFinancialAgent().getCustomerPaymentTerms() != null) {
            if (!RiskType.CREDIT.equals(agency.getFinancialAgent().getRiskType())) {

                PaymentTerms pts = agency.getFinancialAgent().getCustomerPaymentTerms();
                if (pts != null) for (PaymentTermsLine l : pts.getLines()) if (PaymentReferenceDate.ARRIVAL.equals(l.getReferenceDate())) {
                    BookingDueDate pl;
                    dueDates.add(pl = new BookingDueDate());
                    pl.setBooking(this);
                    pl.setDate(start.minusDays(l.getRelease()));
                    pl.setAmount(Helper.roundEuros(totalValue * l.getPercent() / 100d));
                    pl.setAgent(agency.getFinancialAgent());
                    pl.setCurrency(agency.getCurrency());
                    pl.setAgent(agency.getFinancialAgent());
                    pl.setType(DueDateType.COLLECTION);
                }
            }
        }
    }

    protected void updateCancellationTerms(EntityManager em) {
        cancellationTerms.clear();
        if (agency.getCancellationRules() != null) {
            for (CancellationRule r : agency.getCancellationRules().getRules()) {

                if ((r.getStart() == null || !r.getStart().isAfter(end))
                        && (r.getEnd() == null || !r.getEnd().isBefore(start))) {
                    CancellationTerm t;
                    cancellationTerms.add(t = new CancellationTerm());
                    t.setBooking(this);

                    t.setDate(start.minusDays(r.getRelease()));

                    //todo: completar cálculos
                    //todo: buscar en contrato y en compra
                    double v = r.getPercent() * totalValue / 100d;
                    v += r.getAmount();
                    long de = DAYS.between(start, end) - 1;
                    long noches = r.getFirstNights() < de?r.getFirstNights():de;
                    v += noches * totalValue / de;
                    t.setAmount(Helper.roundEuros(v));
                }

            }
        }
    }

    public abstract void priceServices(EntityManager em, List<PriceBreakdownItem> breakdown) throws Throwable;


    public void updateCharges(EntityManager em, List<PriceBreakdownItem> breakdown) throws Throwable {
        getServiceCharges().clear();

        for (PriceBreakdownItem i : breakdown) {

            BookingCharge c;
            getServiceCharges().add(c = new BookingCharge());
            c.setAudit(new Audit(MDD.getCurrentUser()));
            c.setTotal(i.getValue());
            c.setCurrency(getCurrency());

            c.setText(i.getText());

            c.setAgency(getAgency());

            c.setType(ChargeType.SALE);
            c.setBooking(this);

            c.setInvoice(null);

            c.setBillingConcept(i.getConcept());

        }

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

                            if (o instanceof Booking) {
                                if (!((Booking)o).isActive()) s = (s != null)?s + " cancelled":"cancelled";
                            } else {
                                if (!((Boolean)((Object[])o)[9])) {
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


    //@Action(order = 10)
    public static void updateAll() throws Throwable {
        Helper.transact(em -> {

            ((List<HotelBooking>)em.createQuery("select x from " + HotelBooking.class.getName() + " x").getResultList()).forEach(b -> {
                b.getLines().forEach(l -> {
                    try {
                        l.check();
                        l.price();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
                b.updateData();
            });

        });
    }


    @PostLoad
    public void postload() {
        //setChangesControlSignature(createChangeControlSignature());
    }

    public void createDueDates(EntityManager em) {

        getDueDates().clear();

        if (getAgency() != null && getAgency().getFinancialAgent() != null && getAgency().getFinancialAgent().getCustomerPaymentTerms() != null) {

            if (RiskType.PREPAYMENT.equals(getAgency().getFinancialAgent().getRiskType())) {
                getAgency().getFinancialAgent().getCustomerPaymentTerms().getLines().forEach(l -> {
                    if (!PaymentReferenceDate.INVOICE.equals(l.getReferenceDate())) { // solo tiene sentido si la fecha de referencia no es la fecha de factura, y si el cliente no es prepago / directo
                        BookingDueDate dd;
                        getDueDates().add(dd = new BookingDueDate());
                        dd.setBooking(this);
                        dd.setType(DueDateType.COLLECTION);
                        dd.setAgent(getAgency().getFinancialAgent());
                        dd.setCurrency(getAgency().getCurrency());
                        dd.setAgent(getAgency().getFinancialAgent());
                        LocalDate d = LocalDate.now();
                        if (PaymentReferenceDate.CONFIRMATION.equals(l.getReferenceDate())) d = LocalDate.now();
                        else if (PaymentReferenceDate.ARRIVAL.equals(l.getReferenceDate())) d = getStart();
                        else if (PaymentReferenceDate.DEPARTURE.equals(l.getReferenceDate())) d = getEnd();
                        dd.setDate(d.plusDays(l.getRelease()));
                        dd.setAmount(Helper.roundEuros(l.getPercent() * getTotalNetValue() / 100d));
                    }
                });
            }

        }


    }


    public String createSignature() {
        String s = "error when serializing";
        try {
            Map<String, Object> m = new LinkedHashMap<>();
            if (getLeadName() != null) m.put("leadName", getLeadName());
            if (getStart() != null) m.put("start", getStart().toString());
            if (getEnd() != null) m.put("end", getEnd().toString());
            if (getSpecialRequests() != null) m.put("specialRequests", getSpecialRequests());
            m.put("active", isActive());
            m.put("confirmed", isConfirmed());
            
            completeSignature(m);
            
            s = Helper.toJson(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public String createPriceSignature() {
        String s = "error when serializing";
        try {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("priceOverrided", isValueOverrided());
            if (isValueOverrided()) {
                m.put("overridedValue", getOverridedValue());
            }
            m.put("costOverrided", isCostOverrided());
            if (isCostOverrided()) {
                m.put("overridedCost", getOverridedCost());
            }

            if (!isValueOverrided() || !isCostOverrided()) {
                if (getStart() != null) m.put("start", getStart().toString());
                if (getEnd() != null) m.put("end", getEnd().toString());
                completeSignature(m);
            }

            s = Helper.toJson(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public String createDocSignature() {
        String s = "error when serializing";
        try {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("totalPaid", getTotalPaid());
            m.put("paid", isPaid());
            m.put("confirmed", isConfirmed());
            s = Helper.toJson(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s + createSignature();
    }

    protected abstract void completeSignature(Map<String,Object> m);
}
