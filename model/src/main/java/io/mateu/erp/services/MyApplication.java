package io.mateu.erp.services;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("resources")
public class MyApplication extends ResourceConfig {
    public MyApplication() {
        //packages("org.foo.rest;org.bar.rest");
        packages("io.mateu.erp.services");
        register(JacksonFeature.class);
        register(new CORSFilter());
    }
}