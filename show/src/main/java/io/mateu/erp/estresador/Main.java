package io.mateu.erp.estresador;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class Main {

    public static void main(String... args) {

        URI baseUri = UriBuilder.fromUri("http://0.0.0.0/").port(9998).build();
        ResourceConfig config = new ResourceConfig(Recurso.class);
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config);



    }


}
