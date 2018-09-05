package io.mateu.erp.model.booking.parts;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.product.tour.Circuit;
import io.mateu.erp.model.product.tour.Excursion;
import io.mateu.erp.model.product.tour.TourShift;
import io.mateu.erp.model.product.tour.TourVariant;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
@Getter@Setter
public class ExcursionBooking extends Booking {

    @ManyToOne
    @NotNull
    private Excursion excursion;


    @ManyToOne
    @NotNull
    private TourVariant variant;

    @ManyToOne
    @NotNull
    private TourShift shift;


}
