package io.mateu.erp.model.revenue;

import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.ui.mdd.server.annotations.ListColumn;
import io.mateu.ui.mdd.server.annotations.SearchFilter;
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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "handlingFee")
    private List<HandlingFeeLine> lines = new ArrayList<>();

}
