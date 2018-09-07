package io.mateu.erp.model.invoicing;

import javax.persistence.Entity;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
public class HeaderInvoiceLine extends AbstractInvoiceLine {

    public HeaderInvoiceLine() {
        super();
    }

    public HeaderInvoiceLine(Invoice invoice) {
        super(invoice);
    }

}
