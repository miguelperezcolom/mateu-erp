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
public class PointOfSaleSettlementPaymentAllocation extends AbstractPaymentAllocation {

    @ManyToOne@NotNull
    private PointOfSaleSettlement pointOfSaleSettlement;

    public void setPointOfSaleSettlement(PointOfSaleSettlement pointOfSaleSettlement) {
        this.pointOfSaleSettlement = pointOfSaleSettlement;
        if (pointOfSaleSettlement != null) pointOfSaleSettlement.setUpdatePending(true);
    }
}
