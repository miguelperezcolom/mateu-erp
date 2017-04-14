package io.mateu.erp.model.booking;

import io.mateu.erp.model.authentication.Audit;
import io.mateu.erp.model.financials.Actor;
import io.mateu.ui.mdd.server.annotations.*;
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
    @SearchFilter
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


    @OneToMany(mappedBy = "booking")
    @Ignored
    private List<Service> services = new ArrayList<>();

    @Override
    public String toString() {
        return "" + getId() + " - " + getLeadName() + " (" + ((getAgency() != null)?getAgency().getName():"No agency") + ")";
    }
}
