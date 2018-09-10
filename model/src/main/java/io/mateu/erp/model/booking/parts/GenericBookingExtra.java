package io.mateu.erp.model.booking.parts;

import io.mateu.erp.model.booking.File;
import io.mateu.erp.model.product.generic.Extra;
import io.mateu.mdd.core.annotations.Order;
import io.mateu.mdd.core.annotations.SearchFilter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class GenericBookingExtra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private GenericBooking booking;

    @ManyToOne
    @NotNull
    private Extra extra;

    private int units;
}
