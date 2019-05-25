package io.mateu.erp.model.invoicing;

import io.mateu.erp.model.taxes.VAT;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity@Getter@Setter
public class VATLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne@NotNull
    private Invoice invoice;

    private double base;

    private double percent;

    @ManyToOne
    private VAT vat;

    private double total;

    private boolean exempt;

    private boolean specialRegime;



}
