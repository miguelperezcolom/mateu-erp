package io.mateu.erp.model.invoicing;

import io.mateu.erp.model.partners.Partner;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class IssueInvoicesItem {

    private Partner partner;

    private double total;


    public IssueInvoicesItem(Partner partner, double total) {
        this.partner = partner;
        this.total = total;
    }

}
