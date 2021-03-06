package io.mateu.erp.model.world;

import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.erp.model.taxes.VAT;
import io.mateu.mdd.core.annotations.Ignored;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * holder for states
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Getter@Setter
public class Destination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private Country country;

    @NotNull
    private String name;

    @OneToMany(mappedBy = "destination")
    @Ignored
    private List<Resort> resorts = new ArrayList<>();

    @OneToMany(mappedBy = "destination")
    @Ignored
    private List<Airport> airports = new ArrayList<>();

    @ManyToOne
    private VAT vat;

    private String paymentRemarks;

    @Column(name = "_order")
    private int order;



    @Override
    public String toString() {
        return getName();
    }
}