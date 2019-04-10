package io.mateu.erp.model.invoicing;

import io.mateu.erp.model.booking.Booking;
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
public class BookingInvoiceLine extends AbstractInvoiceLine {

    @ManyToOne
    @NotNull
    private BookingCharge charge;


    public BookingInvoiceLine() {
        super();
    }

    public BookingInvoiceLine(Invoice invoice, BookingCharge c) {
        super(invoice);

        charge = c;

        setPrice(c.getTotal());
        setQuantity(1);
        setTotal(getPrice());
        setSubject(c.getText());
    }
}
