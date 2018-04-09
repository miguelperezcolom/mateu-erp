package io.mateu.erp.model.booking;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.mateu.erp.model.authentication.Audit;
import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.importing.TransferBookingRequest;
import io.mateu.erp.model.payments.BookingPaymentAllocation;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.erp.model.product.transfer.TransferType;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.Pair;
import io.mateu.ui.mdd.server.annotations.*;
import io.mateu.ui.mdd.server.interfaces.WithTriggers;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import io.mateu.ui.mdd.shared.ActionType;
import io.mateu.ui.mdd.shared.MDDLink;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * holder for booking. Basically a booking locator associated with a customer, under which we will
 * keep a list of booked services, charges, etc
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Getter
@Setter
@UseIdToSelect(ql = "select x.id, concat(x.leadName, ' - ', x.agency.name, ' - ', x.id) as text from Booking x where x.id = xxxx")
public class Booking implements WithTriggers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SearchFilter
    @Order(desc = true, priority = 10)
    private long id;

    @Embedded
    @Output
    @SearchFilter(field="created")
    @SearchFilter(field="modified")
    private Audit audit;

    @ManyToOne
    @NotNull
    @SearchFilter
    @QLFilter("x.agency = true")
    private Actor agency;

    @NotNull
    @SearchFilter(exactMatch = true)
    private String agencyReference;

    @NotNull
    @SearchFilter
    private String leadName;

    private String email;

    @SameLine
    private String telephone;

    private boolean confirmed;

    @SameLine
    private boolean cancelled;

    @Ignored
    @SearchFilter
    private LocalDate start;

    @Ignored
    @SameLine
    private LocalDate finish;

    private String comments;


    @Output
    private double totalNetValue;

    @Output
    @SameLine
    private double totalRetailValue;

    @Output
    @SameLine
    private double totalCommissionValue;


    @Output
    @SameLine
    private double balance;

    @Ignored
    @ManyToOne
    private Currency currency;

    @Transient
    @Ignored
    private boolean wasCancelled = false;


    @OneToMany(mappedBy = "booking")
    @OrderColumn(name = "orderInBooking")
    @Ignored
    private List<Service> services = new ArrayList<>();

    @OneToMany(mappedBy = "booking")
    @OrderColumn(name = "id")
    @Ignored
    private List<BookingPaymentAllocation> payments = new ArrayList<>();



    @Override
    public String toString() {
        return "" + getId() + " / " + ((getAgencyReference() != null)?getAgencyReference():"") + " - " + getLeadName() + " (" + ((getAgency() != null)?getAgency().getName():"No agency") + ") " + ((getComments() != null)?getComments():"");
    }


    @Links
    public List<MDDLink> getLinks() {
        List<MDDLink> l = new ArrayList<>();
        l.add(new MDDLink("Services", Service.class, ActionType.OPENLIST, new Data("booking.id", getId())));
        l.add(new MDDLink("Updates", TransferBookingRequest.class, ActionType.OPENLIST, new Data("customer", new Pair(getAgency().getId(), getAgency().getName()), "agencyReference", getAgencyReference())));
        return l;
    }

    public static Booking getByAgencyRef(EntityManager em, String agencyRef, Actor age)
    {
        try {
            String jpql = "select x from " + Booking.class.getName() + " x" +
                    " where x.agencyReference='" + agencyRef + "' and x.agency.id= " + age.getId();
            Query q = em.createQuery(jpql);
            List<Booking> l = q.getResultList();
            Booking b = (l.size() > 0)?l.get(0):null;
            return b;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String... args) throws Throwable {
        Actor a = new Actor();
        a.setId(1);
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {
                System.out.println(getByAgencyRef(em, "1234", a));
            }
        });

    }

    @Override
    public void beforeSet(EntityManager em, boolean isNew) throws Throwable {
        setWasCancelled(isCancelled());
    }

    @Override
    public void afterSet(EntityManager em, boolean isNew) throws Exception, Throwable {
        if (isCancelled() && isCancelled() != isWasCancelled()) {
            cancel(em, getAudit().getModifiedBy());
        }
    }

    public void cancel(EntityManager em, User u) {
        for (Service s : getServices()) {
            s.cancel(em, u);
        }
    }

    @Override
    public void beforeDelete(EntityManager em) throws Throwable {

    }

    @Override
    public void afterDelete(EntityManager em) throws Throwable {

    }

    public Map<String,Object> getData() {
        Map<String, Object> d = new HashMap<>();

        d.put("start", getStart());
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        d.put("serviceDates", "" + ((getStart() != null)?getStart().format(f):"...") + " - " + ((getFinish() != null)?getFinish().format(f):"..."));
        d.put("startddmmyyyy", getStart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        double base = Helper.roundOffEuros(getTotalNetValue() / (1d + 10d / 100d));
        double iva = Helper.roundOffEuros(getTotalNetValue() - base);


        d.put("base", base);
        d.put("iva", iva);

        List<String> points = new ArrayList<>();
        String comentarios = "";
        int pax = 0;
        boolean allServicesAreValued = true;
        boolean allPurchasesAreValued = true;
        double totalCost = 0;

        boolean todoCancelado = true;

        for (Service s : getServices()) {
            if (s instanceof TransferService) {
                TransferService t = (TransferService) s;
                if (pax < t.getPax()) pax = t.getPax();
                if (!points.contains(t.getPickupText())) points.add(t.getPickupText());
                if (!points.contains(t.getDropoffText())) points.add(t.getDropoffText());
                d.put("transferType", "" + t.getTransferType());
            }
            todoCancelado &= s.isCancelled();
            allServicesAreValued &= s.isValued();
            allPurchasesAreValued &= s.isPurchaseValued();
            if (!Strings.isNullOrEmpty(s.getComment())) comentarios += s.getComment();
            totalCost += s.getTotalCost();
        }

        d.put("valued", allServicesAreValued);
        d.put("total", getTotalNetValue());
        d.put("purchaseValued", allPurchasesAreValued);
        d.put("totalCost", totalCost);

        d.put("id", getId());
        d.put("locator", getId());
        d.put("leadName", getLeadName());
        d.put("agency", getAgency().getName());
        d.put("agencyReference", getAgencyReference());
        d.put("status", (todoCancelado)?"CANCELLED":"ACTIVE");
        d.put("created", getAudit().getCreated().format(DateTimeFormatter.BASIC_ISO_DATE.ISO_DATE_TIME));
        d.put("office", "");

        d.put("comments", comentarios);
        d.put("direction", String.join(",", points));
        d.put("pax", pax);

        return d;
    }
}
