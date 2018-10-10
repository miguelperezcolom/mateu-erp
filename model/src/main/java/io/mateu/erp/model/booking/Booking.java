package io.mateu.erp.model.booking;

import com.google.common.base.Strings;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Grid;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.themes.ValoTheme;
import io.mateu.erp.model.booking.transfer.IslandbusHelper;
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
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.interfaces.GridDecorator;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.User;
import io.mateu.mdd.core.model.config.Template;
import io.mateu.mdd.core.model.util.EmailHelper;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;
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
import javax.validation.constraints.NotNull;
import javax.xml.transform.stream.StreamSource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Entity
@Getter@Setter
public abstract class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Order(desc = true, priority = 10)
    private long id;


    @Section("Service")
    @Embedded
    private Audit audit;

    @ListColumn(width = 60)
    @ManyToOne
    @NotNull
    @ColumnWidth(350)
    @NoChart
    private File file;

    @ListColumn(width = 60)
    @NotInEditor
    @HtmlCol
    @ColumnWidth(100)
    private String icons;



    @KPI
    @ListColumn(width = 100)
    @ColumnWidth(102)
    private boolean confirmed = true;

    @NotWhenCreating
    @KPI
    @ListColumn(width = 80)
    private boolean active = true;

    @NotNull
    @ListColumn(width = 60)
    private LocalDate start;
    @Column(name = "_end")
    @SameLine
    @NotNull
    @ListColumn(width = 60)
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


    @OneToMany(mappedBy = "booking")
    @Output
    @NotWhenCreating
    private List<Service> services = new ArrayList<>();


    private boolean directSale;

    @ManyToOne
    @NotNull
    @ListColumn(width = 60)
    @ColumnWidth(156)
    private PointOfSale pos;

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
    private double totalValue;

    @KPI
    @ListColumn
    private double totalNetValue;

    @KPI
    @SameLine
    private double totalCost;

    @KPI
    @SameLine
    private double balance;

    @KPI
    @ManyToOne
    private Currency currency;


    @KPI
    private boolean available;


    private boolean valueOverrided;
    @SameLine
    private FastMoney overridedValue;

    public boolean isOverridedValueVisible() {
        return valueOverrided;
    }

    @SameLine
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

    private boolean alreadyInvoiced;

    @NotWhenCreating
    @UseLinkToListView
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<BookingCharge> charges = new ArrayList<>();


    @ManyToOne
    private Partner commissionAgent;

    private boolean nonCommissionable;



    @Override
    public String toString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return getClass().getSimpleName() + " from " + ((start != null)?start.format(dtf):"-") + " to " + ((end != null)?end.format(dtf):"-");
    }



    @Action(order = 1)
    public static void searchAvailable() {

    }

    @Action(order = 1, icon = VaadinIcons.ENVELOPES)
    @NotWhenCreating
    public void sendVouchers(String changeEmail, String postscript) throws Throwable {


        Helper.transact(em ->{

            long t0 = new Date().getTime();



            String to = changeEmail;
            if (Strings.isNullOrEmpty(to)) {
                to = getFile().getEmail();
            }
            if (Strings.isNullOrEmpty(to)) {
                to = getFile().getAgency().getEmail();
            }
            if (Strings.isNullOrEmpty(to)) throw new Exception("No valid email address. Please check the agency " + getFile().getAgency().getName() + " and fill the email field.");



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

            email.setSubject("Booking " + getId() + " vouchers");


            String msg = postscript;

            String freemark = appconfig.getVouchersEmailTemplate();

            if (!Strings.isNullOrEmpty(freemark)) {
                Map<String, Object> data = Helper.getGeneralData();
                data.put("postscript", postscript);
                data.put("leadname", getFile().getLeadName());
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
            to = getFile().getEmail();
        }
        if (Strings.isNullOrEmpty(to)) {
            to = getFile().getAgency().getEmail();
        }
        if (Strings.isNullOrEmpty(to)) throw new Exception("No valid email address. Please check the agency " + getFile().getAgency().getName() + " and fill the email field.");

        if (template != null) {
            Map<String, Object> data = Helper.getGeneralData();
            data.put("postscript", postscript);
            EmailHelper.sendEmail(to, !Strings.isNullOrEmpty(subject) ? subject : template.getSubject(), Helper.freemark(template.getFreemarker(), data), false);
        } else {
            EmailHelper.sendEmail(to, subject, postscript, false);
        }

    }

    @Action(order = 5, confirmationMessage = "Are you sure you want to confirm this booking?", style = ValoTheme.BUTTON_FRIENDLY, icon = VaadinIcons.CHECK)
    @NotWhenCreating
    public void confirm(EntityManager em) {
        setConfirmed(true);
        em.merge(this);
    }

    public boolean isConfirmVisible() {
        return !isConfirmed();
    }


    @Action(order = 5, confirmationMessage = "Are you sure you want to unconfirm this booking?", style = ValoTheme.BUTTON_DANGER, icon = VaadinIcons.CLOSE)
    @NotWhenCreating
    public void unconfirm(EntityManager em) {
        setConfirmed(false);
        em.merge(this);
    }

    public boolean isUnconfirmVisible() {
        return isConfirmed();
    }


    @Action(order = 6, confirmationMessage = "Are you sure you want to cancel this booking?", style = ValoTheme.BUTTON_DANGER, icon = VaadinIcons.CLOSE)
    @NotWhenCreating
    public void cancel(EntityManager em) {
        cancel(em, em.find(User.class, MDD.getUserData().getLogin()));
    }

    public void cancel(EntityManager em, User u) {

        services.forEach(s -> s.cancel(em, u));

        setActive(false);

        boolean allCancelled = true;
        for (Booking b : getFile().getBookings()) if (b.isActive()) {
            allCancelled = false;
            break;
        }
        if (allCancelled) {
            getFile().setActive(false);
            em.merge(getFile());
        }

        em.merge(this);
    }

    public boolean isCancelVisible() {
        return isActive();
    }


    @Action(order = 6, confirmationMessage = "Are you sure you want to uncancel this booking?", style = ValoTheme.BUTTON_FRIENDLY, icon = VaadinIcons.CHECK)
    @NotWhenCreating
    public void uncancel(EntityManager em) throws Exception {
        User u = em.find(User.class, MDD.getUserData().getLogin());

        if (services.size() > 0) throw new Exception("Sorry. At this moment You can not uncancel a booking with cancelled services.");

        setActive(false);

        boolean allCancelled = true;
        for (Booking b : getFile().getBookings()) if (b.isActive()) {
            allCancelled = false;
            break;
        }
        if (allCancelled) {
            getFile().setActive(false);
            em.merge(getFile());
        }

        em.merge(this);
    }

    public boolean isUncancelVisible() {
        return !isActive();
    }



    @PrePersist@PreUpdate
    public void pre() throws Throwable {
        if (isValueOverrided()) {
            if (overridedBillingConcept == null) throw new Exception("Billing concept is required. Please fill");
        }
        validate();
        if (isConfirmed()) {
            generateServices(Helper.getEMFromThreadLocal());
        }
        price(Helper.getEMFromThreadLocal());
    }




    @Action(order = 8, icon = VaadinIcons.SPLIT)
    @NotWhenCreating
    public void build(EntityManager em) {
        generateServices(em);
    }

    public abstract void validate() throws Exception;

    protected abstract void generateServices(EntityManager em);


    @Action(order = 7, icon = VaadinIcons.EURO)
    @NotWhenCreating
    public void price(EntityManager em) throws Throwable {

        if (isValueOverrided()) {
            if (overridedBillingConcept == null) throw new Exception("Billing concept is required. Please fill");
            setValue(new Amount(overridedValue));
            setValued(true);
            updateCharges(em);
        } else {
            priceServices();
        }

        em.merge(this);
    }

    public abstract void priceServices() throws Throwable;


    private void updateCharges(EntityManager em) {
        for (BookingCharge c : getCharges()) {
            c.getFile().getCharges().remove(c);
            em.remove(c);
        }
        getCharges().clear();

        BookingCharge c;
        getCharges().add(c = new BookingCharge());
        c.setAudit(new Audit(em.find(User.class, MDD.getUserData().getLogin())));
        c.setTotal(getValue());

        c.setText("Booking " + getId());

        c.setPartner(getFile().getAgency());

        c.setType(ChargeType.SALE);
        c.setBooking(this);
        c.setFile(getFile());
        getFile().getCharges().add(c);

        c.setInvoice(null);
        c.setService(null);


        c.setBillingConcept(getOverridedBillingConcept());

        em.persist(c);

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
                            if (!((Boolean)((Object[])o)[5])) {
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
