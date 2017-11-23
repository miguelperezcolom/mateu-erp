package io.mateu.erp.traductores.caval.in;


import org.easytravelapi.common.GetPortfolioRS;
import org.glassfish.jersey.client.ClientResponse;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class Tester {

    public static void main(String... args) {


        test1();


    }

    private static void test1() {

        Client client = ClientBuilder.newClient();

        WebTarget t = client.target("http://test.easytravelapi.com/rest/yourauthtoken/commons/portfolio");

        GetPortfolioRS response = t.request("application/json")
                .get(GetPortfolioRS.class);

        System.out.println("" + response.toString());


    }
}
