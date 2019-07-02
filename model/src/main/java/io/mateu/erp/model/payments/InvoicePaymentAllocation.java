package io.mateu.erp.model.payments;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.invoicing.Invoice;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotNull;

@Entity
@Getter@Setter
public class InvoicePaymentAllocation extends AbstractPaymentAllocation {

    @ManyToOne@NotNull
    private Invoice invoice;

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
        if (invoice != null) invoice.updateBalance();
    }

    @PrePersist
    @PreUpdate
    public void pre() {
        setDescription(invoice != null?"Invoice " + invoice.getNumber():"---");
    }


}
