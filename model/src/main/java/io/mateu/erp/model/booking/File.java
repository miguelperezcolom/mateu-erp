package io.mateu.erp.model.booking;

import com.google.common.base.Strings;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Grid;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.themes.ValoTheme;
import io.mateu.erp.model.authentication.ERPUser;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.invoicing.BookingCharge;
import io.mateu.erp.model.invoicing.IssuedInvoice;
import io.mateu.erp.model.partners.Agency;
import io.mateu.erp.model.payments.*;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.interfaces.GridDecorator;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.User;
import io.mateu.mdd.core.model.config.Template;
import io.mateu.mdd.core.model.util.EmailHelper;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.workflow.WorkflowEngine;
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
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.xml.transform.stream.StreamSource;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    @SearchFilter(field="created")
    @SearchFilter(field="modified")
    private Audit audit;


    @ManyToOne@NotNull
    private Agency agency;

    @ListColumn
    @SearchFilter
    @NotEmpty
    private String leadName;



    @Ignored
    @SearchFilter
    @ListColumn
    @ColumnWidth(132)
    private LocalDate start;

    @Ignored
    @SameLine
    private LocalDate finish;

    @TextArea
    private String comments;


    @KPI
    @ListColumn
    @Money
    private double totalValue;

    @KPI
    @ListColumn
    @Money
    private double totalNetValue;

    @KPI
    @SameLine
    @Money
    private double totalCost;

    @KPI
    @SameLine
    @Money
    private double balance;

    @KPI
    @ManyToOne
    private Currency currency;

    @ListColumn
    @ColumnWidth(80)
    @SearchFilter
    @KPI
    private boolean active = true;


    @Transient
    @Ignored
    private transient boolean alreadyCancelled = false;



    @Section("Links")
    @ManyToOne@Output
    private QuotationRequest quotationRequest;


    @OneToMany(mappedBy = "file")
    @OrderColumn(name = "orderInBooking")
    @UseLinkToListView
    private List<Booking> bookings = new ArrayList<>();


    @OneToMany(mappedBy = "file")
    @OrderColumn(name = "id")
    @UseLinkToListView(addEnabled = true, deleteEnabled = true)
    private List<FilePaymentAllocation> payments = new ArrayList<>();


    @Ignored
    private LocalDateTime updateRqTime = null;


    @Override
    public String toString() {
        return "" + getId() + " - " + getLeadName() + " " + ((getComments() != null)?getComments():"");
    }


    @PostLoad
    public void beforeSet() throws Throwable {
        setAlreadyCancelled(!isActive());
    }

    @Action(order = 0, icon = VaadinIcons.MAP_MARKER)
    public BookingMap map() {
        return new BookingMap(this);
    }


    @Action(order = 6, confirmationMessage = "Are you sure you want to cancel this file?", style = ValoTheme.BUTTON_DANGER, icon = VaadinIcons.CLOSE)
    @NotWhenCreating
    public void cancel(EntityManager em) {
        for (Booking s : getBookings()) {
            s.cancel(em);
        }
    }

    @Action(icon = VaadinIcons.DOLLAR)
    public void changeCurrency(EntityManager em) {
        //todo: cambiar moneda
    }





    @Action(order = 1, icon = VaadinIcons.ENVELOPES)
    @NotWhenCreating
    public void sendVouchers(@NotEmpty String changeEmail, String postscript) throws Throwable {


        Helper.transact(em ->{

            long t0 = new Date().getTime();



            String to = changeEmail;
            //todo: enviar un email por agencia
            /*
            if (Strings.isNullOrEmpty(to)) {
                to = getboogetAgency().getEmail();
            }
            */
            if (Strings.isNullOrEmpty(to)) throw new Exception("No valid email address.");



            Document xml = new Document(new Element("services"));

            for (Booking b : getBookings()) for (Service s : b.getServices()) {
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

                EmailHelper.send(email);



        });


    }

    @Action(order = 2, icon = VaadinIcons.ENVELOPE)
    @NotWhenCreating
    public void sendEmail(@Help("If blank the postscript will be sent as the email body") Template template, @NotEmpty String changeEmail, @Help("If blank, the subject from the templaet will be used") String subject, @TextArea String postscript) throws Throwable {

        String to = changeEmail;
        //todo: enviar cada voucher a cada agencia
        /*
        if (Strings.isNullOrEmpty(to)) {
            to = getEmail();
        }
        if (Strings.isNullOrEmpty(to)) {
            to = getAgency().getEmail();
        }
        */
        if (Strings.isNullOrEmpty(to)) throw new Exception("No valid email address.");

        if (template != null) {
            Map<String, Object> data = Helper.getGeneralData();
            data.put("postscript", postscript);
            EmailHelper.sendEmail(to, !Strings.isNullOrEmpty(subject) ? subject : template.getSubject(), Helper.freemark(template.getFreemarker(), data), false);
        } else {
            EmailHelper.sendEmail(to, subject, postscript, false);
        }

    }



    @Action(order = 4, icon = VaadinIcons.INVOICE)
    @NotWhenCreating
    public FileInvoiceForm invoice() throws Throwable {
        return new FileInvoiceForm(this);
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


            FilePaymentAllocation a;
            p.getBreakdown().add(a = new FilePaymentAllocation());
            a.setPayment(p);
            a.setFile(this);
            getPayments().add(a);
            a.setValue(amount);

            em.persist(p);

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
                            if (!((Boolean)((Object[])o)[7])) {
                                s = (s != null)?s + " cancelled":"cancelled";
                            }
                            return s;
                        }
                    });
                });
            }
        };
    }


    public void updateTotals() {

        double total = 0;
        double totalNeto = 0;
        double totalCoste = 0;
        double totalPagado = 0;

        if (getQuotationRequest() != null) {
            for (QuotationRequestPaymentAllocation pa : getQuotationRequest().getPayments()) {
                totalPagado += pa.getValue();
            }
        }

        for (Booking b : getBookings()) {
            total += b.getTotalValue();
            totalNeto += b.getTotalNetValue();
            totalCoste += b.getTotalCost();
            for (BookingPaymentAllocation pa : b.getPayments()) {
                totalPagado += pa.getValue();
            }
        }

        for (FilePaymentAllocation pa : getPayments()) {
            totalPagado += pa.getValue();
        }


        setTotalValue(Helper.roundEuros(total));
        setTotalNetValue(Helper.roundEuros(totalNeto));
        setTotalCost(Helper.roundEuros(totalCoste));

        setBalance(Helper.roundEuros(totalPagado - totalNeto));

    }


    @PostPersist@PostUpdate
    public void post() {

        WorkflowEngine.add(() -> {

            try {
                Helper.transact(em -> {

                    File f = em.find(File.class, getId());

                    if (f.getUpdateRqTime() != null) {
                        f.updateTotals();

                        f.setUpdateRqTime(null);
                    }


                });
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

        });

    }

    public java.io.File buildProforma(EntityManager em) throws Throwable {
        String archivo = UUID.randomUUID().toString();

        java.io.File temp = (System.getProperty("tmpdir") == null)? java.io.File.createTempFile(archivo, ".pdf"):new java.io.File(new java.io.File(System.getProperty("tmpdir")), archivo + ".pdf");


        System.out.println("java.io.tmpdir=" + System.getProperty("java.io.tmpdir"));

        buildProforma(em, temp);

        return temp;
    }

    public void buildProforma(EntityManager em, java.io.File temp) throws Throwable {
        System.out.println("Temp file : " + temp.getAbsolutePath());
        Document xml = new Document(new Element("invoices"));

        List<BookingCharge> charges = new ArrayList<>();
        for (Booking b : getBookings()) charges.addAll(b.getCharges().stream().filter(i -> i.getInvoice() == null).collect(Collectors.toList()));

        xml.getRootElement().addContent(new IssuedInvoice(MDD.getCurrentUser(), charges, true, getAgency().getCompany().getFinancialAgent(), getAgency().getFinancialAgent(), null).toXml(em));

        System.out.println(Helper.toString(xml.getRootElement()));


        FileOutputStream fileOut = new FileOutputStream(temp);
        //String sxslfo = Resources.toString(Resources.getResource(Contract.class, xslfo), Charsets.UTF_8);
        String sxml = new XMLOutputter(Format.getPrettyFormat()).outputString(xml);
        System.out.println("xml=" + sxml);
        fileOut.write(Helper.fop(new StreamSource(new StringReader(AppConfig.get(em).getXslfoForIssuedInvoice())), new StreamSource(new StringReader(sxml))));
        fileOut.close();

    }
}
