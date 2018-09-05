package io.mateu.erp.model.booking.parts;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.tickets.Ticket;
import io.mateu.erp.model.booking.tickets.TicketStatus;
import io.mateu.mdd.core.annotations.TextArea;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
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
    private Ticket ticket;


    public TicketBooking() {
        setStart(LocalDate.now());
        setEnd(LocalDate.now());
    }


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


    @PrePersist
    public void pre() throws Exception {
        if (!TicketStatus.LIVE.equals(ticket.getStatus())) throw new Exception("Ticket must be live");
    }
}
