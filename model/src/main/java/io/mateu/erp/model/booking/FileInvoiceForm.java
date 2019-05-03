package io.mateu.erp.model.booking;

import com.google.common.base.Strings;
import com.vaadin.icons.VaadinIcons;
import io.mateu.erp.model.authentication.ERPUser;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.invoicing.BookingCharge;
import io.mateu.erp.model.invoicing.Invoice;
import io.mateu.erp.model.invoicing.IssuedInvoice;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.model.util.EmailHelper;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.vaadinport.vaadin.MDDUI;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.mail.internet.InternetAddress;
import javax.persistence.EntityManager;
import javax.validation.constraints.NotEmpty;
import javax.xml.transform.stream.StreamSource;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter@Setter
public class FileInvoiceForm {

    @Ignored
    private java.io.File temp;

    @Ignored
    private File file;

    @IFrame@FullWidth
    private URL proforma;


    public FileInvoiceForm(File file) throws Throwable {
        this.file = file;

        Helper.notransact(em -> {

            java.io.File temp = file.buildProforma(em);


            String baseUrl = System.getProperty("tmpurl");
            if (baseUrl == null) {
                proforma = temp.toURI().toURL();
            }
            proforma = new URL(baseUrl + "/" + temp.getName());

        });


    }


    @Action(icon = VaadinIcons.ENVELOPE, order = 1)
    public void sendProforma(EntityManager em, String overrideEmail, @TextArea String postscript) throws Throwable {

        String to = overrideEmail;
        if (Strings.isNullOrEmpty(to)) to = file.getAgency().getEmail();
        if (Strings.isNullOrEmpty(to)) throw new Exception("No valid email address.");


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

            email.setSubject("File " + file.getId() + " proforma");


            String msg = postscript;

            String freemark = appconfig.getVouchersEmailTemplate();

            if (!Strings.isNullOrEmpty(freemark)) {
                Map<String, Object> data = Helper.getGeneralData();
                data.put("postscript", postscript);
                data.put("leadname", file.getLeadName());
                msg = Helper.freemark(freemark, data);
            }

            email.setMsg(msg);

            email.addTo((!Strings.isNullOrEmpty(System.getProperty("allemailsto")))?System.getProperty("allemailsto"):to);

            java.io.File attachment = temp;
            if (attachment != null) email.attach(attachment);

        EmailHelper.send(email);


    }

    @Action(icon = VaadinIcons.INVOICE, order = 2)
    public List<IssuedInvoice> createInvoice() throws Throwable {

        List<IssuedInvoice> l = new ArrayList<>();

        Helper.transact(em -> {

            List<BookingCharge> charges = new ArrayList<>();
            for (Booking b : file.getBookings()) charges.addAll(b.getCharges().stream().filter(i -> i.getInvoice() == null).collect(Collectors.toList()));

            Booking firstBooking = file.getBookings().size() > 0?file.getBookings().get(0):null;

            if (firstBooking != null) {

                IssuedInvoice i = new IssuedInvoice(MDD.getCurrentUser(), charges, false, firstBooking.getAgency().getCompany().getFinancialAgent(), firstBooking.getAgency().getFinancialAgent(), null);
                ((IssuedInvoice) i).setAgency(file.getAgency());
                i.setNumber(((IssuedInvoice) i).getAgency().getCompany().getBillingSerial().createInvoiceNumber());
                em.persist(i);

                charges.forEach(c -> em.merge(c));

                l.add(i);

            }


        });

        return l;
    }


    @Override
    public String toString() {
        return "Proforma for file " + file;
    }
}
