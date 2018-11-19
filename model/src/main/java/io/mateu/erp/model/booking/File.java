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
import javax.validation.constraints.NotEmpty;
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
    @SearchFilter(field="created")
    @SearchFilter(field="modified")
    private Audit audit;

    @ListColumn
    @SearchFilter
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

    @ListColumn(width = 60)
    @ColumnWidth(80)
    @SearchFilter
    @KPI
    private boolean active = true;


    @Transient
    @Ignored
    private transient boolean alreadyCancelled = false;



    @Section("Links")
    @OneToMany(mappedBy = "file")
    @OrderColumn(name = "orderInBooking")
    @UseLinkToListView
    private List<QuotationRequest> quotationRequests = new ArrayList<>();


    @OneToMany(mappedBy = "file")
    @OrderColumn(name = "orderInBooking")
    @UseLinkToListView
    private List<Booking> bookings = new ArrayList<>();






    @Override
    public String toString() {
        return "" + getId() + " - " + getLeadName() + " " + ((getComments() != null)?getComments():"");
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

            email.send();


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

}
