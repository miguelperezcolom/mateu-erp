package io.mateu.erp.model.product.tour;

import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.product.Extra;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Getter@Setter
public class TourPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private Contract contract;

    @ManyToOne
    @NotNull
    private Tour tour;

    @ManyToOne
    @NotNull
    private TourVariant variant;

    @ManyToOne
    private TourPriceZone zone;

    @ManyToOne
    private Extra extra;

    @Column(name = "_start")
    private LocalDate start;

    @Column(name = "_end")
    private LocalDate end;

    private LocalDate bookingWindowStart;

    private LocalDate bookingWindowEnd;

    @ManyToOne
    private BillingConcept billingConcept;

    private String description;

    private double pricePerAdult;

    private double pricePerChild;

    private double pricePerVehicle;

    @Column(name = "_order")
    private int order;

    private boolean active;

}
