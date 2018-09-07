package io.mateu.erp.model.booking.parts;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.ManagedEvent;
import io.mateu.mdd.core.annotations.KPI;
import io.mateu.mdd.core.annotations.NotWhenCreating;
import io.mateu.mdd.core.annotations.Output;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter@Setter
public abstract class TourBooking extends Booking {

    @ManyToOne
    @NotWhenCreating
    @KPI
    private ManagedEvent managedEvent;

}
