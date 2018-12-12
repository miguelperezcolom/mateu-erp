package io.mateu.erp.model.booking.parts;

import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import io.mateu.erp.dispo.Helper;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.ValidationStatus;
import io.mateu.erp.model.booking.generic.GenericService;
import io.mateu.erp.model.booking.generic.GenericServiceExtra;
import io.mateu.erp.model.booking.hotel.HotelService;
import io.mateu.erp.model.booking.hotel.HotelServiceLine;
import io.mateu.erp.model.financials.Amount;
import io.mateu.erp.model.invoicing.BookingCharge;
import io.mateu.erp.model.invoicing.ChargeType;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.FullWidth;
import io.mateu.mdd.core.annotations.Output;
import io.mateu.mdd.core.annotations.Position;
import io.mateu.mdd.core.annotations.UseLinkToListView;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.User;
import lombok.Getter;
import lombok.Setter;
import org.javamoney.moneta.FastMoney;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Getter@Setter
public class HotelBooking extends Booking {

    @ManyToOne
    @NotNull
    @Position(8)
    private Hotel hotel;

    public boolean isStartVisible() {
        return false;
    }

    public boolean isEndVisible() {
        return false;
    }

    public boolean isAdultsVisible() {
        return false;
    }

    public boolean isChildrenVisible() {
        return false;
    }

    public boolean isAgesVisible() {
        return false;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "booking")
    @Position(9)
    private List<HotelBookingLine> lines = new ArrayList<>();

    public String getLinesHtml() {

        String h = "<div class='lines'>";
        for (HotelBookingLine l : lines) {
            h += "<div class='line" + (l.isActive() ? "" : " cancelled") + "'>";
            h += l.toString();
            h += "</div>";
        }
        h += "</div>";

        return h;
    }


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

    @Override
    public void cancel(EntityManager em, User u) {
        for (HotelBookingLine l : lines) l.setActive(false);
        super.cancel(em, u);
    }

    /*
    @PrePersist
    @PreUpdate
    public void pre() throws Error {
        super.pre();
    }
    */

    @Override
    public void createCharges(EntityManager em) throws Throwable {
        for (HotelBookingLine l : lines) if (l.getContract() != null) {
            BookingCharge c;
            getCharges().add(c = new BookingCharge());
            c.setAudit(new Audit(getAudit().getModifiedBy()));
            c.setTotal(new Amount(FastMoney.of(l.getValue(), "EUR")));

            c.setText(l.toString());

            c.setPartner(getAgency());

            c.setType(ChargeType.SALE);
            c.setBooking(this);
            getCharges().add(c);

            c.setInvoice(null);

            c.setBillingConcept(l.getContract().getBillingConcept());

            em.persist(c);
        }
    }

    public void updateData() {
        LocalDate d0 = null;
        LocalDate d1 = null;
        boolean active = false;
        boolean valid = true;
        double v = 0;
        for (HotelBookingLine l : lines) {
            if (l.getStart() != null && (d0 == null  || d0.isBefore(l.getStart()))) d0 = l.getStart();
            if (l.getEnd() != null && (d1 == null  || d1.isAfter(l.getEnd()))) d1 = l.getEnd();
            active |= l.isActive();
            valid &= l.isEnoughRooms() && l.isMinStay() && l.isOccupationOk() && l.isRelease() && l.isSalesClosed() && l.isWeekDays();
            v += l.getValue();
        }
        setStart(d0);
        setEnd(d1);
        setActive(active);
        setValidationStatus(valid?ValidationStatus.VALID:ValidationStatus.INVALID);
        setTotalNetValue(Helper.roundEuros(v));
        setTotalValue(Helper.roundEuros(v));
    }

}