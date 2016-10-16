package io.mateu.erp.model.product;

import io.mateu.erp.model.world.City;

import javax.persistence.*;

/**
 * Created by miguel on 16/10/16.
 */
@Entity
@DiscriminatorValue("PLACEABLE")
public class AbstractPlaceable extends AbstractItem {

    @ManyToOne
    @JoinColumn(name = "PLAIDCTY")
    private City city;

    @Column(name = "PLALON")
    private String lon;

    @Column(name = "PLALAT")
    private String lat;

}
