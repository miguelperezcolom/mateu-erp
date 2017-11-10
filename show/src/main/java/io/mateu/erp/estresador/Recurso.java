package io.mateu.erp.estresador;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

@Path("recurso")
public class Recurso {

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public String get() {
        String json = "{";

        try {

            json += "\"running\":" + Estresador.running;

            json += ",\"status\":\"" + (Estresador.running?"RUNNING":"IDLE") + "\"";

            DecimalFormat df = new DecimalFormat("###,###,###,###,###,###,###", new DecimalFormatSymbols(Locale.GERMAN));

            json += ",\"totalrqs\":\"" + df.format(Estresador.pets) + "\"";
            json += ",\"totalms\":" + Estresador.totalMs;
            json += ",\"avgms\":" + Estresador.tiempoMedioMs;
            json += ",\"minms\":" + Estresador.tiempoMinimoMs;
            json += ",\"maxms\":" + Estresador.tiempoMaximoMs;

            json += ",\"lasttotalrqs\":" + Estresador.petsUltimoMinuto;
            json += ",\"lasttotalms\":" + Estresador.totalMsUltimoMinuto;
            json += ",\"lastavgms\":" + Estresador.tiempoMedioMsUltimoMinuto;
            json += ",\"lastminms\":" + Estresador.tiempoMinimoMsUltimoMinuto;
            json += ",\"lastmaxms\":" + Estresador.tiempoMaximoMsUltimoMinuto;
            json += ",\"lastrqspersecond\":" + Estresador.petsUltimoMinuto;

            long segundos = 0;
            if (Estresador.inicio != null) segundos = (int) ChronoUnit.SECONDS.between(Estresador.inicio, Estresador.duracion);

            long ss = segundos % 60;
            long m = ((segundos - ss) / 60) % 60;
            long h = ((segundos - m * 60 - ss) / 3600) % 24;

            String s = "" + h + " hours " + m + " minutes " + ss + " seconds";


            json += ",\"testtime\":\"" + s + "\"";


            json += MonitorSistema.getJson();


        } catch (Exception e) {
            e.printStackTrace();
        }

        json += "}";
        return json;
    }

    @GET
    @Path("/run")
    @Produces(MediaType.TEXT_PLAIN)
    public String run(@QueryParam("url") String url, @QueryParam("hilos") int hilos, @QueryParam("duracionEnMinutos") int duracionEnMinutos) throws Exception {
        Estresador.estresar(url, hilos, duracionEnMinutos);
        return "ok";
    }

}
