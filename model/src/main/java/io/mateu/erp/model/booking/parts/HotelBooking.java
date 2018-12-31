package io.mateu.erp.model.booking.parts;

import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.ValidationStatus;
import io.mateu.erp.model.booking.hotel.HotelService;
import io.mateu.erp.model.booking.hotel.HotelServiceLine;
import io.mateu.erp.model.financials.Amount;
import io.mateu.erp.model.invoicing.BookingCharge;
import io.mateu.erp.model.invoicing.ChargeType;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.Position;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.User;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;
import org.javamoney.moneta.FastMoney;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

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
    public void generateServices(EntityManager em) {
        HotelService s = null;
        if (getServices().size() > 0) {
            s = (HotelService) getServices().get(0);
            s.getLines().clear();
        }
        if (s == null) {
            getServices().add(s = new HotelService());
            s.setBooking(this);
            s.setAudit(new Audit(MDD.getCurrentUser()));
        }
        s.setOffice(hotel.getOffice());
        s.setFinish(getEnd());
        s.setStart(getStart());
        s.setHotel(hotel);
        for (HotelBookingLine e : getLines()) s.getLines().add(new HotelServiceLine(s, e));
        s.setSpecialRequests(getSpecialRequests());
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
            getServiceCharges().add(c = new BookingCharge());
            c.setAudit(new Audit(getAudit().getModifiedBy()));
            c.setTotal(new Amount(FastMoney.of(l.getValue(), "EUR")));

            c.setText(l.toSimpleString());

            c.setPartner(getAgency());

            c.setType(ChargeType.SALE);
            c.setBooking(this);
            getServiceCharges().add(c);

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
        boolean available = true;
        boolean valued = true;
        double v = 0;
        for (HotelBookingLine l : lines) {
            if (l.getStart() != null && (d0 == null  || d0.isBefore(l.getStart()))) d0 = l.getStart();
            if (l.getEnd() != null && (d1 == null  || d1.isAfter(l.getEnd()))) d1 = l.getEnd();
            active |= l.isActive();
            valid &= l.isEnoughRooms() && l.isMinStay() && l.isOccupationOk() && l.isRelease() && l.isSalesClosed() && l.isWeekDays();
            available &= l.isAvailable();
            valued &= l.isValued();

            v += l.getValue();
        }
        setStart(d0);
        setEnd(d1);
        setActive(active);
        setValidationStatus(valid?ValidationStatus.VALID:ValidationStatus.INVALID);
        setAvailable(available);
        if (!isValueOverrided()) {
            setValued(valued);
            setTotalNetValue(Helper.roundEuros(v));
            setTotalValue(Helper.roundEuros(v));
        }
        setUpdatePending(true);
    }




    @PostLoad
    public void postload() {
        lines.forEach(l -> l.postLoad());
        super.postload();
    }


    @PostUpdate@PostPersist@PostRemove
    public void post() {
        lines.forEach(l -> l.post());
        super.post();
    }

    @Override
    public String getParticularDescription() {
        return getHotel()!= null?getHotel().getName():"No hotel";
    }


    @Override
    public String getServiceDataHtml() {

        DecimalFormat df = new DecimalFormat("###,###,###,###,##0.00");

        String h = "";
        h +=
                "                <table width=\"100%\">\n" +
                "                    <tr><td width=\"50%\" style=\"vertical-align: top;\">\n" +
                "\n" +
                "                        <table>\n" +
                "                            <tr><td width='140px'>Su referencia:</td><td>" + getAgencyReference() + "</td></tr>\n" +
                "                            <tr><td>Llegada:</td><td>" + getStart() + "</td></tr>\n" +
                "                            <tr><td>Salida:</td><td>" + getEnd() + "</td></tr>\n" +
                "                            <tr><td>Noches:</td><td>" + DAYS.between(getStart(), getEnd().minusDays(1)) + "</td></tr>\n" +
                "                        </table>\n" +
                "\n" +
                "                    </td><td>\n" +
                "\n" +
                "                        <h3>" + hotel + "</h3>\n" +
                "\n";

        int pos = 0;
        for (HotelBookingLine l : lines) {
            h +=
                    "                        <h4>Estancia " + pos++ + "</h4>\n" +
                    "                        <table>\n" +
                    "                            <tr><td width='140px'>Nº habitaciones:</td><td>" + l.getRooms() + "</td></tr>\n" +
                    "                            <tr><td>Tipo de habitación:</td><td>" + l.getRoom().getType() + ": " + df.format(l.getValue()) + " &euro;</td></tr>\n" +
                    "                            <tr><td>Régimen:</td><td>" + l.getBoard().getType() + "</td></tr>\n" +
                    "                            <tr><td>Llegada:</td><td>" + l.getStart() + "</td></tr>\n" +
                    "                            <tr><td>Salida:</td><td>" + l.getEnd() + "</td></tr>\n" +
                    "                            <tr><td>Noches:</td><td>" + DAYS.between(l.getStart(), l.getEnd().minusDays(1)) + "</td></tr>\n" +
                    "                            <tr><td>Adultos/hab.:</td><td>" + l.getAdultsPerRoon() + "</td></tr>\n" +
                    "                            <tr><td>Niños/hab.</td><td>" + l.getChildrenPerRoom() + "</td></tr>\n" +
//                    "                            <tr><td>Oferta:</td><td>EB 15%</td></tr>\n" +
                    "                        </table>\n" +
                    "\n";
        }

        h +=
                "                    </td></tr>\n" +
                "                </table>";
        return h;
    }

    @Override
    public String getProductDataHtml() {
        String h = "";
        h += "                <h2 style=\"margin-bottom: 0px;\">DATOS DEL ALOJAMIENTO</h2>\n" +
                "\n" +
                "                <hr style=\"margin-top: 0px;\">\n" +
                "\n" +
                "                <table width='100%'>\n" +
                "                    <tr><td width='33%'>Nombre del hotel</td><td>" + hotel.getName() + "</td></tr>\n" +
                "                    <tr><td>Dirección</td><td>" + hotel.getAddress() + " " + hotel.getZone() + " " + hotel.getZone().getDestination() + " " + hotel.getZip() + "</td></tr>\n" +
                "                    <tr><td>Teléfono</td><td>" + hotel.getTelephone() + "</td></tr>\n" +
                "                    <tr><td>E-mail</td><td>" + hotel.getEmail() + "</td></tr>\n" +
                "                    <tr><td>Localización GPS</td><td>" + hotel.getLat() + ", " + hotel.getLon() + "</td></tr>\n" +
                "                </table>\n" +
                "\n" +
                "\n" +
                "    <p></p><p></p><p></p>";
        return h;
    }

    @Override
    public String getPaymentRemarksHtml() {
        String h = "";

        h += hotel.getZone().getDestination().getPaymentRemarks();

        return h;
    }
}