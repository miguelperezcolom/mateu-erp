package io.mateu.erp.services;

import io.mateu.erp.services.easytravelapi.*;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

public class ETAServer {

/*
    public static void main(String... args) throws IOException {

        HttpServer server = null;

        try {
            server = GrizzlyServerFactory.createHttpServer("http://localhost:5555");
            System.out.println("Press any key to stop the service...");
            System.in.read();
        } finally {
            try {
                if (server != null) {
                    server.stop();
                }
            } finally {
                ;
            }
        }
    }
*/


    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/myapp/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.underdog.jersey.grizzly package
        final ResourceConfig rc = new ResourceConfig(CommonsServiceImpl.class, ActivityBookingServiceImpl.class, TransferBookingServiceImpl.class, HotelBookingServiceImpl.class, ChannelManagerServiceImpl.class, StatsResource.class);
        //final ResourceConfig rc = new ResourceConfig().packages("io.mateu.common.services.easytravelapi");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        //new JHades().overlappingJarsReport();


        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.stop();
    }

}
