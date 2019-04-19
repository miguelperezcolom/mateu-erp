package io.mateu.erp.model.booking;

import io.mateu.erp.model.commissions.CommissionSettlement;
import io.mateu.erp.model.partners.CommissionAgent;
import io.mateu.mdd.core.annotations.Output;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity@Getter@Setter
public class BookingCommission {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @ManyToOne
    @Output
    private Booking booking;

    @ManyToOne@NotNull
    private CommissionAgent agent;


    private double total;

    @ManyToOne@Output
    private CommissionSettlement settlement;

}
