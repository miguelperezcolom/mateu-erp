package io.mateu.erp.nododispo;

import io.mateu.erp.services.StatsResource;
import io.mateu.erp.services.easytravelapi.*;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

public class ETAServer {

    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = System.getProperty("url", "http://localhost:8080");

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {

        final ResourceConfig rc = new ResourceConfig(CommonsServiceImpl.class, ActivityBookingServiceImpl.class, TransferBookingServiceImpl.class, HotelBookingServiceImpl.class, ChannelManagerServiceImpl.class, StatsResource.class);

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);

    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        HttpServer server = null;

        try {
            server = startServer();
            System.out.println("Press any key to stop the service...");
            System.in.read();
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
