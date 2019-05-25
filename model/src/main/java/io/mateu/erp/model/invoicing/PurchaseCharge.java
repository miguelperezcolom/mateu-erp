package io.mateu.erp.model.invoicing;


import com.vaadin.data.provider.DataProvider;
import io.mateu.erp.model.booking.PurchaseOrder;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.partners.Provider;
import io.mateu.mdd.core.annotations.DependsOn;
import io.mateu.mdd.core.annotations.Output;
import io.mateu.mdd.core.dataProviders.JPQLListDataProvider;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
@Getter@Setter
public class PurchaseCharge extends Charge {


    @ManyToOne
    @NotNull
    private Provider provider;


    @ManyToOne
    @NotNull
    private PurchaseOrder purchaseOrder;


    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
        if (purchaseOrder != null) setProvider(purchaseOrder.getProvider());
    }

    @ManyToOne@Output
    private IssuedInvoice chargedTo;

    public void setChargedTo(IssuedInvoice chargedTo) throws Exception {
        if (this.chargedTo != null && this.chargedTo.equals(chargedTo)) throw  new Exception("Can not change the issued invoice this charge is related to");
        this.chargedTo = chargedTo;
    }


    public PurchaseCharge() {
        setType(ChargeType.PURCHASE);
    }


}
