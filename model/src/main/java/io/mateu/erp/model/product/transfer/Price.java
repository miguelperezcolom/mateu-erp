package io.mateu.erp.model.product.transfer;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by miguel on 25/2/17.
 */
@Entity
@Getter
@Setter
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Contract contract;

    @ManyToOne
    private Vehicle vehicle;

    private PricePer pricePer;

    @ManyToOne
    private Zone zone;

    private double price;

}
