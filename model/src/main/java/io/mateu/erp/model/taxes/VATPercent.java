package io.mateu.erp.model.taxes;

import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.ui.mdd.server.annotations.ListColumn;
import io.mateu.ui.mdd.server.annotations.Required;
import io.mateu.ui.mdd.server.annotations.SearchFilter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class VATPercent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Required
    @SearchFilter
    @ManyToOne
    private VAT vat;

    @ManyToOne
    @Required
    @SearchFilter
    private BillingConcept billingConcept;

    private double percent;
}
