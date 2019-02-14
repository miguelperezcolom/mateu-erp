package io.mateu.erp.model.booking.lists;

import com.google.common.base.Strings;
import io.mateu.erp.model.booking.parts.TransferBooking;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.interfaces.AbstractJPQLListView;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter@Setter
public class FlightsListView extends AbstractJPQLListView<FlightsListView.Row> {

    private LocalDate checkInFrom = LocalDate.now();

    private LocalDate checkInTo;

    @Getter@Setter
    public class Row {
        private String flightNumber;

        private String flightTime;

        private long bookings;

        private long pax;
    }

    @Override
    public Query buildQuery(EntityManager em, boolean forCount) throws Throwable {
        String ql = "";

        ql += " select b.arrivalflightnumber as fn, to_char(b.arrivalflighttime, 'yyyy-MM-dd HH:mi') as ft, count(*), sum(b.adults + b.children) ";

        ql+= " from booking b ";

        Map<String, Object> params = new HashMap<>();
        String w = "";

        w += " b.dtype = 'TransferBooking' and b.active = true ";

        if (checkInFrom != null) {
            if (!"".equals(w)) w += " and ";
            w += " b.start >= ?s";
            params.put("s", checkInFrom);
        }
        if (checkInTo != null) {
            if (!"".equals(w)) w += " and ";
            w += " b.end <= ?t";
            params.put("t", checkInTo);
        }
        /*
        if (hotel != null) {
            if (!"".equals(w)) w += " and ";
            w += " x.hotel.id <= :h";
            params.put("h", hotel.getId());
        }
        */

        if (!"".equals(w)) ql += " where " + w + " ";



        ql += " group by fn, ft ";
        ql += " order by ft, fn ";

        if (forCount) {
            ql = " select count(*) from (" + ql + ") x";
        }

        Query q = em.createNativeQuery(ql);
        params.keySet().forEach(k -> q.setParameter(k, params.get(k)));

        return q;
    }


    @Action
    public static void changeFlightInfo(String flightNumber, LocalDateTime flightTime, Set<Row> selection) throws Throwable {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Row r : selection) {
            Helper.transact(em -> {

                String s = "select x from " + TransferBooking.class.getName() + " x ";

                Map<String, Object> params = new HashMap<>();

                String w = "";

                if (r.getFlightNumber() != null) {
                    if (!"".equals(w)) w += " and ";
                    w += " x.arrivalFlightNumber = :fn";
                    params.put("fn", r.getFlightNumber());
                }

                if (r.getFlightTime() != null) {
                    if (!"".equals(w)) w += " and ";
                    w += " x.arrivalFlightTime = :ft";
                    params.put("ft", LocalDateTime.parse(r.getFlightTime(), dtf));
                }

                if (!"".equals(w)) s += " where " + w + " ";

                s += " order by x.start";

                Query q = em.createQuery(s);
                params.keySet().forEach(k -> q.setParameter(k, params.get(k)));
                List<TransferBooking> bookings = q.getResultList();

                for (TransferBooking b : bookings) {
                    if (!Strings.isNullOrEmpty(flightNumber)) b.setArrivalFlightNumber(flightNumber);
                    if (flightTime != null) b.setArrivalFlightTime(flightTime);
                }

            });
        }
    }
}
