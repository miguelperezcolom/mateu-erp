package io.mateu.erp.model.booking.parts;

import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.tickets.Ticket;
import io.mateu.erp.model.booking.tickets.TicketStatus;
import io.mateu.erp.model.product.Variant;
import io.mateu.erp.model.product.tour.Excursion;
import io.mateu.erp.model.product.tour.TourShift;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.mdd.core.annotations.Position;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Map;

@Entity
@Getter@Setter
public class TicketBooking extends Booking {

    @NotNull
    @ManyToOne
    @Position(13)
    private Ticket ticket;

    @Position(14)
    @ManyToOne@NotNull
    private Excursion excursion;

    @Position(15)
    @ManyToOne@NotNull
    private Variant variant;

    @Position(16)
    @ManyToOne@NotNull
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
    public void priceServices(EntityManager em) {

    }

    @Override
    protected void completeSignature(Map<String, Object> m) {

    }
}
