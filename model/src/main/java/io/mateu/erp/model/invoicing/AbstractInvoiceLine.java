package io.mateu.erp.model.invoicing;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Getter
@Setter
public abstract class AbstractInvoiceLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Invoice invoice;

    private String subject;

    private double quantity;

    private double price;

    private double discountPercent;

    private double total;


}
