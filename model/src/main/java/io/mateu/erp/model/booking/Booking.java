package io.mateu.erp.model.booking;

import io.mateu.erp.model.authentication.Audit;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.financials.Currency;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPAHelper;
import io.mateu.ui.mdd.server.annotations.*;
import io.mateu.ui.mdd.server.util.JPATransaction;
import io.mateu.ui.mdd.shared.ActionType;
import io.mateu.ui.mdd.shared.MDDLink;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SearchFilter
    private long id;

    @Embedded
    @Output
    private Audit audit;

    @ManyToOne
    @Required
    @SearchFilter
    private Actor agency;

    @Required
    @SearchFilter(exactMatch = true)
    private String agencyReference;

    @StartsLine
    @Required
    @SearchFilter
    private String leadName;

    private String email;

    private String telephone;

    @StartsLine
    private boolean confirmed;

    private boolean cancelled;

    @Ignored
    @SearchFilter
    private LocalDate start;

    @Ignored
    private LocalDate finish;

    @StartsLine
    private String comments;


    @Ignored
    private double total;

    @Ignored
    private double balance;

    @Ignored
    @ManyToOne
    private Currency currency;


    @OneToMany(mappedBy = "booking")
    @OrderColumn(name = "orderInBooking")
    @Ignored
    private List<Service> services = new ArrayList<>();

    @Override
    public String toString() {
        return "" + getId() + " - " + getLeadName() + " (" + ((getAgency() != null)?getAgency().getName():"No agency") + ")";
    }


    @Action(name = "Services")
    public MDDLink openServices() {
        return new MDDLink(Service.class, ActionType.OPENLIST, new Data("id", getId()));
    }


    public static Booking getByAgencyRef(EntityManager em, String agencyRef, Actor age)
    {
        try {
            String jpql = "select x from Booking x" +
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
}
