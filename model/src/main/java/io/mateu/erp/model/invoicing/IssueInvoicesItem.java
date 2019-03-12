package io.mateu.erp.model.invoicing;

import io.mateu.erp.model.partners.Agency;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class IssueInvoicesItem {

    private Agency agency;

    private double total;


    public IssueInvoicesItem(Agency agency, double total) {
        this.agency = agency;
        this.total = total;
    }

}
