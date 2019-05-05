package io.mateu.erp.model.booking;

import io.mateu.erp.model.financials.BillingConcept;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class PriceBreakdownItem {

    BillingConcept concept;

    private String text;

    private double value;

    public PriceBreakdownItem(BillingConcept concept, String text, double value) {
        this.concept = concept;
        this.text = text;
        this.value = value;
    }
}
