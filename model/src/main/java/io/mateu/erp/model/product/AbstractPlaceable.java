package io.mateu.erp.model.product;

import io.mateu.erp.model.world.City;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by miguel on 16/10/16.
 */
@Entity
public class AbstractPlaceable extends AbstractItem {

    @ManyToOne
    private City city;

    private String lon;

    private String lat;

}
