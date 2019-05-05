package io.mateu.erp.model.commissions;

import com.vaadin.data.provider.QuerySortOrder;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.BookingCommission;
import io.mateu.erp.model.partners.CommissionAgent;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.MainSearchFilter;
import io.mateu.mdd.core.interfaces.AbstractJPQLListView;
import io.mateu.mdd.core.model.authentication.Audit;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter@Setter
public class NotSettledByAgentListView extends AbstractJPQLListView<NotSettledByAgentListView.Row> {

    @MainSearchFilter
    private LocalDate from;

    @MainSearchFilter
    private LocalDate to;

    @MainSearchFilter
    private CommissionAgent commissionAgent;

    @Getter@Setter
    public class Row {
        @Ignored
        private long agentId;

        private String agentName;

        private double total;

        @Ignored
        private LocalDate from;

        @Ignored
        private LocalDate to;
    }


    @Override
    public Row getNewRowInstance() throws IllegalAccessException, InstantiationException {
        Row r = new Row();
        r.setFrom(from);
        r.setTo(to);
        return r;
    }

    @Override
    public Query buildQuery(EntityManager em, List<QuerySortOrder> sortOrders, boolean forCount) throws Throwable {
        String ql = "";

        ql += " select l.agent.id, l.agent.name, sum(l.total) as total " +
                " from " + BookingCommission.class.getName() + " l ";

        Map<String, Object> params = new HashMap<>();
        String w = "";

        w += " l.settlement = null ";

        if (from != null) {
            if (!"".equals(w)) w += " and ";
            w += " l.booking.serviceDate >= :s";
            params.put("s", from);
        }
        if (to != null) {
            if (!"".equals(w)) w += " and ";
            w += " l.booking.serviceDate <= :t";
            params.put("t", from);
        }

        if (commissionAgent != null) {
            if (!"".equals(w)) w += " and ";
            w += " l.agent.id = :h";
            params.put("h", commissionAgent.getId());
        }


        if (!"".equals(w)) ql += " where " + w + " ";



        ql += " group by l.agent.id, l.agent.name ";
        ql += " order by total ";


        Query q = em.createQuery(ql);
        params.keySet().forEach(k -> q.setParameter(k, params.get(k)));

        if (forCount) {
            return getCountQueryForEclipseLink(em, q);
        }

        return q;
    }


    public static List<BookingCommission> getCommissions(EntityManager em, Row row) {
        String ql = "";

        ql += " select l from " + BookingCommission.class.getName() + " l ";

        Map<String, Object> params = new HashMap<>();
        String w = "";

        w += " l.settlement = null ";

        if (row.getFrom() != null) {
            if (!"".equals(w)) w += " and ";
            w += " l.booking.serviceDate >= :s";
            params.put("s", row.getFrom());
        }
        if (row.getTo() != null) {
            if (!"".equals(w)) w += " and ";
            w += " l.booking.serviceDate <= :t";
            params.put("t", row.getTo());
        }

        if (row.getAgentId() > 0) {
            if (!"".equals(w)) w += " and ";
            w += " l.agent.id = :h";
            params.put("h", row.getAgentId());
        }


        if (!"".equals(w)) ql += " where " + w + " ";


        ql += " order by l.id ";


        Query q = em.createQuery(ql);
        params.keySet().forEach(k -> q.setParameter(k, params.get(k)));

        return q.getResultList();
    }


    @Action
    public static void settle(EntityManager em, Set<Row> selection) {
        for (Row r : selection) {
            CommissionSettlement s = new CommissionSettlement();
            s.setAudit(new Audit(MDD.getCurrentUser()));
            s.setAgent(em.find(CommissionAgent.class, r.getAgentId()));
            s.setLines(getCommissions(em, r));
            for (BookingCommission l : s.getLines()) {
                l.setSettlement(s);
            }
            s.setUpdatePending(true);
            em.persist(s);
        }
    }

}
