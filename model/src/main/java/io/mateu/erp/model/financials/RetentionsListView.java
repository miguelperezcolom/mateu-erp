package io.mateu.erp.model.financials;

import com.google.common.base.Strings;
import com.vaadin.data.provider.QuerySortOrder;
import io.mateu.erp.model.invoicing.ReceivedInvoice;
import io.mateu.mdd.core.annotations.MainSearchFilter;
import io.mateu.mdd.core.annotations.Money;
import io.mateu.mdd.core.annotations.Sum;
import io.mateu.mdd.core.interfaces.AbstractJPQLListView;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter@Setter
public class RetentionsListView extends AbstractJPQLListView<RetentionsListView.Row> {

    @MainSearchFilter
    private LocalDate from;
    @MainSearchFilter
    private LocalDate to;
    @MainSearchFilter
    private FinancialAgent financialAgent;


    @Override
    public Query buildQuery(EntityManager em, List<QuerySortOrder> sortOrders, boolean forCount) throws Throwable {
        String ql = "";

        ql += " select l.issueDate, l.number, l.total, l.retainedPercent, l.retainedTotal " +
                " from " + ReceivedInvoice.class.getName() + " l ";

        Map<String, Object> params = new HashMap<>();
        String w = "";

        w += "l.retentionTerms is not null";

        if (from != null) {
            if (!"".equals(w)) w += " and ";
            w += " l.invoiceDate >= :s";
            params.put("s", from);
        }
        if (to != null) {
            if (!"".equals(w)) w += " and ";
            w += " l.invoiceDate <= :t";
            params.put("t", from);
        }

        if (financialAgent!= null) {
            if (!"".equals(w)) w += " and ";
            w += " l.issuer.id = :h";
            params.put("h", financialAgent.getId());
        }


        if (!"".equals(w)) ql += " where " + w + " ";


        ql += " order by l.issueDate ";


        Query q = em.createQuery(ql);
        params.keySet().forEach(k -> q.setParameter(k, params.get(k)));

        if (forCount) {
            return getCountQueryForEclipseLink(em, q);
        }

        return q;
    }

    @Getter@Setter
    public class Row {
        private LocalDate invoiceDate;
        private String invoiceNumber;
        @Money@Sum
        private double total;
        private double percent;
        @Money@Sum
        private double retained;
    }

}
