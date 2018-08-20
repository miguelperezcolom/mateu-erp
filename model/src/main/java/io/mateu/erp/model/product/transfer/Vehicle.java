package io.mateu.erp.model.product.transfer;

import io.mateu.mdd.core.annotations.SearchFilter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

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

    @NotNull
    @SearchFilter
    private String name;

    @NotNull
    private int minPax;

    @NotNull
    private int maxPax;

    private boolean onRequest;

    @Override
    public String toString() {
        return getName();
    }
}
