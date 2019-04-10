package io.mateu.erp.model.payments;

import io.mateu.erp.model.commissions.CommissionSettlement;
import io.mateu.erp.model.organization.PointOfSaleSettlement;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
@Getter@Setter
public class CommissionSettlementPaymentAllocation extends AbstractPaymentAllocation {

    @ManyToOne@NotNull
    private CommissionSettlement commissionSettlement;

    public void setCommissionSettlement(CommissionSettlement commissionSettlement) {
        this.commissionSettlement = commissionSettlement;
        if (commissionSettlement != null) commissionSettlement.setUpdatePending(true);
    }
}
