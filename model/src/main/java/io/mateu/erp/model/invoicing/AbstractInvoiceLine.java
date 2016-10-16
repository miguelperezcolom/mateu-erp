package io.mateu.erp.model.invoicing;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Table(name = "MA_INVOICELINE")
@Getter
@Setter
public abstract class AbstractInvoiceLine {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO, generator="invoiceline_seq_gen")
    @SequenceGenerator(name="invoiceline_seq_gen", sequenceName="INL_SEQ")
    @Column(name = "INLIDINL")
    private long id;

    @ManyToOne
    @JoinColumn(name = "INLIDINV")
    private Invoice invoice;

    @Column(name = "INLSUBJECT")
    private String subject;

    @Column(name = "INLQUANTITY")
    private double quantity;

    @Column(name = "INLPRICE")
    private double price;

    @Column(name = "INLDISCOUNTPERCENT")
    private double discountPercent;

    @Column(name = "INLTOTAL")
    private double total;


}
