package io.mateu.erp.services.easytravelapi;

import io.swagger.jaxrs.config.BeanConfig;

import java.util.HashSet;
import java.util.Set;

public class Application  extends javax.ws.rs.core.Application {

    public Application() {

        System.out.println("hola 4!");

    }


    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> s = new HashSet<Class<?>>();

        s.add(CommonsServiceImpl.class);
        s.add(ChannelManagerServiceImpl.class);
        s.add(ActivityBookingServiceImpl.class);
        s.add(HotelBookingServiceImpl.class);
        s.add(TransferBookingServiceImpl.class);

        return s;
    }
}
