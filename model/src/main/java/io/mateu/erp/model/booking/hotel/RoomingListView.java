package io.mateu.erp.model.booking.hotel;

import io.mateu.erp.model.booking.parts.HotelBooking;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.erp.model.workflow.SendRoomingByEmailTask;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.interfaces.AbstractJPQLListView;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.xml.ws.Action;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter@Setter
public class RoomingListView extends AbstractJPQLListView<RoomingListView.Row> {


    private Hotel hotel;

    private LocalDate checkInFrom = LocalDate.now();

    private LocalDate checkInTo;

    @Getter@Setter
    public class Row {
        @Ignored
        private long hotelId;

        private String hotelName;

        private long bookings;
    }

    @Override
    public Query buildQuery(EntityManager em, boolean forCount) throws Throwable {
        String ql = "";

        ql += " select h.id, h.name, count(*) ";

        ql+= " from booking b inner join hotel h on h.id = b.hotel_id ";

        Map<String, Object> params = new HashMap<>();
        String w = "";

        if (checkInFrom != null) {
            if (!"".equals(w)) w += " and ";
            w += " x.start >= :s";
            params.put("s", checkInFrom);
        }
        if (checkInTo != null) {
            if (!"".equals(w)) w += " and ";
            w += " x.end <= :t";
            params.put("t", checkInTo);
        }
        if (hotel != null) {
            if (!"".equals(w)) w += " and ";
            w += " x.hotel.id <= :h";
            params.put("h", hotel.getId());
        }

        if (!"".equals(w)) ql += " where " + w + " ";



        ql += " group by h.id, h.name ";
        ql += " order by h.name ";

        if (forCount) {
            ql = " select count(*) from (" + ql + ")";
        }

        Query q = em.createNativeQuery(ql);
        params.keySet().forEach(k -> q.setParameter(k, params.get(k)));

        return q;
    }


    @Action
    public void send(String email, String postscript, Set<Row> selection) throws Throwable {
        for (Row r : selection) {
            Helper.transact(em -> {

                Hotel h = em.find(Hotel.class, r.getHotelId());

                String s = "select x from " + HotelBooking.class.getName() + " x ";

                Map<String, Object> params = new HashMap<>();

                String w = "";

                if (checkInFrom != null) {
                    if (!"".equals(w)) w += " and ";
                    w += " x.start >= :s";
                    params.put("s", checkInFrom);
                }
                if (checkInTo != null) {
                    if (!"".equals(w)) w += " and ";
                    w += " x.end <= :t";
                    params.put("t", checkInTo);
                }
                if (hotel != null) {
                    if (!"".equals(w)) w += " and ";
                    w += " x.hotel.id <= :h";
                    params.put("h", hotel.getId());
                }

                if (!"".equals(w)) s += " where " + w + " ";

                s += " order by x.start";

                Query q = em.createQuery(s);
                params.keySet().forEach(k -> q.setParameter(k, params.get(k)));
                List<HotelBooking> bookings = q.getResultList();

                SendRoomingByEmailTask t = new SendRoomingByEmailTask(email, postscript, h, bookings);
                for (HotelBooking b : bookings) {
                    b.getTasks().add(t);
                }

            });
        }
    }
}
