package io.mateu.erp.model.payments;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.QuotationRequest;
import io.mateu.erp.model.invoicing.IssuedInvoice;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
@Getter@Setter
public class QuotationRequestPaymentAllocation extends AbstractPaymentAllocation {

    @ManyToOne@NotNull
    private QuotationRequest quotationRequest;

    public void setQuotationRequest(QuotationRequest quotationRequest) {
        this.quotationRequest = quotationRequest;
        if (quotationRequest != null) quotationRequest.setForcePre(true);
    }
}
