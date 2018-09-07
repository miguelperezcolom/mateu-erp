package io.mateu.erp.model.invoicing;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.text.DecimalFormat;

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


    public AbstractInvoiceLine(Invoice invoice) {
        this.invoice = invoice;
    }

    public AbstractInvoiceLine() {

    }


    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("##,###,###,###,###.00");
        return "<div style='min-width: 300px;display: inline-block;'>" + subject + "</div><div style='text-align:right; width: 200px;display: inline-block;'>" + df.format(total) + "</div>";
    }
}
