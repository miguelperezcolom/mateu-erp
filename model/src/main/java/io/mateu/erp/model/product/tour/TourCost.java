package io.mateu.erp.model.product.tour;

import com.vaadin.data.provider.DataProvider;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.partners.Provider;
import io.mateu.erp.model.product.AbstractProduct;
import io.mateu.erp.model.product.ProductType;
import io.mateu.erp.model.product.Variant;
import io.mateu.erp.model.product.generic.Extra;
import io.mateu.erp.model.product.generic.GenericProduct;
import io.mateu.mdd.core.annotations.DependsOn;
import io.mateu.mdd.core.annotations.SameLine;
import io.mateu.mdd.core.dataProviders.JPQLListDataProvider;
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
    private Variant variant;

    @DependsOn("product")
    public DataProvider getVariantDataProvider() throws Throwable {
        return new JPQLListDataProvider(
                "select x from " + Tour.class.getName() + " y inner join y.variants x " +
                        ((getTour() != null)?" where y.id = " + getTour().getId():""));
    }


    @ManyToOne
    private Extra extra;

    @NotNull
    @ManyToOne
    private ProductType type;

    @ManyToOne@NotNull
    private AbstractProduct product;

    @DependsOn("type")
    public DataProvider getProductDataProvider() throws Throwable {
        return new JPQLListDataProvider(
                "select x from " + AbstractProduct.class.getName() + " x " +
                        ((getType() != null)?" where x.type.id = " + getType().getId():""));
    }

    @ManyToOne
    @NotNull
    private Variant productVariant;

    @DependsOn("product")
    public DataProvider getProductVariantDataProvider() throws Throwable {
        return new JPQLListDataProvider(
                "select x from " + AbstractProduct.class.getName() + " y inner join y.variants x " +
                        ((getProduct() != null)?" where y.id = " + getProduct().getId():""));
    }

    @ManyToOne
    private ExcursionPriceZone pickupZone;

    private int fromTourPax;
    @SameLine
    private int toTourPax;

    @ManyToOne
    private Provider provider;

    @Column(name = "_order")
    private int order;

    private int day;

    private int nights;

    private int units;

    private boolean cash;

    private boolean providerConfirmationRequired;

    private boolean overrideCost;

    @ManyToOne
    private Currency currency;

    private double minCost;

    private double costPerService;

    private double costPerUnit;

    private double costPerPax;

    private double costPerInfant;

    private double costPerChild;

    private double costPerJunior;

    private double costPerAdult;

    private double costPerSenior;



    public boolean isCurrencyVisible() {
        return overrideCost;
    }

    public boolean isMinCostVisible() {
        return overrideCost;
    }

    public boolean isCostPerServiceVisible() {
        return overrideCost;
    }

    public boolean isCostPerUnitVisible() {
        return overrideCost;
    }

    public boolean isCostPerPaxVisible() {
        return overrideCost;
    }

    public boolean isCostPerInfantVisible() {
        return overrideCost;
    }

    public boolean isCostPerChildVisible() {
        return overrideCost;
    }

    public boolean isCostPerJuniorVisible() {
        return overrideCost;
    }

    public boolean isCostPerAdultVisible() {
        return overrideCost;
    }

    public boolean isCostPerSeniorVisible() {
        return overrideCost;
    }

}
