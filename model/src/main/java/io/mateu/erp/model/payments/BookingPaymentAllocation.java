package io.mateu.erp.model.payments;

import io.mateu.erp.model.booking.Booking;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter@Setter
public class BookingPaymentAllocation extends AbstractPaymentAllocation {

    @ManyToOne
    private Booking booking;

}
