package io.mateu.erp.model.booking.tickets;

import io.mateu.mdd.core.annotations.Output;
import io.mateu.mdd.core.annotations.UseIdToSelect;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
@Getter@Setter
@UseIdToSelect
public class Ticket {

    @Id
    private String id;

    @NotNull@Output@ManyToOne
    private TicketBook book;

    @NotNull
    private TicketStatus status = TicketStatus.LIVE;


    @Override
    public String toString() {
        return "" + ((getBook() != null)?((getBook().getProduct() != null)?getBook().getProduct().getName():"No product"):"No book") + " - " + getStatus();
    }
}
