package io.mateu.erp.model.invoicing.lists;

import com.vaadin.data.provider.QuerySortOrder;
import io.mateu.erp.model.invoicing.*;
import io.mateu.erp.model.partners.Agency;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.interfaces.AbstractJPQLListView;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Getter@Setter
public class NotInvoicedByAgencyListView extends AbstractJPQLListView<NotInvoicedByAgencyListView.Row> {

    @MainSearchFilter
    private LocalDate from;

    @MainSearchFilter
    private LocalDate to;

    @MainSearchFilter
    private Agency agency;

    @Getter@Setter
    public class Row {
        @Ignored
        private long agencyId;

        private String agencyName;

        private String financialAgent;

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

        ql += " select l.agency.id, l.agency.name, a.name, sum(l.total) as total " +
                " from " + BookingCharge.class.getName() + " l left join l.agency.financialAgent a";

        Map<String, Object> params = new HashMap<>();
        String w = complete(params);


        if (!"".equals(w)) ql += " where " + w + " ";


        ql += " group by l.agency.id, l.agency.name, a.name ";
        ql += " order by total ";


        Query q = em.createQuery(ql);
        params.keySet().forEach(k -> q.setParameter(k, params.get(k)));

        if (forCount) {
            return getCountQueryForEclipseLink(em, q);
        }

        return q;
    }

    private String complete(Map<String,Object> params) {
        String w = "";

        w += " l.invoice = null ";

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

        if (agency != null) {
            if (!"".equals(w)) w += " and ";
            w += " l.agency.id = :h";
            params.put("h", agency.getId());
        }

        return w;
    }


    @Action
    public InvoiceResult proform(EntityManager em, Set<Row> selection) throws Throwable {

        InvoiceResult result = new InvoiceResult();
        List<BookingCharge> charges = new ArrayList<>();

        String ql = "select l from " + BookingCharge.class.getName() + " l ";

        Map<String, Object> params = new HashMap<>();
        String w = complete(params);

        if (selection.size() > 0) {
            if (!"".equals(w)) w += " and ";
            w += " l.agency in :ps ";
            params.put("ps", selection.stream().map(r -> em.find(Agency.class, r.getAgencyId())).collect(Collectors.toList()));
        }

        if (!"".equals(w)) ql += " where " + w + " ";

        Query q = em.createQuery(ql);
        params.keySet().forEach(k -> q.setParameter(k, params.get(k)));

        charges.addAll(q.getResultList());

        List<IssuedInvoice> invoices = Invoicer.proform(em, MDD.getCurrentUser(), charges);

        result.setInvoices(invoices);
        result.setMsg("" + invoices.size() + " proformas created.");

        URL pdf = Invoice.createPdf(invoices);

        result.setPdf(pdf);

        return result;
    }
}
