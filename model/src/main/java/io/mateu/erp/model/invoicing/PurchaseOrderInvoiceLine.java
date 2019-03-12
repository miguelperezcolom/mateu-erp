package io.mateu.erp.model.invoicing;

import io.mateu.erp.model.booking.PurchaseOrder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Getter@Setter
public class PurchaseOrderInvoiceLine extends AbstractInvoiceLine {

    @ManyToOne
    @NotNull
    private PurchaseOrder purchaseOrder;


    public PurchaseOrderInvoiceLine() {
        super();
    }

    public PurchaseOrderInvoiceLine(Invoice invoice, PurchaseCharge c) {
        super(invoice);

        purchaseOrder = c.getPurchaseOrder();

        setPrice(c.getTotal());
        setQuantity(1);
        setTotal(getPrice());
        setSubject(c.getText());
    }
}
