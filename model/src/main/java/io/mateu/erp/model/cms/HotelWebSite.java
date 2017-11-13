package io.mateu.erp.model.cms;

import com.google.common.io.Files;
import io.mateu.erp.model.authentication.AuthToken;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.mdd.server.annotations.Owned;
import io.mateu.ui.mdd.server.annotations.Tab;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.File;

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

    @Override
    public void createContentFiles(EntityManager em, File contentDir) throws Throwable {

        {
            java.io.File f = new java.io.File(contentDir.getAbsolutePath() + java.io.File.separator + "index.md");

            if (!f.exists()) Files.write(getHome().toMd().toString().getBytes(), f);
        }

        {
            java.io.File f = new java.io.File(contentDir.getAbsolutePath() + java.io.File.separator + "offers.md");

            if (!f.exists()) Files.write(getOffers().toMd().toString().getBytes(), f);
        }

        {
            java.io.File f = new java.io.File(contentDir.getAbsolutePath() + java.io.File.separator + "services.md");

            if (!f.exists()) Files.write(getServices().toMd().toString().getBytes(), f);
        }

        {
            java.io.File f = new java.io.File(contentDir.getAbsolutePath() + java.io.File.separator + "contact.md");

            if (!f.exists()) Files.write(getContact().toMd().toString().getBytes(), f);
        }

        {
            java.io.File f = new java.io.File(contentDir.getAbsolutePath() + java.io.File.separator + "booking.md");

            if (!f.exists()) Files.write(getBooking().toMd().toString().getBytes(), f);
        }

    }
}
