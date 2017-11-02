package io.mateu.erp.model.workflow;

import com.google.common.base.Strings;
import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.organization.Office;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;

import javax.mail.internet.InternetAddress;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import java.net.URL;

/**
 * Created by miguel on 28/4/17.
 */
@Entity
@Getter
@Setter
public class SendEmailTask extends AbstractTask {

    @Column(name = "_to")
    private String to;
    private String cc;
    @Column(name = "_from")
    private String from;
    private String subject;
    private String message;

    @ManyToOne
    private Office office;

    @Override
    public void run(EntityManager em, User user) throws Throwable {

        AppConfig appconfig = AppConfig.get(em);

        HtmlEmail email = new HtmlEmail();
        email.setHostName((getOffice() != null)?getOffice().getEmailHost():appconfig.getAdminEmailSmtpHost());
        email.setSmtpPort((getOffice() != null)?getOffice().getEmailPort():appconfig.getAdminEmailSmtpPort());
        email.setAuthenticator(new DefaultAuthenticator((getOffice() != null)?getOffice().getEmailUsuario():appconfig.getAdminEmailUser(), (getOffice() != null)?getOffice().getEmailPassword():appconfig.getAdminEmailPassword()));
        //email.setSSLOnConnect(true);
        email.setFrom((getOffice() != null)?getOffice().getEmailFrom():appconfig.getAdminEmailFrom());
        if (!Strings.isNullOrEmpty((getOffice() != null)?getOffice().getEmailCC():appconfig.getAdminEmailCC())) {
            for (String s : ((getOffice() != null)?getOffice().getEmailCC():appconfig.getAdminEmailCC()).split("[;, ]")) {
                if (!Strings.isNullOrEmpty(s)) email.getCcAddresses().add(new InternetAddress(s));
            }
        }

        email.setSubject(getSubject());

        String msg = getMessage();

        if (msg.contains("mylogosrc") && appconfig.getLogo() != null) {
            URL url = new URL(appconfig.getLogo().toFileLocator().getUrl());
            String cid = email.embed(url, "" + appconfig.getBusinessName() + " logo");
            msg = msg.replaceAll("mylogosrc", "cid:" + cid);
        }

        email.setMsg(msg);

        for (String s : getTo().split("[;, ]")) {
            if (!Strings.isNullOrEmpty(s)) email.addTo(s);
        }


        email.send();

    }


    public static void main(String... args) {

        for (String[] x : new String[][] {
                {"inboxtest@viajesibiza.es", "Y4t3n3m0sXML"}
                //, {"inbox@viajesibiza.es", "Y4t3n3m0sXML"}
                //, {"reservas@viajesibiza.es", "Y4t3n3m0sXML"}
        }) {

            try {

                Email email = new HtmlEmail();
                email.setHostName("mail.invisahoteles.com");
                email.setSmtpPort(25);
                email.setAuthenticator(new DefaultAuthenticator(x[0], x[1]));
                //email.setSSLOnConnect(true);
                email.setFrom(x[0]);

                email.setSubject("test");
                email.setMsg("test");
                email.addTo("miguelperezcolom@gmail.com");
                email.send();

                System.out.println("" + x[0] + "/" + x[1] + " ok.");

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        for (String s : "miguel@x.z;wdeheu@wiw.aa;; jdeidje@udud.aa dheh@ss.ee".split("[;, ]")) {
            System.out.println("==>" + s + ".");
        }


    }
}
