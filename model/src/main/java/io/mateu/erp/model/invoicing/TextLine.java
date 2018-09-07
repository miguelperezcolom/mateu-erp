package io.mateu.erp.model.invoicing;

import javax.persistence.Entity;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
public class TextLine extends AbstractInvoiceLine {

    public TextLine() {
        super();
    }

    public TextLine(Invoice invoice) {
        super(invoice);
    }
}
