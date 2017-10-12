package io.mateu.erp.services.easytravelapi;

import javax.xml.ws.Endpoint;

/**
 * Created by miguel on 27/7/17.
 */
public class Server {

    public static void main(String...args) {
        System.out.println("Starting Server");


        {
            ActivityBookingServiceImpl implementor = new ActivityBookingServiceImpl();
            String address = "http://localhost:9000/activity";
            Endpoint.publish(address, implementor);
        }


    }

}
