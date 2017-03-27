package io.mateu.erp.model.product.transfer;

import io.mateu.ui.mdd.server.annotations.Required;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by miguel on 25/2/17.
 */
@Entity
@Getter
@Setter
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Required
    private String name;

    @Required
    private int minPax;

    @Required
    private int maxPax;

    private boolean onRequest;
}
