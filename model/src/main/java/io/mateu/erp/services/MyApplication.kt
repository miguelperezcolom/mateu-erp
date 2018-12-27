package io.mateu.erp.services

import io.mateu.erp.services.easytravelapi.*
import org.glassfish.jersey.jackson.JacksonFeature
import org.glassfish.jersey.server.ResourceConfig

import javax.ws.rs.ApplicationPath

@ApplicationPath("/xresources")
class MyApplication : ResourceConfig() {
    init {

        //packages("org.foo.rest;org.bar.rest");

        println("PASA POR AQUÍxxxx!!!!")

        //register(MyApplicationEventListener.class);

        //packages("io.mateu.common.services"); // no funciona si registramos las clases una a una
        register(PickupConfirmationService::class.java)
        register(CommonsServiceImpl::class.java)
        register(TransferBookingServiceImpl::class.java)
        register(ActivityBookingServiceImpl::class.java)
        register(HotelBookingServiceImpl::class.java)
        register(StatsResource::class.java)
        register(CMSServiceImpl::class.java)
        register(JacksonFeature::class.java)
        //todo: recuperar????
        //register(Converter1::class.java)
        register(CORSFilter())
    }
}