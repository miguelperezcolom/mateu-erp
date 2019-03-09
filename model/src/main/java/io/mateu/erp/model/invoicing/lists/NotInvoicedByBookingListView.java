package io.mateu.erp.model.invoicing.lists;

import com.vaadin.data.provider.QuerySortOrder;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.invoicing.*;
import io.mateu.erp.model.partners.Partner;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.interfaces.AbstractJPQLListView;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Getter@Setter
public class NotInvoicedByBookingListView extends AbstractJPQLListView<NotInvoicedByBookingListView.Row> {

    @MainSearchFilter
    private LocalDate from;

    @MainSearchFilter
    private LocalDate to;

    @MainSearchFilter
    private Partner partner;

    @Getter@Setter
    public class Row {
        private long bookingId;

        private String agentName;

        private String leadName;

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

        ql += " select l.booking.id, l.partner.name, l.booking.leadName, sum(l.total.value) as total " +
                " from " + BookingCharge.class.getName() + " l ";

        Map<String, Object> params = new HashMap<>();
        String w = complete(params);

        if (!"".equals(w)) ql += " where " + w + " ";



        ql += " group by l.booking.id, l.partner.name, l.booking.leadName ";
        ql += " order by total ";


        Query q = em.createQuery(ql);
        params.keySet().forEach(k -> q.setParameter(k, params.get(k)));

        if (forCount) {
            return getCountQueryForEclipseLink(em, q);
        }

        return q;
    }

    private String complete(Map<String,Object> params) {
        String w = " l.type = " + ChargeType.class.getName() + "." + ChargeType.SALE + " and l.invoice = null ";

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

        if (partner != null) {
            if (!"".equals(w)) w += " and ";
            w += " l.partner.id = :h";
            params.put("h", partner.getId());
        }

        return w;
    }


    @Action
    public InvoiceResult invoice(EntityManager em, Set<Row> selection) throws Throwable {
        InvoiceResult result = new NotInvoicedByBookingListView().new InvoiceResult();

        List<Charge> charges = new ArrayList<>();

        String ql = "select l from " + Charge.class.getName() + " l ";

        Map<String, Object> params = new HashMap<>();
        String w = complete(params);

        if (selection.size() > 0) {
            if (!"".equals(w)) w += " and ";
            w += " l.booking in :ps ";
            params.put("ps", selection.stream().map(r -> em.find(Booking.class, r.getBookingId())).collect(Collectors.toList()));
        }

        if (!"".equals(w)) ql += " where " + w + " ";

        Query q = em.createQuery(ql);
        params.keySet().forEach(k -> q.setParameter(k, params.get(k)));

        charges.addAll(q.getResultList());

        List<IssuedInvoice> invoices = Invoicer.invoice(em, MDD.getCurrentUser(), charges);

        result.setInvoices(invoices);
        result.setMsg("" + invoices.size() + " invoices created.");

        return result;
    }

    @Getter@Setter
    public class InvoiceResult {
        @Output
        private String msg;
        @Ignored
        private List<IssuedInvoice> invoices = new ArrayList<>();

        @Action
        public void send(String to, @TextArea String postscript) {
            MDD.alert("Invoices sent");
        }
    }
}
