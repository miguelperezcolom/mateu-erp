package io.mateu.erp.model.booking;

import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.partners.Provider;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity@Getter@Setter
public class BookingIssue {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne@NotNull
    private Booking booking;

    private String text;

    @ManyToOne@NotNull
    private BillingConcept billingConcept;

    private double charge;

    private double cost;

    @NotNull@ManyToOne
    private Currency currency;

    @NotNull
    private Provider provider;
}
