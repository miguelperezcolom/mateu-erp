package io.mateu.erp.model.booking.parts;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.product.generic.GenericProduct;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
@Getter@Setter
public class GenericBooking extends Booking {

    @ManyToOne@NotNull
    private GenericProduct product;


    private int units;


}
