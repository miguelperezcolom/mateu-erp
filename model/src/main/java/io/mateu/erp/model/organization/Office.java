package io.mateu.erp.model.organization;

import com.google.common.base.Strings;
import com.vaadin.ui.Button;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.world.Resort;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.NotInList;
import io.mateu.mdd.core.annotations.Section;
import io.mateu.mdd.core.model.util.EmailHelper;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;

import javax.mail.internet.InternetAddress;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * holder for offices (e.g. Central, Ibiza, Tokio)
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Getter@Setter
public class Office {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Section("Info")
    @NotNull
    private String name;

    @NotNull
    @ManyToOne
    private Company company;

    @NotNull
    @ManyToOne
    private Currency currency;


    @NotNull
    @ManyToOne
    @NotInList
    private Resort resort;


    private String email;

    private String telephone;

    private String fax;

    private String address;

    private String pickupConfirmationTelephone;


    /*
    @NotNull
    @ManyToOne
    @NotInList
    @DataProvider(dataProvider = AirportDataProvider.class)
    private TransferPoint defaultAirportForTransfers;
    */

    @Section("Email")
    @NotInList
    private String emailHost;
    @NotInList
    private int emailPort;
    @NotInList
    private boolean emailStartTLS;
    @NotInList
    private boolean emailSSLOnConnect;
    @NotInList
    private String emailUsuario;
    @NotInList
    private String emailPassword;
    @NotInList
    private String emailFrom;
    @NotInList
    private String emailCC;


    @Transient
    private Button check = new Button("Test", e -> {

        try {

            HtmlEmail email = new HtmlEmail();
            email.setHostName(emailHost);
            email.setSmtpPort(emailPort);
            email.setAuthenticator(new DefaultAuthenticator(emailUsuario, emailPassword));
            email.setSSLOnConnect(emailSSLOnConnect);
            email.setStartTLSEnabled(emailStartTLS);
            email.setFrom(emailFrom);
            if (!Strings.isNullOrEmpty(emailCC)) email.getCcAddresses().add(new InternetAddress(emailCC));

            email.setSubject("Test email");
            email.setHtmlMsg("This is a test email");
            email.addTo((!Strings.isNullOrEmpty(System.getProperty("allemailsto")))?System.getProperty("allemailsto"):"miguelperezcolom@gmail.com");

            EmailHelper.send(email);


            MDD.info("Email sent OK");

        } catch (Exception ex) {
            MDD.alert(ex);
        }

    });


    @Override
    public String toString() {
        return getName();
    }
}
