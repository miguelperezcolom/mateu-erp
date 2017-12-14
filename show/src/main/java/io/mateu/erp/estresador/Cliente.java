package io.mateu.erp.estresador;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Cliente implements Runnable {
    private final String url;
    private final int id;

    public Cliente(String url, int id) {
        this.url = url;
        this.id = id;
    }

    @Override
    public void run() {


        //http://admin.test.quoon.net/resources/eyAiY3JlYXRlZCI6ICJXZWQgTm92IDA4IDEyOjE4OjQ3IENFVCAyMDE3IiwgInVzZXJJZCI6ICJhZG1pbiIsICJhY3RvcklkIjogIjMiLCAiaG90ZWxJZCI6ICIxMiJ9
        // /hotel/available?
        // resorts=1&checkin=20180101&checkout=20180115&occupancies=1x2&incudestaticinfo=false

        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(url); //"http://example.com/rest");
        WebTarget resourceWebTarget = webTarget.path("hotel");
        WebTarget dispoWebTarget = resourceWebTarget.path("available");

        Random random = new Random();

        LocalDate entrada0 = LocalDate.of(2018, 01, 01);

        while (!Estresador.terminar) {

            int noches = 1 + random.nextInt(14);
            int release = 1 + random.nextInt(180);

            LocalDate entrada = entrada0.plusDays(release);
            LocalDate salida = entrada.plusDays(noches);

            DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyyMMdd");

            Invocation.Builder invocationBuilder = dispoWebTarget
                    .queryParam("resorts", "1")
                    .queryParam("checkin", entrada.format(f))
                    .queryParam("checkout", salida.format(f))
                    .queryParam("occupancies", "1x2")
                    .queryParam("incudestaticinfo", "false")
                    .request(MediaType.APPLICATION_JSON);

            long t0 = System.currentTimeMillis();
            Response response = invocationBuilder.get();
            String s = response.readEntity(String.class);
            //response.close();
            long t = System.currentTimeMillis() - t0;

            acumular(t);

            try {
                System.out.println("hilo " + id + ":" + response.getLocation().toURL().toString());
            } catch (Exception e) {
                System.out.println("error al intentar capturar la url");
            }

            System.out.println("hilo " + id + ": respuesta " + response.getStatus() + "/" + response.getLength() + " en " + t + " ms.");

//            System.out.println(response.getStatus());
//            System.out.println(response.readEntity(String.class));
        }

    }

    private void acumular(long t) {
        Estresador.pets++;
        Estresador.totalMs += t;
        Estresador.tiempoMedioMs = Estresador.totalMs / Estresador.pets;
        if (Estresador.tiempoMaximoMs < t) Estresador.tiempoMaximoMs = t;
        if (Estresador.tiempoMinimoMs == 0 || Estresador.tiempoMinimoMs > t) Estresador.tiempoMinimoMs = t;

        Estresador.petsUltimoMinuto++;
        Estresador.totalMsUltimoMinuto += t;
        Estresador.tiempoMedioMsUltimoMinuto = Estresador.totalMsUltimoMinuto / Estresador.petsUltimoMinuto;
        if (Estresador.tiempoMaximoMsUltimoMinuto < t) Estresador.tiempoMaximoMsUltimoMinuto = t;
        if (Estresador.tiempoMinimoMsUltimoMinuto == 0 || Estresador.tiempoMinimoMsUltimoMinuto > t) Estresador.tiempoMinimoMsUltimoMinuto = t;
    }
}
