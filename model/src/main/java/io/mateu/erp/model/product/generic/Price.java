package io.mateu.erp.model.product.generic;

import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.product.tour.Tour;
import io.mateu.erp.model.product.tour.TourDuration;
import io.mateu.erp.model.product.tour.TourPriceZone;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 31/1/17.
 */
@Entity(name = "GenericContractPrice")
@Getter
@Setter
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Contract contract;

    @ManyToOne
    private GenericProduct product;

    @ManyToOne
    private Extra extra;

    @Column(name = "_start")
    private LocalDate start;

    @Column(name = "_end")
    private LocalDate end;

    @ManyToOne
    private BillingConcept billingConcept;

    private String description;

    private LocalDate bookingWindowStart;

    private LocalDate bookingWindowEnd;

    private boolean percent;

    private double pricePerUnit;
    private double pricePerAdult;
    private double pricePerChild;
    private double pricePerUnitAndDay;
    private double pricePerAdultAndDay;
    private double pricePerChildAndDay;

    @ManyToOne
    private Tour tour;

    private TourDuration tourDuration;

    @ManyToOne
    private TourPriceZone tourPriceZone;

    private int fromTourPax;
    private int toTourPax;

    @Column(name = "_order")
    private int order;

    private boolean active = true;

}
