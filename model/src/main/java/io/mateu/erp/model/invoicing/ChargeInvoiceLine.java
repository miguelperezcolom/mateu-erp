package io.mateu.erp.model.invoicing;

import io.mateu.erp.model.booking.File;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Getter@Setter
public class ChargeInvoiceLine extends AbstractInvoiceLine {

    @ManyToOne
    @NotNull
    private Charge charge;


    public ChargeInvoiceLine() {
        super();
    }

    public ChargeInvoiceLine(Invoice invoice, Charge charge) {
        super(invoice);

        this.charge = charge;

        setPrice(charge.getTotal().getValue());
        setQuantity(1);
        setTotal(getPrice());
        setSubject(charge.getText());
    }
}
