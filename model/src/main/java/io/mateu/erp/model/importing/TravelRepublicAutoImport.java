package io.mateu.erp.model.importing;

//import okhttp3.*;

import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.partners.Partner;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.model.util.Constants;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.JPATransaction;
import io.mateu.mdd.core.workflow.WorkflowEngine;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Transient;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


@Entity
@Getter
@Setter
public class TravelRepublicAutoImport extends TransferAutoImport {

    //OkHttpClient client = new OkHttpClient();

    @Transient
    @Ignored
    BasicCookieStore cookieStore = new BasicCookieStore();

    @Transient
    @Ignored
    CloseableHttpClient httpclient = HttpClients.custom()
            .setDefaultCookieStore(cookieStore)
            .build();


    public static void main(String... args) {
        System.setProperty("appconf", "/home/miguel/work/quotravel.properties");

        run();

        WorkflowEngine.exit(0);
    }

    public static void run() {
        try {
            TravelRepublicAutoImport i = new TravelRepublicAutoImport();
            i.setCustomer((Partner) Helper.selectObjects("select x from " + Partner.class.getName() + " x where x.name = 'TRAVELREPUBLIC'").get(0));
            i.setPointOfSale((PointOfSale) Helper.selectObjects("select x from " + PointOfSale.class.getName() + " x where x.name = 'Importación'").get(0));
            i.getBookings(LocalDate.now(), 300);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }


    @Override
    public void getBookings(LocalDate from, int days) {

        try {
            Helper.transact(new JPATransaction() {
                @Override
                public void run(EntityManager em) throws Throwable {

                    //ir a la web, hacer login y recuperar fichero
                    String csv = "";
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("d-MMM-yy");
                    DateTimeFormatter dfh = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    String fdesde = from.format(df);
                    String fhasta = from.plusDays(days).format(df);
                    try {
                        csv = recuperarCsv(from, from.plusDays(days));
                    } catch (Exception e)
                    {
                        addHistory(LocalDateTime.now().format(dfh) + " - Error: " + e.getMessage() + " \n " + e.getStackTrace());
                        e.printStackTrace();
                        return; //Salimos porque sin el fichero no podemos hacer nada
                    }

                    //crear nueva TravelRepublicImportTask
                    if (csv!=null && csv.length()>0)
                    {
                        User u = em.find(User.class, Constants.IMPORTING_USER_LOGIN);
                        TravelRepublicImportTask t = new TravelRepublicImportTask(getName()+ " (" + fdesde + "-" + fhasta+")",u, getCustomer(),csv, getOffice(), getPointOfSale(), em.find(BillingConcept.class, "TRA"));
                        em.persist(t);
                        addHistory(LocalDateTime.now().format(dfh)+ " - Tarea creada");
                    }
                    else {
                        System.out.println("Error: el xml esta vacio!");
                        addHistory(LocalDateTime.now().format(dfh) + " - Error: el xml esta vacio!");
                        return; //Salimos porque sin el fichero no podemos hacer nada
                    }


                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    private String recuperarCsv(LocalDate fdesde, LocalDate fhasta) throws Exception {

        String h = get("https://transfers.travelrepublic.co.uk");

        System.out.println(h);

        System.out.println("*******************************************************************************");

            /*
            <form name="aspnetForm" method="post" action="logon.aspx" id="aspnetForm" style="margin: 0px">
            <input name="ctl00$cphMainContent$txtCompanyId" type="text" id="ctl00_cphMainContent_txtCompanyId" style="width: 260px" />
            <input name="ctl00$cphMainContent$txtPassword" type="password" id="ctl00_cphMainContent_txtPassword" style="width: 260px" />
            <a id="ctl00_cphMainContent_btnSubmit" class="orangeButton boxShadow floatRight" href="javascript:__doPostBack(&#39;ctl00$cphMainContent$btnSubmit&#39;,&#39;&#39;)">Logon</a>
             */


        {
            Document doc = Jsoup.parse(h);
            Element link = doc.select("form").first();
            Map<String, String> m = new HashMap<>();
            link.select("input").forEach((e) -> {
                m.put(e.attr("name"), e.attr("value"));
            });


            m.put("ctl00$cphMainContent$txtCompanyId", "reservas@viajesibiza.com");
            m.put("ctl00$cphMainContent$txtPassword", "viajesibiza");
            m.put("__EVENTTARGET", "ctl00$cphMainContent$btnSubmit");
            m.put("__LASTFOCUS", "");
            m.put("__EVENTARGUMENT", "");

            h = post("https://transfers.travelrepublic.co.uk/logon.aspx", m);

            System.out.println(h);

        }


        h = get("https://transfers.travelrepublic.co.uk/Manifest.aspx");

        {
            Document doc = Jsoup.parse(h);
            Element link = doc.select("form").first();
            Map<String, String> m = new HashMap<>();
            link.select("input").forEach((e) -> {
                m.put(e.attr("name"), e.attr("value"));
            });


            m.put("ctl00$cphMainContent$txtDepartureStartDate", fdesde.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.UK))); //"01-jan-2018");
            m.put("ctl00$cphMainContent$txtDepartureEndDate", fhasta.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.UK))); //"01-may-2018");
            m.put("ctl00$cphMainContent$chkIncludeCancelled", "on");
            m.put("__EVENTTARGET", "ctl00$cphMainContent$btnView");
            m.put("__LASTFOCUS", "");
            m.put("__EVENTARGUMENT", "");

            h = post("https://transfers.travelrepublic.co.uk/Manifest.aspx", m);

            System.out.println(h);
        }


        String csv = null;

        {
            Document doc = Jsoup.parse(h);
            Element link = doc.select("form").first();
            Map<String, String> m = new HashMap<>();
            link.select("input").forEach((e) -> {
                m.put(e.attr("name"), e.attr("value"));
            });


            m.put("__EVENTTARGET", "ctl00$cphMainContent$btnDownload");
            m.put("__LASTFOCUS", "");
            m.put("__EVENTARGUMENT", "");

            csv = post("https://transfers.travelrepublic.co.uk/Manifest.aspx", m);

            System.out.println(h);

        }


        return csv;

    }

    private String post(String url, Map<String, String> m) throws IOException, URISyntaxException {


        String h = null;


        RequestBuilder b = RequestBuilder.post()
                .setUri(new URI(url));
        for (String k : m.keySet()) b.addParameter(k, m.get(k));
        b.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        b.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");

        CloseableHttpResponse response2 = httpclient.execute(b.build());

        try {
            HttpEntity entity = response2.getEntity();

            System.out.println("Login form get: " + response2.getStatusLine());
            //EntityUtils.consume(entity);

            h = EntityUtils.toString(response2.getEntity());

            System.out.println("*******************************************************************************");

            System.out.println("Post logon cookies:");
            List<Cookie> cookies = cookieStore.getCookies();
            if (cookies.isEmpty()) {
                System.out.println("None");
            } else {
                for (int i = 0; i < cookies.size(); i++) {
                    System.out.println("- " + cookies.get(i).toString());
                }
            }

            System.out.println("*******************************************************************************");

        } finally {
            response2.close();
        }

        return h;
    }

    private String get(String url) throws IOException, URISyntaxException {
        /*
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
        */

        String h = null;

        RequestBuilder b = RequestBuilder.get()
                .setUri(new URI(url));
        b.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        b.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");

        CloseableHttpResponse response2 = httpclient.execute(b.build());

        try {
            HttpEntity entity = response2.getEntity();

            System.out.println("Login form get: " + response2.getStatusLine());

            h = EntityUtils.toString(response2.getEntity());

            System.out.println("*******************************************************************************");

            System.out.println("Get logon cookies:");
            List<Cookie> cookies = cookieStore.getCookies();
            if (cookies.isEmpty()) {
                System.out.println("None");
            } else {
                for (int i = 0; i < cookies.size(); i++) {
                    System.out.println("- " + cookies.get(i).toString());
                }
            }

            System.out.println("*******************************************************************************");

        } finally {
            response2.close();
        }


        return h;
    }

}
