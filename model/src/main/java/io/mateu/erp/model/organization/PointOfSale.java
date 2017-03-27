package io.mateu.erp.model.organization;

import io.mateu.ui.mdd.server.annotations.Required;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * holder for points of sale (e.g. a website, webservices)
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Getter
@Setter
public class PointOfSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Required
    private String name;

}
