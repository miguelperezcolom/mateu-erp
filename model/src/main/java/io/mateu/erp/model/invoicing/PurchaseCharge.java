package io.mateu.erp.model.invoicing;


import com.vaadin.data.provider.DataProvider;
import io.mateu.erp.model.booking.PurchaseOrder;
import io.mateu.erp.model.booking.Service;
import io.mateu.mdd.core.annotations.DependsOn;
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
    private PurchaseOrder purchaseOrder;


    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
        if (purchaseOrder != null) setPartner(purchaseOrder.getProvider());
    }


    @ManyToOne
    private Service service;

    public void setService(Service service) {
        this.service = service;
        if (service != null) setOffice(service.getOffice());
    }


    @DependsOn("booking")
    public DataProvider getServiceDataProvider() throws Throwable {
        return new JPQLListDataProvider(
                "select x from " + PurchaseOrder.class.getName() + " y inner join y.services x " +
                        ((getPurchaseOrder() != null)?" where y.id = " + getPurchaseOrder().getId():" where y.id = 0"));
    }




    public PurchaseCharge() {
        setType(ChargeType.PURCHASE);
    }


}
