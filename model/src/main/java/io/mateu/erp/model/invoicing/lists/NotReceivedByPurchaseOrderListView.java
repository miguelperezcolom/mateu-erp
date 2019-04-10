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
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter@Setter
public class NotReceivedByPurchaseOrderListView extends AbstractJPQLListView<NotReceivedByPurchaseOrderListView.Row> {

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

    @Override
    public boolean isEditHandled() {
        return true;
    }

    @Override
    public Object onEdit(Row row) throws Throwable {
        return Helper.find(PurchaseOrder.class, row.getPurchaseOrderId());
    }

    @Action
    public Invoice receive(EntityManager em, Set<Row> selection, @NotEmpty String invoiceNumber, @NotNull LocalDate invoiceDate, LocalDate taxDate, LocalDate dueDate) throws Exception {

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

        return createInvoice(em, charges, invoiceNumber, invoiceDate, taxDate, dueDate);
    }

    public static Invoice createInvoice(EntityManager em, List<PurchaseCharge> charges, String invoiceNumber, LocalDate invoiceDate, LocalDate taxDate, LocalDate dueDate) throws Exception {
        ReceivedInvoice result = new ReceivedInvoice();
        result.setAudit(new Audit(MDD.getCurrentUser()));


        if (charges.size() == 0) throw new Exception("No charge selected");

        charges.forEach(c -> {
            result.getLines().add(new PurchaseOrderInvoiceLine(result, c));
            c.setInvoice(result);
        });

        result.setNumber(invoiceNumber);
        result.setIssueDate(invoiceDate);
        result.setDueDate(dueDate);
        result.setTaxDate(taxDate);
        result.setProvider(charges.get(0).getProvider());
        result.setRecipient(charges.get(0).getPurchaseOrder().getOffice().getCompany().getFinancialAgent());
        result.setIssuer(result.getProvider().getFinancialAgent());
        result.setCurrency(result.getProvider().getCurrency());

        if (result.getRecipient() == null) throw new Exception("Missing financial agent for company " + charges.get(0).getPurchaseOrder().getOffice().getCompany().getName() + ". Please fill");
        if (result.getIssuer() == null) throw new Exception("Missing financial agent for provider " + result.getProvider().getName() + ". Please fill");

        em.persist(result);

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
