package io.mateu.erp.services;

import io.mateu.erp.services.easytravelapi.CMSServiceImpl;
import io.mateu.erp.services.easytravelapi.HotelBookingServiceImpl;
import io.mateu.ui.core.rest.Converter1;
import io.mateu.ui.core.rest.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/xresources")
public class MyApplication extends ResourceConfig {
    public MyApplication() {

        //packages("org.foo.rest;org.bar.rest");

        System.out.println("PASA POR AQUÍxxxx!!!!");

        //register(MyApplicationEventListener.class);

        //packages("io.mateu.erp.services"); // no funciona si registramos las clases una a una
        register(PickupConfirmationService.class);
        register(HotelBookingServiceImpl.class);
        register(CMSServiceImpl.class);
        register(JacksonFeature.class);
        register(Converter1.class);
        register(new CORSFilter());
    }
}