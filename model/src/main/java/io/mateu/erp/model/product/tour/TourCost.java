package io.mateu.erp.model.product.tour;

import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.partners.Provider;
import io.mateu.erp.model.product.AbstractProduct;
import io.mateu.erp.model.product.ProductType;
import io.mateu.erp.model.product.Variant;
import io.mateu.erp.model.product.generic.Extra;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter@Setter
public class TourCost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private Tour tour;

    @ManyToOne
    @NotNull
    private Variant variant;

    @ManyToOne
    private Extra extra;

    @NotNull
    @ManyToOne
    private ProductType type;

    @ManyToOne
    private AbstractProduct product;

    @ManyToOne
    @NotNull
    private Variant productVariant;

    @ManyToOne
    private Provider provider;

    @Column(name = "_order")
    private int order;

    private int day;

    private int nights;

    private int units;

    private boolean overrideCost;

    @ManyToOne
    private Currency currency;

    private double costPerService;

    private double costPerAdult;

    private double costPerChild;

    private double costPerUnit;

    private double minCost;
}
