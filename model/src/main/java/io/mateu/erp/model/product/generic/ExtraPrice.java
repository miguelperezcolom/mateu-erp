package io.mateu.erp.model.product.generic;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by miguel on 1/2/17.
 */
@Entity
@Getter
@Setter
public class ExtraPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Extra extra;

    @ManyToOne
    private Price price;

    private double pricePerUnit;
    private double pricePerAdult;
    private double pricePerChild;
    private double pricePerUnitAndDay;
    private double pricePerAdultAndDay;
    private double pricePerChildAndDay;

}
