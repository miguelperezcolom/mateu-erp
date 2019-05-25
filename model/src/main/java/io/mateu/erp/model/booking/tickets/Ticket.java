package io.mateu.erp.model.booking.tickets;

import io.mateu.erp.model.booking.Booking;
import io.mateu.mdd.core.annotations.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Entity
@Getter@Setter
@UseIdToSelect
@NewNotAllowed@Indelible
public class Ticket {

    @Id
    private String id;

    @NotNull@Output@ManyToOne@MainSearchFilter
    private TicketBook book;

    @NotNull@MainSearchFilter
    private TicketStatus status = TicketStatus.LIVE;

    @TextArea
    private String comments;

    @OneToOne@Output
    private Booking booking;


    @Override
    public String toString() {
        return "" + ((getBook() != null)?"Book " + getBook().getId():"No book") + " - " + getStatus();
    }
}
