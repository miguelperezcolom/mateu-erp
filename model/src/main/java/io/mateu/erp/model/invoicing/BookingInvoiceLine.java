package io.mateu.erp.model.invoicing;

import io.mateu.erp.model.booking.Booking;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
public class BookingInvoiceLine extends AbstractInvoiceLine {

    @ManyToOne
    private Booking booking;

}
