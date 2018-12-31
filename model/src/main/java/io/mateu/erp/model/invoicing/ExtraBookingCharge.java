package io.mateu.erp.model.invoicing;


import io.mateu.erp.model.booking.Booking;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
@Getter@Setter
public class ExtraBookingCharge extends BookingCharge {

    public ExtraBookingCharge() {
        super(); setExtra(true);
    }

}
