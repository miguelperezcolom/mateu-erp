package io.mateu.erp.model.cms;

import io.mateu.erp.model.authentication.AuthToken;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.mdd.server.annotations.Owned;
import io.mateu.ui.mdd.server.annotations.Tab;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class HotelWebSite extends Website {

    @Tab("Hotel")
    @ManyToOne
    private AuthToken authToken;

    @Tab("Home")
    @OneToOne
    @Owned
    private HotelHomePage home;

    @Tab("Offers")
    @OneToOne
    @Owned
    private HotelOffersPage offers;

    @Tab("Services")
    @OneToOne
    @Owned
    private HotelServicesPage services;


    @Tab("Contact")
    @OneToOne
    @Owned
    private HotelContactPage contact;


    @Tab("Booking")
    @OneToOne
    @Owned
    private HotelBookingPage booking;

    @Override
    public Data toData() {
        return super.toData();
    }
}
