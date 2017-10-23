package io.mateu.erp.model.payments;

import io.mateu.erp.model.invoicing.Invoice;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Litigation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @ManyToOne
    private Invoice invoice;

    private double amount;

    private String comment;

}
