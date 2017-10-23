package io.mateu.erp.model.payments;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.invoicing.Invoice;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter@Setter
public class InvoicePaymentAllocation extends AbstractPaymentAllocation {

    @ManyToOne
    private Invoice invoice;

}
