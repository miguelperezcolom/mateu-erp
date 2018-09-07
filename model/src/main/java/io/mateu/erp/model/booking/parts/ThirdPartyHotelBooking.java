package io.mateu.erp.model.booking.parts;

import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.thirdParties.Integration;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter@Setter
public class ThirdPartyHotelBooking extends Booking {

    @NotNull
    @ManyToOne
    private Integration integration;

    private String hotelName;

    private String hotelCategory;

    private String hotelAddress;

    private String hotelCity;

    private String hotelState;

    private String hotelCountry;

    private String hotelDataSheetUrl;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "booking")
    private List<ThirdPartyHotelBookingLine> lines = new ArrayList<>();


    public ThirdPartyHotelBooking() {
        setIcons(FontAwesome.HOTEL.getHtml() + FontAwesome.LINK.getHtml());
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
