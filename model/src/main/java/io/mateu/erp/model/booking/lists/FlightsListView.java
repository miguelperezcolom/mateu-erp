package io.mateu.erp.model.booking.lists;

import com.google.common.base.Strings;
import com.vaadin.data.provider.QuerySortOrder;
import com.vaadin.icons.VaadinIcons;
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
    private LocalDate date = LocalDate.now();

    @MainSearchFilter
    private TransferDirection direction;


    @Getter@Setter
    public class Row {
        private String flightNumber;

        private String flightTime;

        private long bookings;

        private long pax;

        private boolean checked;

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

        ql += " select b.flightNumber as fn, to_char(b.flightTime, 'yyyy-MM-dd HH24:mi') as ft, count(*), sum(b.pax), bool_and(b.flightChecked) ";

        ql+= " from service b ";

        Map<String, Object> params = new HashMap<>();
        String w = "";

        w += " b.dtype = 'TransferService' and b.active = true ";

        if (date != null) {
            if (!"".equals(w)) w += " and ";
            w += " b.start = ?s";
            params.put("s", date);
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




    @Action(order = 1, icon = VaadinIcons.ARROW_LEFT)
    public void prev() throws Throwable {
        if (date != null) date = date.minusDays(1); else date = LocalDate.now().minusDays(1);
        search();
    }

    @Action(order = 2, icon = VaadinIcons.ARROW_RIGHT)
    public void next() throws Throwable {
        if (date != null) date = date.plusDays(1); else date = LocalDate.now().plusDays(1);
        search();
    }

    @Action(order = 3)
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

                System.out.println("" + bookings.size() + " bookings found for " + r.getFlightNumber() + " " + r.getFlightTime());

                for (TransferService b : bookings) {

                    System.out.println("modifying fligt data for transfer booking " + b.getBooking().getAgencyReference());

                    if (!Strings.isNullOrEmpty(flightNumber)) {
                        if (TransferDirection.OUTBOUND.equals(b.getDirection())) ((TransferBooking)b.getBooking()).setDepartureFlightNumber(flightNumber);
                        else ((TransferBooking)b.getBooking()).setArrivalFlightNumber(flightNumber);
                        b.getBooking().setUpdateRqTime(LocalDateTime.now());
                        System.out.println("flight number set to " + flightNumber);
                    }
                    if (flightTime != null) {
                        LocalDateTime d = TransferDirection.OUTBOUND.equals(b.getDirection())?((TransferBooking) b.getBooking()).getDepartureFlightTime():((TransferBooking) b.getBooking()).getArrivalFlightTime();
                        d = LocalDateTime.of(d.getYear(), d.getMonthValue(), d.getDayOfMonth(), ((flightTime - flightTime % 100) / 100), flightTime % 100);
                        if (TransferDirection.OUTBOUND.equals(b.getDirection())) ((TransferBooking)b.getBooking()).setDepartureFlightTime(d);
                        else ((TransferBooking)b.getBooking()).setArrivalFlightTime(d);
                        b.getBooking().setUpdateRqTime(LocalDateTime.now());
                        System.out.println("flight time set to " + flightTime);
                    }
                }

            });
        }
    }

    @Action(order = 4)
    public static void maskAsChecked(Set<Row> selection) throws Throwable {
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

                System.out.println("" + bookings.size() + " bookings found for " + r.getFlightNumber() + " " + r.getFlightTime());

                for (TransferService b : bookings) {

                    System.out.println("marking flight checked for transfer booking " + b.getBooking().getAgencyReference());

                    b.setFlightChecked(true);

                }

            });
        }
    }


}
