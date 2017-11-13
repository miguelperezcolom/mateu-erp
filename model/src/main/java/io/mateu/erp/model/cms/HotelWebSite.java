package io.mateu.erp.model.cms;

import io.mateu.erp.model.authentication.AuthToken;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.mdd.server.annotations.Tab;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class HotelWebSite extends Website {

    @Tab("Hotel")
    @ManyToOne
    private AuthToken authToken;

    @Tab("Home")
    @Embedded
    private HotelHomePage home = new HotelHomePage();

    @Tab("Offers")
    @Embedded
    private HotelOffersPage offers = new HotelOffersPage();

    @Tab("Services")
    @Embedded
    private HotelServicesPage services = new HotelServicesPage();


    @Tab("Contact")
    @Embedded
    private HotelContactPage contact = new HotelContactPage();


    @Tab("Booking")
    @Embedded
    private HotelBookingPage booking = new HotelBookingPage();

    @Override
    public Data toData() {
        return super.toData();
    }
}
