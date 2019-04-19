package io.mateu.erp.model.booking.parts;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.ManagedEvent;
import io.mateu.mdd.core.annotations.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter@Setter
public abstract class TourBooking extends Booking {

    @ManyToOne
    @NotWhenCreating
    @Output
    @Position(14)
    private ManagedEvent managedEvent;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    @UseLinkToListView
    @Position(15)
    private List<TourBookingExtra> extras = new ArrayList<>();


    private LocalDateTime checkTime;


}
