package io.mateu.erp.model.booking.parts;

import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.PriceBreakdownItem;
import io.mateu.erp.model.booking.freetext.FreeTextService;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.revenue.ProductLine;
import io.mateu.erp.model.thirdParties.Integration;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.Position;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Getter@Setter
public class ThirdPartyHotelBooking extends Booking {

    @NotNull
    @ManyToOne
    @Position(3)
    private Integration integration;

    @NotEmpty
    @Position(4)
    private String hotelName;

    @Position(5)
    private String hotelCategory;

    @Position(6)
    private String hotelAddress;

    @Position(7)
    private String hotelCity;

    @Position(8)
    private String hotelState;

    @Position(9)
    private String hotelCountry;

    @Position(10)
    private String hotelDataSheetUrl;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "booking")
    @Position(11)
    private List<ThirdPartyHotelBookingLine> lines = new ArrayList<>();


    public boolean isStartVisible() { return false; }
    public boolean isEndVisible() { return false; }
    public boolean isAdultsVisible() { return false; }
    public boolean isChildrenVisible() { return false; }
    public boolean isAgesVisible() { return false; }



    public ThirdPartyHotelBooking() {
        setIcons(FontAwesome.HOTEL.getHtml() + FontAwesome.LINK.getHtml());
    }


    @Override
    protected void completeChangeSignatureData(Map<String, String> data) {

    }

    @Override
    public void validate() throws Exception {

    }

    @Override
    public void generateServices(EntityManager em) {
        FreeTextService s = null;
        if (getServices().size() > 0) {
            s = (FreeTextService) getServices().get(0);
        }
        if (s == null) {
            getServices().add(s = new FreeTextService());
            s.setBooking(this);
            s.setAudit(new Audit(MDD.getCurrentUser()));
        }
        s.setOffice(integration.getOffice());
        s.setFinish(getEnd());
        s.setStart(getStart());
        s.setText(getDescription());
        s.setDeliveryDate(getStart());
        s.setReturnDate(getEnd());
        em.merge(s);

    }

    @Override
    protected ProductLine getEffectiveProductLine() {
        return getIntegration().getProductLine();
    }

    public String getDescription() {
        String s = "";
        s += hotelName;
        for (ThirdPartyHotelBookingLine l : getLines()) {
            s += "\n";
            s += l.getRooms() + " x " + l.getRoomName() + " | " + l.getBoardName() + " x " + l.getAdultsPerRoon() + "AD + " + l.getChildrenPerRoom() + "CH";
        }
        return s;
    }

    @Override
    public void priceServices(EntityManager em, List<PriceBreakdownItem> breakdown) {

    }

    @Override
    protected BillingConcept getDefaultBillingConcept(EntityManager em) {
        return AppConfig.get(em).getBillingConceptForHotel();
    }

    @Override
    protected void completeSignature(Map<String, Object> m) {

    }
}
