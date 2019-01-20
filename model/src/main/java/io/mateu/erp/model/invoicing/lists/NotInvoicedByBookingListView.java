package io.mateu.erp.model.invoicing.lists;

import io.mateu.erp.model.invoicing.BookingCharge;
import io.mateu.erp.model.invoicing.Charge;
import io.mateu.erp.model.invoicing.ChargeType;
import io.mateu.erp.model.invoicing.Invoice;
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
    public Query buildQuery(EntityManager em, boolean forCount) throws Throwable {

        String ql = "";

        ql += " select l.booking.id, l.partner.name, l.booking.leadName, sum(l.total.value) as total " +
                " from " + BookingCharge.class.getName() + " l ";

        Map<String, Object> params = new HashMap<>();
        String w = "";

        w += " l.type = " + ChargeType.class.getName() + "." + ChargeType.SALE + " and l.invoice = null ";

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


    @Action
    public static InvoiceResult invoice(EntityManager em, Set<Row> selection) {
        InvoiceResult result = new NotInvoicedByBookingListView().new InvoiceResult();
        for (Row r : selection) {
            System.out.println("facturar " + r.getAgentName() + " , from " + r.getFrom() + " , to " + r.getTo());
        }
        result.setMsg("3 invoices created.");
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
