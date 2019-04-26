package io.mateu.erp.model.booking.parts;

import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.PriceBreakdownItem;
import io.mateu.erp.model.booking.tickets.Ticket;
import io.mateu.erp.model.booking.tickets.TicketStatus;
import io.mateu.erp.model.product.Variant;
import io.mateu.erp.model.product.tour.Excursion;
import io.mateu.erp.model.product.tour.ExcursionLanguage;
import io.mateu.erp.model.product.tour.ExcursionShift;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.erp.model.revenue.ProductLine;
import io.mateu.mdd.core.annotations.Position;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Getter@Setter
public class TicketBooking extends Booking {

    @NotNull
    @ManyToOne
    @Position(13)
    private Ticket ticket;


    @ManyToOne
    @NotNull
    @Position(14)
    private Excursion excursion;


    @ManyToOne
    @NotNull
    @Position(15)
    private Variant variant;

    public DataProvider getVariantDataProvider() {
        return new ListDataProvider(excursion != null?excursion.getVariants():new ArrayList());
    }

    @ManyToOne
    @NotNull
    @Position(16)
    private ExcursionShift shift;

    public DataProvider getShiftDataProvider() {
        return new ListDataProvider(excursion != null?excursion.getShifts():new ArrayList());
    }

    @ManyToOne
    @NotNull
    @Position(17)
    private ExcursionLanguage language;

    public DataProvider getLanguageDataProvider() {
        return new ListDataProvider(shift != null?shift.getLanguages():new ArrayList());
    }

    @ManyToOne
    @Position(18)
    private TransferPoint pickupPoint;

    @Position(19)
    private int pickupTime;


    @Position(20)
    private String roomNumber;


    public TicketBooking() {
        setStart(LocalDate.now());
        setEnd(LocalDate.now());
        setIcons(FontAwesome.TICKET.getHtml());
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


    @Override
    protected void completeChangeSignatureData(Map<String, String> data) {

    }

    @PrePersist
    public void pre() throws Error {
        if (!TicketStatus.LIVE.equals(ticket.getStatus())) throw new Error("Ticket must be live");
        super.pre();
    }

    @Override
    public void validate() throws Exception {

    }


    @Override
    public void generateServices(EntityManager em) {

    }

    @Override
    protected ProductLine getEffectiveProductLine() {
        return getExcursion() != null?getExcursion().getProductLine():null;
    }

    @Override
    public void priceServices(EntityManager em, List<PriceBreakdownItem> breakdown) {

    }

    @Override
    protected void completeSignature(Map<String, Object> m) {

    }
}
