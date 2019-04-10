package io.mateu.erp.model.commissions;

import com.vaadin.data.provider.QuerySortOrder;
import io.mateu.erp.model.booking.Booking;
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

        private double commission;

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

        ql += " select l.commissionAgent.id, l.commissionAgent.name, sum(l.totalValue) as total, sum(l.totalCommission) as commission " +
                " from " + Booking.class.getName() + " l ";

        Map<String, Object> params = new HashMap<>();
        String w = "";

        w += " l.commissionSettlement = null ";

        if (from != null) {
            if (!"".equals(w)) w += " and ";
            w += " l.serviceDate >= :s";
            params.put("s", from);
        }
        if (from != null) {
            if (!"".equals(w)) w += " and ";
            w += " l.serviceDate <= :t";
            params.put("t", from);
        }

        if (commissionAgent != null) {
            if (!"".equals(w)) w += " and ";
            w += " l.commissionAgent.id = :h";
            params.put("h", commissionAgent.getId());
        }


        if (!"".equals(w)) ql += " where " + w + " ";



        ql += " group by l.commissionAgent.id, l.commissionAgent.name ";
        ql += " order by total ";


        Query q = em.createQuery(ql);
        params.keySet().forEach(k -> q.setParameter(k, params.get(k)));

        if (forCount) {
            return getCountQueryForEclipseLink(em, q);
        }

        return q;
    }


    @Action
    public static CommissionSettlement settle(EntityManager em, Set<Row> selection) {
        CommissionSettlement result = new CommissionSettlement();
        result.setAudit(new Audit(MDD.getCurrentUser()));
        for (Row r : selection) {

        }
        em.persist(result);
        return result;
    }

}
