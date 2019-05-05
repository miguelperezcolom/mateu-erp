package io.mateu.erp.model.booking.events;

import io.mateu.erp.model.booking.ManagedEvent;
import io.mateu.erp.model.product.tour.Tour;
import io.mateu.erp.model.product.tour.ExcursionShift;
import io.mateu.mdd.core.annotations.SearchFilter;
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
    @SearchFilter
    private Tour tour;

    @ManyToOne
    @NotNull
    @SearchFilter
    private ExcursionShift shift;

    private int totalPax;

    private int totalVehicles;

}
