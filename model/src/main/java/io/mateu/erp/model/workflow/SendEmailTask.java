package io.mateu.erp.model.workflow;

import com.google.common.base.Strings;
import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.organization.Office;
import io.mateu.ui.mdd.server.util.Helper;
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
import java.time.LocalDateTime;

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

        Email email = new HtmlEmail();
        email.setHostName((getOffice() != null)?getOffice().getEmailHost():appconfig.getAdminEmailSmtpHost());
        email.setSmtpPort((getOffice() != null)?getOffice().getEmailPort():appconfig.getAdminEmailSmtpPort());
        email.setAuthenticator(new DefaultAuthenticator((getOffice() != null)?getOffice().getEmailUsuario():appconfig.getAdminEmailUser(), (getOffice() != null)?getOffice().getEmailPassword():appconfig.getAdminEmailPassword()));
        //email.setSSLOnConnect(true);
        email.setFrom((getOffice() != null)?getOffice().getEmailFrom():appconfig.getAdminEmailFrom());
        if (!Strings.isNullOrEmpty((getOffice() != null)?getOffice().getEmailCC():appconfig.getAdminEmailCC())) email.getCcAddresses().add(new InternetAddress((getOffice() != null)?getOffice().getEmailCC():appconfig.getAdminEmailCC()));

        email.setSubject(getSubject());
        email.setMsg(getMessage());
        email.addTo(getTo());
        email.send();

    }
}
