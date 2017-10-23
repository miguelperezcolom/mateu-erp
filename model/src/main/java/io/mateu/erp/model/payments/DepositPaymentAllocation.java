package io.mateu.erp.model.payments;

import io.mateu.erp.model.invoicing.Invoice;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter@Setter
public class DepositPaymentAllocation extends AbstractPaymentAllocation {

    @ManyToOne
    private Deposit deposit;

}
