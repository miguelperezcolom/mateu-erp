package io.mateu.erp.model.booking;

import io.mateu.erp.model.authentication.Audit;
import io.mateu.erp.model.financials.Actor;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPAHelper;
import io.mateu.ui.mdd.server.annotations.*;
import io.mateu.ui.mdd.server.util.JPATransaction;
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
    private long id;

    @Embedded
    @Output
    private Audit audit;

    @ManyToOne
    @Required
    private Actor agency;

    @Required
    private String agencyReference;

    @StartsLine
    @Required
    private String leadName;

    private String email;

    @StartsLine
    private boolean confirmed;

    private boolean cancelled;

    @Ignored
    private LocalDate start;

    @Ignored
    private LocalDate finish;

    @StartsLine
    private String comments;


    @Ignored
    private double total;

    @Ignored
    private double balance;


    @OneToMany(mappedBy = "booking")
    @Ignored
    private List<Service> services = new ArrayList<>();

    @Override
    public String toString() {
        return "" + getId() + " - " + getLeadName() + " (" + ((getAgency() != null)?getAgency().getName():"No agency") + ")";
    }


    public static Booking getByAgencyRef(EntityManager em, String agencyRef, Actor age)
    {
        try {
            String jpql = "select x from Booking x" +
                    " where x.agencyReference='" + agencyRef + "' and x.agency.id= " + age.getId();
            Query q = em.createQuery(jpql);
            Booking b = (Booking) q.getResultList().get(0);
            return b;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String... args) throws Exception {
        Actor a = new Actor();
        a.setId(1);
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {
                System.out.println(getByAgencyRef(em, "1234", a));
            }
        });

    }
}
