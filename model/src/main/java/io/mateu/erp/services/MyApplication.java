package io.mateu.erp.services;

import io.mateu.erp.services.easytravelapi.*;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/xresources")
public class MyApplication extends ResourceConfig {

    public MyApplication() {

        //packages("org.foo.rest;org.bar.rest");

        System.out.println("PASA POR AQUÍxxxx!!!!");

        //register(MyApplicationEventListener.class);

        //packages("io.mateu.common.services"); // no funciona si registramos las clases una a una
        register(PickupConfirmationService.class);
        register(CommonsServiceImpl.class);
        register(TransferBookingServiceImpl.class);
        register(ActivityBookingServiceImpl.class);
        register(HotelBookingServiceImpl.class);
        register(CircuitBookingServiceImpl.class);
        register(GenericBookingServiceImpl.class);
        register(AgentAccessServiceImpl.class);
        register(StatsResource.class);
        register(CMSServiceImpl.class);
        register(JacksonFeature.class);
        //todo: recuperar????
        //register(Converter1::class.java)
        register(new CORSFilter());

    }

}
