package io.mateu.erp.model.booking.parts;

import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.product.hotel.Hotel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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


    public HotelBooking() {
        setIcons(FontAwesome.HOTEL.getHtml());
    }


    @Override
    public void validate() throws Exception {

    }

    @Override
    protected void generateServices(EntityManager em) {

    }

    @Override
    public void priceServices() {

    }
}
