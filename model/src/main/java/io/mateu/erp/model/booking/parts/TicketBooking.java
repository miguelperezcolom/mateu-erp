package io.mateu.erp.model.booking.parts;

import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.tickets.Ticket;
import io.mateu.erp.model.booking.tickets.TicketStatus;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.product.tour.Excursion;
import io.mateu.erp.model.product.tour.TourShift;
import io.mateu.erp.model.product.tour.TourVariant;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.mdd.core.annotations.Position;
import io.mateu.mdd.core.annotations.TextArea;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Getter@Setter
public class TicketBooking extends Booking {

    @NotNull
    @ManyToOne
    @Position(13)
    private Ticket ticket;

    @Position(14)
    private Excursion excursion;

    @Position(15)
    private TourVariant variant;

    @Position(16)
    private TourShift shift;

    @ManyToOne
    @Position(17)
    private TransferPoint pickupPoint;

    @Position(18)
    private int pickupTime;


    @Position(19)
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


    @PrePersist
    public void pre() throws Error {
        if (!TicketStatus.LIVE.equals(ticket.getStatus())) throw new Error("Ticket must be live");
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
