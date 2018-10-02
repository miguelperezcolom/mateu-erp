package io.mateu.erp.model.booking.parts;

import io.mateu.erp.model.product.tour.TourExtra;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity@Getter@Setter
public class TourBookingExtra {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne@NotNull
    private TourBooking booking;

    @ManyToOne
    @NotNull
    private TourExtra extra;

    private int units;

}
