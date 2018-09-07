package io.mateu.erp.model.invoicing;

import io.mateu.erp.model.booking.File;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Getter@Setter
public class BookingInvoiceLine extends AbstractInvoiceLine {

    @ManyToOne
    private File file;


    public BookingInvoiceLine() {
        super();
    }

    public BookingInvoiceLine(Invoice invoice, Charge c) {
        super(invoice);

        file = c.getFile();

        setPrice(c.getTotal().getValue());
        setQuantity(1);
        setTotal(getPrice());
        setSubject(c.getText());
    }
}
