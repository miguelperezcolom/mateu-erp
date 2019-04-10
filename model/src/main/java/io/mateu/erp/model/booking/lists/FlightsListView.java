package io.mateu.erp.model.booking.lists;

import com.google.common.base.Strings;
import com.vaadin.data.provider.QuerySortOrder;
import io.mateu.erp.model.booking.parts.TransferBooking;
import io.mateu.erp.model.booking.transfer.TransferDirection;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.MainSearchFilter;
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

    @MainSearchFilter
    private LocalDate checkInFrom = LocalDate.now();

    @MainSearchFilter
    private LocalDate checkInTo;

    @MainSearchFilter
    private TransferDirection direction;


    @Getter@Setter
    public class Row {
        private String flightNumber;

        private String flightTime;

        private long bookings;

        private long pax;

        @Override
        public boolean equals(Object obj) {
            return this == obj || (obj != null && obj instanceof Row && getSignature().equals(((Row)obj).getSignature()));
        }

        public String getSignature() {
            return "" + flightNumber + "-" + flightTime;
        }
    }

    @Override
    public Query buildQuery(EntityManager em, List<QuerySortOrder> sortOrders, boolean forCount) throws Throwable {
        String ql = "";

        ql += " select b.flightNumber as fn, to_char(b.flightTime, 'yyyy-MM-dd HH24:mi') as ft, count(*), sum(b.pax) ";

        ql+= " from service b ";

        Map<String, Object> params = new HashMap<>();
        String w = "";

        w += " b.dtype = 'TransferService' and b.active = true ";

        if (checkInFrom != null) {
            if (!"".equals(w)) w += " and ";
            w += " b.flightTime >= ?s";
            params.put("s", checkInFrom.atStartOfDay());
        }
        if (checkInTo != null) {
            if (!"".equals(w)) w += " and ";
            w += " b.flightTime < ?t";
            params.put("t", checkInTo.plusDays(1).atStartOfDay());
        }

        if (direction != null) {
            if (!"".equals(w)) w += " and ";
            w += " b.direction = ?d";
            params.put("d", direction.ordinal());
        }

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
    public static void changeFlightInfo(String flightNumber, Integer flightTime, Set<Row> selection) throws Throwable {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Row r : selection) {
            Helper.transact(em -> {

                String s = "select x from " + TransferService.class.getName() + " x ";

                Map<String, Object> params = new HashMap<>();

                String w = "";

                if (r.getFlightNumber() != null) {
                    if (!"".equals(w)) w += " and ";
                    w += " x.flightNumber = :fn";
                    params.put("fn", r.getFlightNumber());
                }

                if (r.getFlightTime() != null) {
                    if (!"".equals(w)) w += " and ";
                    w += " x.flightTime >= :ft0 and x.flightTime < :ft1 ";
                    params.put("ft0", LocalDateTime.parse(r.getFlightTime(), dtf));
                    params.put("ft1", LocalDateTime.parse(r.getFlightTime(), dtf).plusMinutes(1));
                }

                if (!"".equals(w)) s += " where " + w + " ";

                s += " order by x.start";

                Query q = em.createQuery(s);
                params.keySet().forEach(k -> q.setParameter(k, params.get(k)));
                List<TransferService> bookings = q.getResultList();

                for (TransferService b : bookings) {
                    if (!Strings.isNullOrEmpty(flightNumber)) {
                        if (TransferDirection.OUTBOUND.equals(b.getDirection())) ((TransferBooking)b.getBooking()).setDepartureFlightNumber(flightNumber);
                        else ((TransferBooking)b.getBooking()).setArrivalFlightNumber(flightNumber);
                    }
                    if (flightTime != null) {
                        LocalDateTime d = TransferDirection.OUTBOUND.equals(b.getDirection())?((TransferBooking) b.getBooking()).getDepartureFlightTime():((TransferBooking) b.getBooking()).getArrivalFlightTime();
                        d = LocalDateTime.of(d.getYear(), d.getMonthValue(), d.getDayOfMonth(), ((flightTime - flightTime % 100) / 100), flightTime % 100);
                        if (TransferDirection.OUTBOUND.equals(b.getDirection())) ((TransferBooking)b.getBooking()).setDepartureFlightTime(d);
                        else ((TransferBooking)b.getBooking()).setArrivalFlightTime(d);
                    }
                }

            });
        }
    }
}
