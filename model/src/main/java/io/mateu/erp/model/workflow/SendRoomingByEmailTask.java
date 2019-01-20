package io.mateu.erp.model.workflow;

import com.google.common.base.Strings;
import io.mateu.erp.model.booking.parts.HotelBooking;
import io.mateu.erp.model.booking.parts.HotelBookingLine;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import java.io.IOException;
import java.util.*;

@Entity@Getter@Setter
public class SendRoomingByEmailTask extends SendEmailTask {

    public SendRoomingByEmailTask() {

    }

    public SendRoomingByEmailTask(String email, String postscript, Hotel hotel, Collection<HotelBooking> bookings) throws Throwable {
        setSubject("Rooming list");
        setMessage(createMessage(email, postscript, hotel, bookings));
        if (!Strings.isNullOrEmpty(email)) setTo(email);
        else setTo(hotel.getEmail());
    }

    private String createMessage(String email, String postscript, Hotel hotel, Collection<HotelBooking> bookings) throws Throwable {
        String h = "";
        Map<String, Object> data = getData(hotel, bookings);
        System.out.println("data=" + Helper.toJson(data));

        data.put("postscript", postscript);

        h = Helper.freemark(Helper.find(AppConfig.class, 1l).getRoomingTemplate(), data);
        return h;
    }

    private Map<String,Object> getData(Hotel hotel, Collection<HotelBooking> bookings) {
        HashMap<String, Object> d = new HashMap<>();

        ArrayList<Object> ldbs;
        d.put("bookings", ldbs = new ArrayList<>());

        for (HotelBooking b : bookings) {
            HashMap<String, Object> db;
            ldbs.add(db = new HashMap<>());

            db.put("bokingId", b.getId());
            db.put("leadName", b.getLeadName());
            db.put("formalizated", b.getFormalizationDate() != null?b.getFormalizationDate().toLocalDate().toString():(b.getAudit() != null && b.getAudit().getCreated() != null?b.getAudit().getCreated().toLocalDate().toString():"---"));

            ArrayList<Object> ldls;
            db.put("lines", ldls = new ArrayList<>());

            for (HotelBookingLine l : b.getLines()) {
                HashMap<String, Object> dl;
                ldls.add(dl = new HashMap<>());

                dl.put("start", l.getStart().toString());
                dl.put("end", l.getEnd().toString());
                dl.put("rooms", l.getRooms());
                dl.put("room", l.getRoom().getName());
                dl.put("board", l.getBoard().getName());
                dl.put("adults", l.getAdultsPerRoon());
                dl.put("children", l.getChildrenPerRoom());
                dl.put("ages", l.getAges() != null?Arrays.toString(l.getAges()):"");
                dl.put("available", l.isAvailable());
                dl.put("active", l.isActive());
            }

        }

        return d;
    }

}
