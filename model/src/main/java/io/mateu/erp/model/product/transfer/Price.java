package io.mateu.erp.model.product.transfer;

import io.mateu.erp.model.authentication.ERPUser;
import io.mateu.erp.model.product.Tariff;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.SearchFilter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by miguel on 25/2/17.
 */
@Entity(name = "TransferContractPrice")
@Getter
@Setter
@Table(indexes = { @Index(name = "i_transferprice_deprecated", columnList = "deprecated") })
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    @SearchFilter
    private Contract contract;

    @ManyToOne@NotNull
    private Tariff tariff;

    @ManyToOne
    private Extra extra;

    @ManyToOne
    @NotNull
    @SearchFilter
    private Zone origin;

    @ManyToOne
    @NotNull
    @SearchFilter
    private Zone destination;

    @NotNull
    @SearchFilter
    private TransferType transferType;

    @ManyToOne
    @NotNull
    @SearchFilter
    private Vehicle vehicle;

    @NotNull
    private PricePer pricePer;

    private int fromPax;

    private int toPax;

    @NotNull
    private double price;

    private double returnPrice;

    @Column(name = "_order")
    private int order;

    private boolean finalPrice;



    @Ignored
    private boolean deprecated;


    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj != null && obj instanceof Price && id == ((Price) obj).getId());
    }

    @Override
    public String toString() {
        return "From " + (origin != null?origin.getName():"--") + " to " + (destination != null?destination.getName():"---") + " in " + (vehicle != null?vehicle.getName():"---") + " (" + fromPax + "-" + toPax + ") " + pricePer.name() + " = " + price + "/" + returnPrice;
    }

    public Price clone(EntityManager em, ERPUser u) {
        Price p = new Price();
        p.setPrice(getPrice());
        p.setTransferType(getTransferType());
        p.setDestination(getDestination());
        p.setOrigin(getOrigin());
        p.setPricePer(getPricePer());
        p.setVehicle(getVehicle());
        return p;
    }
}
