package io.mateu.erp.model.workflow;

import com.google.common.base.Strings;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.organization.Office;
import io.mateu.mdd.core.annotations.Output;
import io.mateu.mdd.core.model.authentication.User;
import io.mateu.mdd.core.model.common.Resource;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;

import javax.mail.internet.InternetAddress;
import javax.persistence.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 28/4/17.
 */
@Entity
@Getter
@Setter
public class SendEmailTask extends AbstractTask {

    @Column(name = "_to")
    @Output
    private String to;
    @Output
    private String cc;
    @Column(name = "_from")
    @Output
    private String from;
    @Output
    private String subject;
    @Output
    private String message;

    @ManyToOne
    @Output
    private Office office;

    @OneToMany(cascade = CascadeType.ALL)
    @Output
    private List<Resource> attachments = new ArrayList<>();

    @Override
    public void run(EntityManager em, User user) throws Throwable {

        AppConfig appconfig = AppConfig.get(em);

        HtmlEmail email = new HtmlEmail();

        boolean utilizarSmtpOficina = getOffice() != null && !Strings.isNullOrEmpty(getOffice().getEmailHost());

        email.setHostName(utilizarSmtpOficina?getOffice().getEmailHost():appconfig.getAdminEmailSmtpHost());
        email.setSmtpPort(utilizarSmtpOficina?getOffice().getEmailPort():appconfig.getAdminEmailSmtpPort());
        email.setAuthenticator(new DefaultAuthenticator(utilizarSmtpOficina?getOffice().getEmailUsuario():appconfig.getAdminEmailUser(), utilizarSmtpOficina?getOffice().getEmailPassword():appconfig.getAdminEmailPassword()));
        //email.setSSLOnConnect(true);
        email.setFrom(utilizarSmtpOficina?getOffice().getEmailFrom():appconfig.getAdminEmailFrom());

        email.setSubject(getSubject());

        String msg = getMessage();

        if (msg.contains("mylogosrc") && appconfig.getLogo() != null) {
            URL url = new URL(appconfig.getLogo().toFileLocator().getUrl());
            String cid = email.embed(url, "" + appconfig.getBusinessName() + " logo");
            msg = msg.replaceAll("mylogosrc", "cid:" + cid);
        }

        email.setHtmlMsg(msg);

        if (!Strings.isNullOrEmpty(System.getProperty("allemailsto"))) {
            email.addTo(System.getProperty("allemailsto"));
        } else {

            if (!Strings.isNullOrEmpty(utilizarSmtpOficina?getOffice().getEmailCC():appconfig.getAdminEmailCC())) {
                for (String s : (utilizarSmtpOficina?getOffice().getEmailCC():appconfig.getAdminEmailCC()).split("[;, ]")) {
                    if (!Strings.isNullOrEmpty(s)) email.getCcAddresses().add(new InternetAddress(s));
                }
            }

            for (String s : getTo().split("[;, ]")) {
                if (!Strings.isNullOrEmpty(s)) email.addTo(s);
            }
        }

        attachments.forEach(r -> {
            try {
                email.attach(new URL(r.toFileLocator().getUrl()), r.getName(), "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


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
