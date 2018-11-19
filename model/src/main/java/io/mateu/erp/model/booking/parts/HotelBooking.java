package io.mateu.erp.model.booking.parts;

import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.generic.GenericService;
import io.mateu.erp.model.booking.generic.GenericServiceExtra;
import io.mateu.erp.model.booking.hotel.HotelService;
import io.mateu.erp.model.booking.hotel.HotelServiceLine;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.FullWidth;
import io.mateu.mdd.core.annotations.Output;
import io.mateu.mdd.core.annotations.Position;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.User;
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
    @Position(8)
    private Hotel hotel;

    @Output@Position(9)
    private transient String description = "2 adults and 1 children from 2018-06-01 to 06-15 (14 nights)";


    public boolean isStartVisible() { return false; }
    public boolean isEndVisible() { return false; }
    public boolean isAdultsVisible() { return false; }
    public boolean isChildrenVisible() { return false; }
    public boolean isAgesVisible() { return false; }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "booking")
    @Position(10)
    private List<HotelBookingLine> lines = new ArrayList<>();


    public HotelBooking() {
        setIcons(FontAwesome.HOTEL.getHtml());
    }


    @Override
    public void validate() throws Exception {

    }

    @Override
    protected void generateServices(EntityManager em) {
        HotelService s = null;
        if (getServices().size() > 0) {
            s = (HotelService) getServices().get(0);
            s.getLines().clear();
        }
        if (s == null) {
            getServices().add(s = new HotelService());
            s.setBooking(this);
            s.setAudit(new Audit(em.find(User.class, MDD.getUserData().getLogin())));
        }
        s.setOffice(hotel.getOffice());
        s.setFinish(getEnd());
        s.setStart(getStart());
        s.setHotel(hotel);
        for (HotelBookingLine e : getLines()) s.getLines().add(new HotelServiceLine(s, e));
        em.merge(s);
    }

    @Override
    public void priceServices() {

    }
}
