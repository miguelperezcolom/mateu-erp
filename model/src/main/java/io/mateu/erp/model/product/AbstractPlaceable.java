package io.mateu.erp.model.product;

import io.mateu.erp.model.world.City;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.ManyToOne;

/**
 * Created by miguel on 16/10/16.
 */


@Getter
@Setter
public class AbstractPlaceable extends AbstractItem {

    @ManyToOne
    private City city;

    private String lon;

    private String lat;

}
