package io.mateu.erp.model.invoicing;

import io.mateu.erp.model.booking.Booking;
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
public class BookingInvoiceLine extends AbstractInvoiceLine {

    @ManyToOne
    @NotNull
    private Booking booking;


    public BookingInvoiceLine() {
        super();
    }

    public BookingInvoiceLine(Invoice invoice, BookingCharge c) {
        super(invoice);

        booking = c.getBooking();

        setPrice(c.getTotal().getValue());
        setQuantity(1);
        setTotal(getPrice());
        setSubject(c.getText());
    }
}
