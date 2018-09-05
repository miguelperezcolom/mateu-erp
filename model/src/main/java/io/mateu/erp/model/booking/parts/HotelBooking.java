package io.mateu.erp.model.booking.parts;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.product.hotel.Hotel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter@Setter
public class HotelBooking extends Booking {

    @ManyToOne
    @NotNull
    private Hotel hotel;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "booking")
    private List<HotelBookingLine> lines = new ArrayList<>();

}
