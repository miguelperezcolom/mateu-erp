package io.mateu.common.consolidador;

import org.easytravelapi.hotel.BookHotelRQ;
import org.easytravelapi.hotel.BookHotelRS;
import org.easytravelapi.util.Helper;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

public class ETATester {

    public static void main(String... args) {

        if (false) {
            Client client = ClientBuilder.newClient();
            WebTarget myResource = client.target("http://example.com/webapi/read");
            String response = myResource.request(MediaType.TEXT_PLAIN)
                    .get(String.class);
        }


        {
            Client client = ClientBuilder.newClient();
            BookHotelRQ order = new BookHotelRQ();
            WebTarget myResource = client.target("http://test.easytravelapi.com/rest").path("/yourauthtoken/hotel/booking");
            BookHotelRS trackingNumber = myResource.request(MediaType.APPLICATION_JSON)
                    .put(Entity.json(order), BookHotelRS.class);

            System.out.println("rs = " + Helper.toJson(trackingNumber));

        }


    }

}
