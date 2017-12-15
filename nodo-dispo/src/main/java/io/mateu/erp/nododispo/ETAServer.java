package io.mateu.erp.nododispo;

import com.google.common.base.Strings;
import io.mateu.erp.model.util.Helper;
import io.mateu.erp.services.StatsResource;
import io.mateu.erp.services.easytravelapi.*;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.logging.LogManager;

public class ETAServer {

    static {
        Helper.loadProperties();
    }


    /**
     * Should be called before the first use of the JDK logging. The best place is
     * in the static initializer of the class, which has the {@code main()} method.
     */
    static {
        String loggingfile = System.getProperty("java.util.logging.config.file");
        if (Strings.isNullOrEmpty(loggingfile)) {
            URL url = null;
            try {
                url = ETAServer.class.getClassLoader().getResource("logging.properties");
                if (url == null) {
                    System.err.println("Cannot find logging.properties.");
                } else {
                    LogManager.getLogManager().readConfiguration(url.openStream());
                }
            } catch (Exception e) {
                System.err.println("Error reading logging.properties from '" + url + "': " + e);
            }
        }
    }




    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = System.getProperty("url", "http://localhost:8080");

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {

        final ResourceConfig rc = new ResourceConfig(CommonsServiceImpl.class, ActivityBookingServiceImpl.class, TransferBookingServiceImpl.class, HotelBookingServiceImpl.class, ChannelManagerServiceImpl.class, StatsResource.class);
        //final ResourceConfig rc = new ResourceConfig(HotelBookingServiceImpl.class);

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);

    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        System.out.println("starting server at " + BASE_URI);

        HttpServer server = null;

        try {
            server = startServer();
//            System.out.println("Press any key to stop the service...");
//            System.in.read();

            while (true) Thread.sleep(20000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (server != null) {
                    server.shutdown();
                }
            } finally {
                ;
            }
        }
        System.out.println("adios!");

    }

}
