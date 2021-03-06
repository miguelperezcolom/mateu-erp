
package io.mateu.common.traductores.ota.in;

import javax.xml.ws.Endpoint;

/**
 * This class was generated by Apache CXF 3.2.0
 * 2017-11-23T16:27:52.302+01:00
 * Generated source version: 3.2.0
 * 
 */
 
public class Server {

    protected Server() throws Exception {
        System.out.println("Starting Server");

        {
            Object implementor = new HotelBatchPortImpl();
            String address = "http://localhost:8098/Service/Travel/v2/HotelBatch.svc";
            Endpoint.publish(address, implementor);
        }

        {
            Object implementor = new HotelPortImpl();
            String address = "http://localhost:8098/Service/Travel/v2/Hotel.svc";
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
