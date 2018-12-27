package io.mateu.erp.model.booking;

import com.google.common.base.Strings;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Grid;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.themes.ValoTheme;
import io.mateu.erp.model.booking.parts.HotelBooking;
import io.mateu.erp.model.booking.parts.HotelBookingLine;
import io.mateu.erp.model.booking.transfer.IslandbusHelper;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.Amount;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.invoicing.BookingCharge;
import io.mateu.erp.model.invoicing.Charge;
import io.mateu.erp.model.invoicing.ChargeType;
import io.mateu.erp.model.mdd.ValidCellStyleGenerator;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.payments.BookingPaymentAllocation;
import io.mateu.erp.model.product.AbstractContract;
import io.mateu.erp.model.tpv.TPV;
import io.mateu.erp.model.tpv.TPVTransaction;
import io.mateu.erp.model.workflow.*;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.interfaces.GridDecorator;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.User;
import io.mateu.mdd.core.model.common.Resource;
import io.mateu.mdd.core.model.config.Template;
import io.mateu.mdd.core.model.util.EmailHelper;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;
import io.mateu.mdd.core.workflow.WorkflowEngine;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.javamoney.moneta.FastMoney;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.mail.internet.InternetAddress;
import javax.mail.util.ByteArrayDataSource;
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

@Entity
@Getter@Setter
@UseIdToSelect
public abstract class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Order(desc = true, priority = 10)
    @ListColumn
    private long id;


    @Section("Service")
    @Embedded
    private Audit audit;

    @ListColumn(width = 60)
    @ManyToOne
    @QLFilter("x.agency = true")
    @ColumnWidth(100)
    @NoChart
    private File file;

    @ManyToOne
    @NotNull
    @ListColumn
    @ColumnWidth(200)
    @NoChart
    @MainSearchFilter
    @DataProvider(dataProvider =  AgencyDataProvider.class)
    private Partner agency;

    @NotNull
    @MainSearchFilter(exactMatch = true)
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

    @ListColumn(width = 60)
    @NotInEditor
    @HtmlCol
    @ColumnWidth(100)
    private String icons;



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

    @Section("Sale")
    @TextArea
    @SameLine
    private String privateComments;


    @ListColumn(width = 60)
    @ColumnWidth(156)
    @NotInEditor
    private String description;

    @ManyToOne
    @NotNull
    @ListColumn(width = 60)
    @ColumnWidth(156)
    private PointOfSale pos;


    private LocalDateTime formalizationDate;

    private LocalDateTime expiryDate;



    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="value", column=@Column(name="value_value"))
            , @AttributeOverride(name="date", column=@Column(name="value_date"))
            , @AttributeOverride(name="officeChangeRate", column=@Column(name="value_offchangerate"))
            , @AttributeOverride(name="officeValue", column=@Column(name="value_offvalue"))
            , @AttributeOverride(name="nucChangeRate", column=@Column(name="value_nuchangerate"))
            , @AttributeOverride(name="nucValue", column=@Column(name="value_nucvalue"))
    })
    @AssociationOverrides({
            @AssociationOverride(name="currency", joinColumns = @JoinColumn(name = "value_currency"))
    })
    @NotInEditor
    private Amount value;

    @KPI
    @ListColumn
    @Sum
    private double totalValue;

    @KPI
    @ListColumn
    @Sum
    private double totalNetValue;

    @KPI
    @SameLine
    @Sum
    private double totalCost;

    @KPI
    @SameLine
    @Sum
    private double balance;

    @KPI
    @ManyToOne
    private Currency currency;


    private boolean valueOverrided;
    @SameLine
    private FastMoney overridedValue;

    public boolean isOverridedValueVisible() {
        return valueOverrided;
    }

    @ManyToOne
    private BillingConcept overridedBillingConcept;

    public boolean isOverridedBillingConceptVisible() {
        return valueOverrided;
    }

    @Output
    private String priceReport;

    @ListColumn(width = 60)
    @CellStyleGenerator(ValidCellStyleGenerator.class)
    @Output
    @ColumnWidth(120)
    private ValidationStatus validationStatus = ValidationStatus.VALID;

    @Output
    @SameLine
    private String validationMessage;

    @KPI
    private boolean valued;

    @KPI
    private boolean paid;

    private boolean alreadyInvoiced;

    @UseLinkToListView
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    @NotWhenCreating
    private List<Service> services = new ArrayList<>();

    @NotWhenCreating
    @UseLinkToListView(fields = "text,nucs,invoice")
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<BookingCharge> charges = new ArrayList<>();

    @OneToMany(mappedBy = "booking")
    @OrderColumn(name = "id")
    @UseLinkToListView(addEnabled = true, deleteEnabled = true)
    private List<BookingPaymentAllocation> payments = new ArrayList<>();

    @OneToMany(mappedBy = "booking")
    @UseLinkToListView
    private List<TPVTransaction> TPVTransactions = new ArrayList<>();


    @ManyToOne
    @DataProvider(dataProvider =  CommissionAgentDataProvider.class)
    private Partner commissionAgent;

    private boolean nonCommissionable;

    @Embedded
    private BookingInvoiceData invoiceData;

    private String agentName;


    @ManyToMany
    @JoinTable(
            name="booking_task",
            joinColumns=@JoinColumn(name="bookings_ID"),
            inverseJoinColumns=@JoinColumn(name="tasks_ID"))
    @SearchFilter(value="Task Id", field = "id")
    @UseLinkToListView
    private List<AbstractTask> tasks = new ArrayList<>();





    @Ignored
    private transient boolean updatePending = false;
    @Ignored
    private transient boolean updating = false;


    @Ignored
    private transient boolean summarizing = false;



    @Override
    public String toString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return "" + getId() + " - " + getLeadName() + ": " + Helper.capitalize(getClass().getSimpleName().replaceAll("Booking", "")) + " from " + ((start != null)?start.format(dtf):"-") + " to " + ((end != null)?end.format(dtf):"-");
    }

    public Map<String,Object> getData() throws Throwable {
        Map<String, Object> d = Helper.getGeneralData();

        DecimalFormat df = new DecimalFormat("###,###,###,###,##0.00");


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
            allServicesAreValued &= s.isValued();
            //todo: revisar!
            /*
            allPurchasesAreValued &= s.isPurchaseValued();
            if (!Strings.isNullOrEmpty(s.getBooking().getSpecialRequests())) comentarios += s.getBooking().getSpecialRequests();
            if (!Strings.isNullOrEmpty(s.getOperationsComment())) comentarios += s.getOperationsComment();
            totalCost += s.getTotalCost();
            */
        }

        d.put("valued", allServicesAreValued);
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



        d.put("availstatus", confirmed?available?"CONFIRMADA":"ON REQUEST":"PRESUPUESTO");
        d.put("paymentstatus", paid?"PAGADA":"PENDIENTE DE PAGO");


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


        d.put("servicedata", getServiceDataHtml());

        d.put("productdata", getProductDataHtml());

        d.put("totalretail", df.format(getTotalValue()) + " &euro;");
        d.put("totalnet", df.format(getTotalNetValue()) + " &euro;");
        if (agency.getFinancialAgent() != null) d.put("paymentmethod", agency.getFinancialAgent().getPaymentMethod());
        d.put("payments", getPaymentsDataHtml());

        d.put("paymentremarks", getPaymentRemarksHtml());

        d.put("bookingterms", getBookingTermsHtml());

        d.put("cancellationterms", getCancellationTermsHtml());

        return d;
    }

    public String getCancellationTermsHtml() {
        String h = "";

        h +=
                "                <p>- Devolución total del depósito cuando la anulación se realice con más de 20 días de antelación sobre la fecha de entrada.</p>\n" +
                "                <p>- Pérdida del depósito cuando la anulación se realice durante los 20 días anteriores a la fecha de entrada.</p>\n";

        return h;
    }

    public String getBookingTermsHtml() {
        String h = "";

        h +=
                "<p>Usted está reservando directamente con el Hotel.</p>\n" +
                "                <p>- Usted autoriza al Hotel Voramar a realizar un cobro, en concepto de depósito, mediante la tarjeta de crédito facilitada.</p>\n" +
                "                <p>- El pago de la parte pendiente de la reserva se ralizará una vez lleguen al hotel.</p>\n" +
                "                <p>- Esta es una confirmación de rserva para habitación estándard (twin bedroom), si desea un nº de habitación concreto o posición, lamentamos no poder garantizarla, sin embargo haremos lo posible para asignarla si tenemos disponibilidad el día de su entrada.</p>\n" +
                "                <p>- Check-in a partir de las 14:00 horas, chak-out máximo a las 12:00 horas.</p>";

        return h;
    }

    public String getPaymentRemarksHtml() {
        String h = "";

        h =
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

    public String getPaymentsDataHtml() {
        String h = "";

        h +=
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


    public static Booking getByAgencyRef(EntityManager em, String agencyRef, Partner partner)
    {
        try {
            String jpql = "select x from " + File.class.getName() + " x" +
                    " where x.agencyReference='" + agencyRef + "' and x.agency.id= " + partner.getId();
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


    @Action(order = 1, icon = VaadinIcons.ENVELOPES, saveBefore = true, saveAfter = true)
    @NotWhenCreating
    public void sendBooked(String changeEmail, String postscript) throws Throwable {


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

            if (getPos() != null && getPos().getOffice() != null) {
                t.setOffice(getPos().getOffice());
            }
            t.setSubject("Booking " + getId() + "");
            t.setTo(to);
            t.setAudit(new Audit(MDD.getCurrentUser()));
            t.setDescription("Send booked email");
            t.getBookings().add(this);

            String msg = postscript;

            String freemark = appconfig.getBookedEmailTemplate();

            if (!Strings.isNullOrEmpty(freemark)) {
                Map<String, Object> data = getData();
                data.put("postscript", postscript);
                msg = Helper.freemark(freemark, data);
            }

            t.setMessage(msg);

            em.merge(this);

        });


    }

    @Action(order = 2, icon = VaadinIcons.ENVELOPES, saveBefore = true, saveAfter = true)
    @NotWhenCreating
    public void sendVouchers(String changeEmail, String postscript) throws Throwable {


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
                Map<String, Object> data = getData();
                msg = Helper.freemark(freemark, data);
            }

            t.setMessage(msg);


            // creamos vouchers

            Document xml = new Document(new Element("services"));

            if (AppConfig.get(em).getLogo() != null) xml.getRootElement().setAttribute("urllogo", AppConfig.get(em).getLogo().toFileLocator().getTmpPath());

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


            if (temp != null) t.getAttachments().add(new Resource(temp));


            // fin crear vouchers

            em.merge(this);


        });


    }

    @Action(order = 3, icon = VaadinIcons.ENVELOPE, saveBefore = true, saveAfter = true)
    @NotWhenCreating
    public void sendEmail(@Help("If blank the postscript will be sent as the email body") Template template, String changeEmail, @Help("If blank, the subject from the templaet will be used") String subject, @TextArea String postscript) throws Throwable {


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


    @Action(order = 7, confirmationMessage = "Are you sure you want to cancel this booking?", style = ValoTheme.BUTTON_DANGER, icon = VaadinIcons.CLOSE)
    @NotWhenCreating
    public void cancel(EntityManager em) {
        cancel(em, em.find(User.class, MDD.getUserData().getLogin()));
    }

    public void cancel(EntityManager em, User u) {

        services.forEach(s -> s.cancel(em, u));

        setActive(false);

        if (getFile() != null) {
            boolean allCancelled = true;
            for (Booking b : getFile().getBookings()) if (b.isActive()) {
                allCancelled = false;
                break;
            }
            if (allCancelled) {
                getFile().setActive(false);
                em.merge(getFile());
            }
        }

        em.merge(this);
    }

    public boolean isCancelVisible() {
        return isActive();
    }


    @Action(order = 7, confirmationMessage = "Are you sure you want to uncancel this booking?", style = ValoTheme.BUTTON_FRIENDLY, icon = VaadinIcons.CHECK)
    @NotWhenCreating
    public void uncancel(EntityManager em) throws Exception {
        User u = em.find(User.class, MDD.getUserData().getLogin());

        if (services.size() > 0) throw new Exception("Sorry. At this moment You can not uncancel a booking with cancelled services.");

        setActive(false);

        if (getFile() != null) {
            boolean allCancelled = true;
            for (Booking b : getFile().getBookings()) if (b.isActive()) {
                allCancelled = false;
                break;
            }
            if (allCancelled) {
                getFile().setActive(false);
                em.merge(getFile());
            }
        }

        em.merge(this);
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
                to = getEmail();
            }
            if (Strings.isNullOrEmpty(to)) {
                to = getAgency().getEmail();
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

    @Action(order = 5, icon = VaadinIcons.INVOICE)
    @NotWhenCreating
    public BookingInvoiceForm invoice() throws Throwable {
        return new BookingInvoiceForm(this);
    }



    @PrePersist@PreUpdate
    public void pre() throws Error {
        try {
            if (isValueOverrided()) {
                if (overridedBillingConcept == null) throw new Exception("Billing concept is required. Please fill");
            }
            validate();

            setDescription(getParticularDescription());

        } catch (Throwable e) {
            throw new Error(e);
        }
    }


    @PostPersist@PostUpdate
    public void post() {
        if (Helper.getEMFromThreadLocal().getTransaction().isActive()) WorkflowEngine.add(new Runnable() {
            @Override
            public void run() {
                try {
                    Helper.transact(em -> {
                        Booking b = em.merge(Booking.this);
                        if (b.isConfirmed()) {
                            b.generateServices(em);
                        }
                        b.price(em);
                        b.summarize(em);
                        b.getAgency().setUpdatePending(true);
                    });
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
    }

    public void summarize(EntityManager em) {

        if (summarizing) {
            System.out.println("Booking " + getId() + " already summarizing");
        } else {
            summarizing = true;
            System.out.println("booking " + getId() + ".summarize");

            //todo: actualizar estado, balances, etc

            if (true) {
                for (Service x : services) {
                    if (x.getStart() != null) {
                        if (start == null || start.isAfter(x.getStart())) start = x.getStart();
                        if (end == null || end.isBefore(x.getStart())) end = x.getStart();
                    }

                    if (x.getFinish() != null) {
                        if (start == null || start.isAfter(x.getFinish())) start = x.getFinish();
                        if (end == null || end.isBefore(x.getFinish())) end = x.getFinish();
                    }

                    if (agency.isOneLinePerBooking()) x.setServiceDateForInvoicing(end);
                    else x.setServiceDateForInvoicing(x.getStart());
                }
            }
            summarizing = false;
        }

    }


    public String getParticularDescription() {
        return Helper.capitalize(getClass().getSimpleName());
    }


    //@Action(order = 8, icon = VaadinIcons.SPLIT)
    @NotWhenCreating
    public void build(EntityManager em) {
        generateServices(em);
    }

    public abstract void validate() throws Exception;

    public abstract void generateServices(EntityManager em);


    //@Action(order = 7, icon = VaadinIcons.EURO)
    @NotWhenCreating
    public void price(EntityManager em) throws Throwable {

        if (isValueOverrided()) {
            if (overridedBillingConcept == null) throw new Exception("Billing concept is required. Please fill");
            setValue(new Amount(overridedValue));
            setValued(true);
        } else {
            priceServices();
        }

        updateCharges(em);

        updateTotals(em);

        em.merge(this);
    }





    @PostLoad
    public void postload() {
        updating = updatePending;
    }

    public synchronized void askForUpdate() {
        if (!updatePending && !updating) {
            setUpdatePending(true);
            WorkflowEngine.add(() -> {
                try {
                    Helper.transact(em -> {

                        setUpdating(true);

                        price(em);

                        build(em);

                        updateTotals(em);

                        em.merge(this);

                    });
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                setUpdatePending(false);
                setUpdating(false);
            });
        }
    }

    private void updateTotals(EntityManager em) {

        double total = 0;
        double totalNeto = 0;
        double totalCoste = 0;

        for (Charge c : getCharges()) {
            System.out.println("**************************updateTotalsxcharge");
            if (ChargeType.SALE.equals(c.getType())) {
                total += c.getTotal().getValue();
                totalNeto += c.getTotal().getValue();
            } else {
                totalCoste += c.getTotal().getValue();
            }
        }

        setTotalValue(Helper.roundEuros(total));
        setTotalNetValue(Helper.roundEuros(totalNeto));
        setTotalCost(Helper.roundEuros(totalCoste));
        setCurrency(em.find(Currency.class, "EUR"));
    }

    public abstract void priceServices() throws Throwable;


    public void updateCharges(EntityManager em) throws Throwable {
        for (BookingCharge c : new ArrayList<>(getCharges())) {
            c.getBooking().getCharges().remove(c);
            if (c.getId() != 0) em.remove(c);
        }
        getCharges().clear();

        if (isValueOverrided()) {
            BookingCharge c;
            getCharges().add(c = new BookingCharge());
            c.setAudit(new Audit(getAudit().getModifiedBy()));
            c.setTotal(getValue());

            c.setText("Booking " + getId());

            c.setPartner(getAgency());

            c.setType(ChargeType.SALE);
            c.setBooking(this);
            getCharges().add(c);

            c.setInvoice(null);


            c.setBillingConcept(getOverridedBillingConcept());

            em.persist(c);
        } else {
            createCharges(em);
        }

    }

    public void createCharges(EntityManager em) throws Throwable {
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


    @Action(order = 10)
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


}
