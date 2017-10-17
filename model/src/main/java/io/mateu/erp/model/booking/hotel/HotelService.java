package io.mateu.erp.model.booking.hotel;

import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.product.hotel.Hotel;

import javax.persistence.EntityManager;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class HotelService extends Service {


    private Hotel hotel;

    private List<HotelServiceLine> lines = new ArrayList<>();



    @Override
    public String createSignature() {
        return null;
    }

    @Override
    public double rate(EntityManager em, boolean sale, Actor supplier, PrintWriter report) throws Throwable {
        return 0;
    }
}
