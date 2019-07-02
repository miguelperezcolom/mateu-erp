package io.mateu.erp.model.payments;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.invoicing.IssuedInvoice;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotNull;

@Entity
@Getter@Setter
public class BookingPaymentAllocation extends AbstractPaymentAllocation {

    @ManyToOne@NotNull
    private Booking booking;


    @ManyToOne
    private IssuedInvoice invoice;

    public void setBooking(Booking booking) {
        this.booking = booking;
        if (booking != null) booking.updateBalance();
    }

    @PrePersist@PreUpdate
    public void pre() {
        setDescription(booking != null?"Booking " + booking.getId() + "(" + booking.getAgency().getName() + " - " + booking.getAgencyReference() + ")":"---");
    }
}
