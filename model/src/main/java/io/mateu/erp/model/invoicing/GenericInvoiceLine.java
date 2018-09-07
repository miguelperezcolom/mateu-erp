package io.mateu.erp.model.invoicing;

import javax.persistence.Entity;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
public class GenericInvoiceLine extends AbstractInvoiceLine {

    public GenericInvoiceLine() {
        super();
    }

    public GenericInvoiceLine(Invoice invoice) {
        super(invoice);
    }
}
