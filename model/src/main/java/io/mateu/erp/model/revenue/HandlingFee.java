package io.mateu.erp.model.revenue;

import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.ListColumn;
import io.mateu.mdd.core.annotations.SearchFilter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter@Setter
public class HandlingFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @SearchFilter
    @ListColumn
    private String name;

    @ManyToOne
    @NotNull
    private BillingConcept billingConcept;

    @Ignored
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "handlingFee")
    private List<HandlingFeeLine> lines = new ArrayList<>();


    @Override
    public String toString() {
        return getName();
    }
}
