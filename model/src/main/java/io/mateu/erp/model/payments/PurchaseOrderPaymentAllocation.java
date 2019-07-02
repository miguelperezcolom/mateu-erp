package io.mateu.erp.model.payments;

import io.mateu.erp.model.booking.File;
import io.mateu.erp.model.booking.PurchaseOrder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter@Setter
public class PurchaseOrderPaymentAllocation extends AbstractPaymentAllocation {

    @ManyToOne@NotNull
    private PurchaseOrder purchaseOrder;

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
        if (purchaseOrder != null) purchaseOrder.setUpdateRqTime(LocalDateTime.now());
    }


    @PrePersist
    @PreUpdate
    public void pre() {
        setDescription(purchaseOrder != null?"PO " + purchaseOrder.getId():"---");
    }

}
