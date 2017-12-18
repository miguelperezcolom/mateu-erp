package io.mateu.erp.model.monitoring;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.util.EntityUtils;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class HttpWatchdog extends Watchdog {

    @NotNull
    private String url;

    @NotNull
    private HttpMethod method;

    private String requestBody;

    private int expectedResponseCode = 200;

    private String expectedText;

    @Override
    public void check(EntityManager em) throws Throwable {
        HttpResponse rs = null;
        if (HttpMethod.GET.equals(getMethod())) rs = Request.Get(getUrl()).execute().returnResponse();
        else {
            rs = Request.Post(getUrl()).execute().returnResponse();
        }
        if (rs.getStatusLine().getStatusCode() != 200) throw new Exception("" + getUrl() + " returned status line " + rs.getStatusLine());
        String content = EntityUtils.toString(rs.getEntity());
        if (!Strings.isNullOrEmpty(getExpectedText()) && !content.toLowerCase().contains(getExpectedText().toLowerCase())) throw new Exception("" + getExpectedText().toLowerCase() + " not found in " + content);
        /*
        Request.Post("http://targethost/login")
                .bodyForm(Form.form().add("username",  "vip").add("password",  "secret").build())
                .execute().returnContent();
                */
    }
}
