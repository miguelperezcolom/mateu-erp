package io.mateu.erp.model.booking.parts;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.ManagedEvent;
import io.mateu.mdd.core.annotations.KPI;
import io.mateu.mdd.core.annotations.NotWhenCreating;
import io.mateu.mdd.core.annotations.Output;
import io.mateu.mdd.core.annotations.Position;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter@Setter
public abstract class TourBooking extends Booking {

    @ManyToOne
    @NotWhenCreating
    @KPI
    private ManagedEvent managedEvent;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    @Position(13)
    private List<TourBookingExtra> extras = new ArrayList<>();

}
