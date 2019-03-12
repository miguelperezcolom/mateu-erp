package io.mateu.erp.model.invoicing.lists;

import com.vaadin.data.provider.QuerySortOrder;
import io.mateu.erp.model.booking.PurchaseOrder;
import io.mateu.erp.model.invoicing.Invoice;
import io.mateu.erp.model.invoicing.PurchaseCharge;
import io.mateu.erp.model.invoicing.PurchaseOrderInvoiceLine;
import io.mateu.erp.model.invoicing.ReceivedInvoice;
import io.mateu.erp.model.partners.Provider;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.interfaces.AbstractJPQLListView;
import io.mateu.mdd.core.model.authentication.Audit;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter@Setter
public class NotReceivedByBookingListView extends AbstractJPQLListView<NotReceivedByBookingListView.Row> {

    @MainSearchFilter
    private LocalDate from;

    @MainSearchFilter
    private LocalDate to;

    @MainSearchFilter
    private Provider provider;

    @Getter@Setter
    public class Row {
        private long purchaseOrderId;

        private LocalDateTime date;

        private LocalDate serviceDate;

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

        ql += " select l.purchaseOrder.id, l.purchaseOrder.audit.created, l.serviceDate, sum(l.total) as total " +
                " from " + PurchaseCharge.class.getName() + " l ";

        Map<String, Object> params = new HashMap<>();
        String w = complete(params);

        if (!"".equals(w)) ql += " where " + w + " ";

        ql += " group by l.purchaseOrder.id, l.purchaseOrder.audit.created, l.serviceDate ";
        ql += " order by total ";


        Query q = em.createQuery(ql);
        params.keySet().forEach(k -> q.setParameter(k, params.get(k)));

        if (forCount) {
            return getCountQueryForEclipseLink(em, q);
        }

        return q;
    }

    private String complete(Map<String,Object> params) {
        String w = " l.invoice = null ";

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

        if (provider != null) {
            if (!"".equals(w)) w += " and ";
            w += " l.provider.id = :h";
            params.put("h", provider.getId());
        }

        return w;
    }


    @Action
    public Invoice receive(EntityManager em, Set<Row> selection) {
        ReceivedInvoice result = new ReceivedInvoice();
        result.setAudit(new Audit(MDD.getCurrentUser()));

        String ql = "";

        ql += " select l " +
                " from " + PurchaseCharge.class.getName() + " l ";

        Map<String, Object> params = new HashMap<>();
        String w = complete(params);

        if (selection.size() > 0) {
            if (!"".equals(w)) w += " and ";
            w += " l.purchaseOrder in :ps ";
            params.put("ps", selection.stream().map(r -> em.find(PurchaseOrder.class, r.getPurchaseOrderId())).collect(Collectors.toList()));
        }

        if (!"".equals(w)) ql += " where " + w + " ";

        Query q = em.createQuery(ql);
        params.keySet().forEach(k -> q.setParameter(k, params.get(k)));

        List<PurchaseCharge> charges = q.getResultList();

        charges.forEach(c -> result.getLines().add(new PurchaseOrderInvoiceLine(result, c)));

        return result;
    }

    @Getter@Setter
    public class InvoiceResult {
        @Output
        private String msg;
        @Ignored
        private List<Invoice> invoices = new ArrayList<>();

        @Action
        public void send(String to, @TextArea String postscript) {
            MDD.alert("Invoices sent");
        }
    }
}
