package io.mateu.erp.model.product;

import io.mateu.erp.model.authentication.Audit;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.ui.mdd.server.annotations.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Getter
@Setter
public class AbstractContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Embedded
    @Output
    private Audit audit;

    @Tab("General")
    @FullWidth
    @NotNull
    @SearchFilter
    @ListColumn
    private String title;

    @NotNull
    @SearchFilter
    @ListColumn
    private ContractType type;

    @NotNull
    @ManyToOne
    private BillingConcept billingConcept;

    private boolean VATIncluded;

    @NotNull
    @ListColumn
    private LocalDate validFrom;
    @NotNull
    @ListColumn
    @SameLine
    private LocalDate validTo;

    @ListColumn
    private LocalDate bookingWindowFrom;
    @ListColumn
    @SameLine
    private LocalDate bookingWindowTo;


    @TextArea
    private String specialTerms;

    @TextArea
    @SameLine
    private String privateComments;

    @ManyToOne
    @NotNull
    @SearchFilter
    @ListColumn
    private Actor supplier;

    @OneToMany
    private List<Actor> targets = new ArrayList<>();

    @Ignored
    private double averagePrice;

}
