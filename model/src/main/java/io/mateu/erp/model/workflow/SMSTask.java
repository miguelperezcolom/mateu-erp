package io.mateu.erp.model.workflow;

import com.google.common.net.UrlEscapers;
import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.ui.mdd.server.util.Helper;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

/**
 * Created by miguel on 5/5/17.
 */
@Entity
@Getter
@Setter
public class SMSTask extends AbstractTask {

    public SMSTask() {

    }

    public SMSTask(long telephoneNumber, String content) {
        this.telephoneNumber = telephoneNumber;
        this.content = content;
    }

    private long telephoneNumber;
    private String content;
    private String answer;

    @Override
    public void run(EntityManager em, User user) throws Throwable {
        /*
apiKey Yes This is the authentication key used to call and unlock the specific integration service. Refer to the Platform portal to retrieve this key for the specific HTTP API integration you want to utilize to deliver your messages. (It will be included in the CURL sample in the code library for that integration).
to Yes The mobile number to which the message must be delivered. The number should be in international format with no + symbol or leading zeros.
text
         */
        //HTTP/S://platform.clickatell.com/messages/http/send?apiKey=xxxxxxxxxxxxxxxx==&to=xxxxxxxxxxx&content=Test+message+text

        // QMgaPs3zToSOdMvT0_gqkQ==


        //https://platform.clickatell.com/messages/http/send?apiKey=QMgaPs3zToSOdMvT0_gqkQ==&to=34629602085&content=Testxx

        setAnswer(Helper.httpGet("https://platform.clickatell.com/messages/http/send?apiKey=" + UrlEscapers.urlFormParameterEscaper().escape(AppConfig.get(em).getClickatellApiKey()) + "&to=" + getTelephoneNumber() + "&content=" + UrlEscapers.urlFormParameterEscaper().escape(getContent())));


    }
}
