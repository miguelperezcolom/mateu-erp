package io.mateu.erp.model.booking.parts;

import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.freetext.FreeTextService;
import io.mateu.erp.model.booking.hotel.HotelService;
import io.mateu.erp.model.booking.hotel.HotelServiceLine;
import io.mateu.erp.model.thirdParties.Integration;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter@Setter
public class ThirdPartyHotelBooking extends Booking {

    @NotNull
    @ManyToOne
    private Integration integration;

    @NotEmpty
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
        FreeTextService s = null;
        if (getServices().size() > 0) {
            s = (FreeTextService) getServices().get(0);
        }
        if (s == null) {
            getServices().add(s = new FreeTextService());
            s.setBooking(this);
            s.setFile(getFile());
            getFile().getServices().add(s);
            s.setAudit(new Audit(em.find(User.class, MDD.getUserData().getLogin())));
        }
        s.setOffice(integration.getOffice());
        s.setFinish(getEnd());
        s.setStart(getStart());
        s.setText(getDescription());
        s.setDeliveryDate(getStart());
        s.setReturnDate(getEnd());
        em.merge(s);

    }

    private String getDescription() {
        String s = "";
        s += hotelName;
        for (ThirdPartyHotelBookingLine l : getLines()) {
            s += "\n";
            s += l.getRooms() + " x " + l.getRoomName() + " | " + l.getBoardName() + " x " + l.getAdultsPerRoon() + "AD + " + l.getChildrenPerRoom() + "CH";
        }
        return s;
    }

    @Override
    public void priceServices() {

    }
}
