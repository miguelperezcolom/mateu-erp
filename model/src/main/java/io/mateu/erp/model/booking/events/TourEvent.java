package io.mateu.erp.model.booking.events;

import io.mateu.erp.model.booking.ManagedEvent;
import io.mateu.erp.model.product.tour.Tour;
import io.mateu.erp.model.product.tour.TourShift;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
@Getter@Setter
public class TourEvent extends ManagedEvent {

    @ManyToOne
    @NotNull
    private Tour tour;

    @ManyToOne
    @NotNull
    private TourShift shift;

    private int totalPax;

    private int totalVehicles;

}
