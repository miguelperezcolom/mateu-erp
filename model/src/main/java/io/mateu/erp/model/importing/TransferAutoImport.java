package io.mateu.erp.model.importing;

import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.partners.Agency;
import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.Output;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Antonia on 26/03/2017.
 */

@Entity
@Getter
@Setter
public abstract class TransferAutoImport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;

    private String url;
    private String login;
    private String password;

    @ManyToOne
    private Agency customer;//cliente de las reservas

    @ManyToOne
    private Office office;

    @ManyToOne
    private PointOfSale pointOfSale;

    @ElementCollection
    @Output
    private List<String> historial = new ArrayList<String>();

    protected void addHistory(String txt)
    {
        if (historial.size()>10)
           historial.remove(0);
        historial.add(txt);
    }

    @Action
    public static void runQueue() throws Throwable {
        TransferImportQueue.run();
    }

    @Action
    public void execute(LocalDate from, int days) {
        getBookings(from, days);
    }

    public abstract void getBookings(LocalDate from, int days);

    protected static String doPost(String toUrl, HashMap<String,String> params) throws Exception {
        //URL url = new URL("http://localhost/post.php");
       // Map<String, Object> params = new LinkedHashMap<>();
       // params.put("parametro", "ProgramaciónExtrema.com");

        URL url = new URL(toUrl);

        // Build the POST data
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, String> param : params.entrySet()) {
            if (postData.length() != 0)
                postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()),"UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        // Setup a URL connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length",
                String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        //conn.setUseCaches(false);
        // conn.setConnectTimeout(Constants.DEFAULT_HTTP_TIMEOUT);

        // Send the request
        //conn.getOutputStream().write(postDataBytes); --> mejor la version cerrando conexion
        OutputStream out = conn.getOutputStream();
        out.write(postDataBytes);
        out.close();

        // Get the response
        String respuesta = "";
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("Error: responseCode=" + responseCode);
        } else {
            // Read conn.getInputStream() here
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            String linea;
            while ((linea = rd.readLine()) != null) {
                respuesta+= linea;
            }
            return respuesta;


        }



    }
}
