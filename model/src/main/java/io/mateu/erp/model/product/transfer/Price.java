package io.mateu.erp.model.product.transfer;

import io.mateu.erp.model.authentication.User;
import io.mateu.ui.mdd.server.annotations.SearchFilter;
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
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    @SearchFilter
    private Contract contract;

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

    public Price clone(EntityManager em, User u) {
        Price p = new Price();
        p.setPrice(getPrice());
        p.setDestination(getDestination());
        p.setOrigin(getOrigin());
        p.setPricePer(getPricePer());
        p.setVehicle(getVehicle());
        return p;
    }
}
