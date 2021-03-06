
package io.mateu.common.traductores.caval.in;

import javax.xml.ws.Endpoint;

/**
 * This class was generated by Apache CXF 3.2.0
 * 2017-11-23T11:17:17.813+01:00
 * Generated source version: 3.2.0
 * 
 */
 
public class Server {

    protected Server() throws Exception {
        System.out.println("Starting Server");



        {
            Object implementor = new HotelBookingServicePortImpl();
            String address = "http://localhost:8099/serveis/caval/20091127/soap/HotelBookingService";
            Endpoint.publish(address, implementor);
        }

        if (true) {
            Object implementor = new CommonsBookingServicePortImpl();
            String address = "http://localhost:8099/serveis/caval/20091127/soap/CommonsBookingService";
            Endpoint.publish(address, implementor);
        }


    }

    public static void main(String args[]) throws Exception {
        new Server();
        System.out.println("Server ready..."); 
        
        Thread.sleep(5 * 60 * 1000); 
        System.out.println("Server exiting");
        System.exit(0);
    }
}
