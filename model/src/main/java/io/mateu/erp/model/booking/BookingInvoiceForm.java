package io.mateu.erp.model.booking;

import com.google.common.base.Strings;
import com.vaadin.icons.VaadinIcons;
import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.invoicing.Charge;
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
import javax.xml.transform.stream.StreamSource;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter@Setter
public class BookingInvoiceForm {

    @Ignored
    private final java.io.File temp;

    @Output
    private Booking booking;

    private String email;

    @TextArea
    private String postscript;

    @IFrame
    @FullWidth
    private URL proforma;


    public BookingInvoiceForm(Booking booking) throws Throwable {
        this.booking = booking;

        if (booking.getAgency() == null) throw new Error("Agency must have a financial agent. Please set financial agent for " + booking.getAgency().getName());
        if (booking.getAgency().getCompany() == null) throw new Error("Agency must have a company. Please set company for " + booking.getAgency().getName());
        if (booking.getAgency().getCompany().getFinancialAgent() == null) throw new Error("Company must have a financial agent. Please set financial agent for " + booking.getAgency().getCompany().getName());

        Document xml = new Document(new Element("invoices"));

        List<Charge> charges = new ArrayList<>();
        charges.addAll(booking.getCharges());


        Helper.notransact(em -> {
            User u = em.find(User.class, MDD.getUserData().getLogin());
            xml.getRootElement().addContent(new IssuedInvoice(u, charges, true, booking.getAgency().getCompany().getFinancialAgent(), booking.getAgency().getFinancialAgent(), null).toXml(em));
        });

        System.out.println(Helper.toString(xml.getRootElement()));


        String archivo = UUID.randomUUID().toString();

        temp = (System.getProperty("tmpdir") == null)? java.io.File.createTempFile(archivo, ".pdf"):new java.io.File(new java.io.File(System.getProperty("tmpdir")), archivo + ".pdf");


        System.out.println("java.io.tmpdir=" + System.getProperty("java.io.tmpdir"));
        System.out.println("Temp file : " + temp.getAbsolutePath());

        FileOutputStream fileOut = new FileOutputStream(temp);
        //String sxslfo = Resources.toString(Resources.getResource(Contract.class, xslfo), Charsets.UTF_8);
        String sxml = new XMLOutputter(Format.getPrettyFormat()).outputString(xml);
        System.out.println("xml=" + sxml);
        Helper.transact(em -> fileOut.write(Helper.fop(new StreamSource(new StringReader(AppConfig.get(em).getXslfoForIssuedInvoice())), new StreamSource(new StringReader(sxml)))));
        fileOut.close();


        String baseUrl = System.getProperty("tmpurl");
        if (baseUrl == null) {
            proforma = temp.toURI().toURL();
        }
        proforma = new URL(baseUrl + "/" + temp.getName());

    }


    @Action(icon = VaadinIcons.ENVELOPE, order = 1)
    public void sendProforma(EntityManager em) throws Throwable {

        if (booking.getAgency() == null) throw new Error("Agency must have a financial agent. Please set financial agent for " + booking.getAgency().getName());
        if (booking.getAgency().getCompany() == null) throw new Error("Agency must have a company. Please set company for " + booking.getAgency().getName());
        if (booking.getAgency().getCompany().getFinancialAgent() == null) throw new Error("Company must have a financial agent. Please set financial agent for " + booking.getAgency().getCompany().getName());

        String to = email;
        //todo: una factura por agencia
        /*
        if (Strings.isNullOrEmpty(to)) {
            to = file.getEmail();
        }
        if (Strings.isNullOrEmpty(to)) {
            to = file.getAgency().getEmail();
        }
        */
        if (Strings.isNullOrEmpty(to)) throw new Exception("No valid email address.");



        AppConfig appconfig = AppConfig.get(em);


        if (EmailHelper.isTesting()) {

            System.out.println("************************************");
            System.out.println("Mail not sent as we are TESTING");
            System.out.println("************************************");


        } else {

// Create the email message
            HtmlEmail email = new HtmlEmail();
            //Email email = new HtmlEmail();
            email.setHostName(appconfig.getAdminEmailSmtpHost());
            email.setSmtpPort(appconfig.getAdminEmailSmtpPort());
            email.setAuthenticator(new DefaultAuthenticator(appconfig.getAdminEmailUser(), appconfig.getAdminEmailPassword()));
            //email.setSSLOnConnect(true);
            email.setFrom(appconfig.getAdminEmailFrom());
            if (!Strings.isNullOrEmpty(appconfig.getAdminEmailCC())) email.getCcAddresses().add(new InternetAddress(appconfig.getAdminEmailCC()));

            email.setSubject("Booking " + booking.getId() + " proforma");


            String msg = postscript;

            String freemark = appconfig.getVouchersEmailTemplate();

            if (!Strings.isNullOrEmpty(freemark)) {
                Map<String, Object> data = Helper.getGeneralData();
                data.put("postscript", postscript);
                data.put("leadname", booking.getLeadName());
                msg = Helper.freemark(freemark, data);
            }

            email.setMsg(msg);

            email.addTo((!Strings.isNullOrEmpty(System.getProperty("allemailsto")))?System.getProperty("allemailsto"):to);

            java.io.File attachment = temp;
            if (attachment != null) email.attach(attachment);

            email.send();

        }


    }

    @Action(icon = VaadinIcons.INVOICE, order = 2)
    public void createInvoice() throws Throwable {

        if (booking.getAgency() == null) throw new Error("Agency must have a financial agent. Please set financial agent for " + booking.getAgency().getName());
        if (booking.getAgency().getCompany() == null) throw new Error("Agency must have a company. Please set company for " + booking.getAgency().getName());
        if (booking.getAgency().getCompany().getFinancialAgent() == null) throw new Error("Company must have a financial agent. Please set financial agent for " + booking.getAgency().getCompany().getName());


        Helper.transact(em -> {

            List<Charge> charges = new ArrayList<>();
            charges.addAll(booking.getCharges());

            Invoice i = new IssuedInvoice(em.find(User.class, MDD.getUserData().getLogin()), charges, false, booking.getAgency().getCompany().getFinancialAgent(), booking.getAgency().getFinancialAgent(), null);
            em.persist(i);

            charges.forEach(c -> em.merge(c));

        });

        MDDUI.get().getNavegador().goBack();
    }

}
