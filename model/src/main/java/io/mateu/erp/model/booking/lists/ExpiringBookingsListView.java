package io.mateu.erp.model.booking.lists;

import com.google.common.base.Strings;
import com.vaadin.data.provider.QuerySortOrder;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.parts.TransferBooking;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.mdd.core.MDD;
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
public class ExpiringBookingsListView extends AbstractJPQLListView<ExpiringBookingsListView.Row> {

    private LocalDate checkInFrom = LocalDate.now();

    private LocalDate checkInTo;

    private LocalDate expiryDateFrom;

    private LocalDate expiryDateTo;

    @Getter@Setter
    public class Row {
        private long bookingId;

        private String leadName;

        private String agency;

        private LocalDateTime expiryDate;

        private LocalDate checkIn;

        private double paid;

        private double pending;
    }

    @Override
    public Query buildQuery(EntityManager em, List<QuerySortOrder> sortOrders, boolean forCount) throws Throwable {
        String ql = "";

        ql += " select b.id, b.leadName, b.agency.name, b.expiryDate, b.start, b.totalPaid, b.balance ";

        ql+= " from " + Booking.class.getName() + " b ";

        Map<String, Object> params = new HashMap<>();
        String w = "";

        w += " b.active = true and b.expiryDate is not null ";

        if (checkInFrom != null) {
            if (!"".equals(w)) w += " and ";
            w += " b.start >= :s";
            params.put("s", checkInFrom);
        }
        if (checkInTo != null) {
            if (!"".equals(w)) w += " and ";
            w += " b.end <= :t";
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



        ql += " order by b.expiryDate, b.start ";


        Query q = em.createQuery(ql);
        params.keySet().forEach(k -> q.setParameter(k, params.get(k)));

        if (forCount) {
            return getCountQueryForEclipseLink(em, q);
        }

        return q;
    }


    @Action
    public static void sendEmail(String postscript, Set<Row> selection) throws Throwable {
        MDD.info("Done");
    }

    @Action
    public static void cancel(Set<Row> selection) throws Throwable {
        MDD.info("Done");
    }
}
