package io.mateu.erp.model.taxes;

import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.mdd.core.annotations.SearchFilter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class VATPercent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @SearchFilter
    @ManyToOne
    private VAT vat;

    @ManyToOne
    @NotNull
    @SearchFilter
    private BillingConcept billingConcept;

    private double percent;
}
