package io.mateu.erp.model.product;

import io.mateu.erp.model.authentication.Audit;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.ui.mdd.server.annotations.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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
    @Required
    @SearchFilter
    private String title;

    @Required
    @SearchFilter
    private ContractType type;

    @Required
    @ManyToOne
    private BillingConcept billingConcept;

    private boolean VATIncluded;

    @StartsLine
    @Required
    private LocalDate validFrom;
    @Required
    private LocalDate validTo;

    private LocalDate bookingWindowFrom;
    private LocalDate bookingWindowTo;

    @StartsLine
    @TextArea
    private String specialTerms;

    @TextArea
    private String privateComments;

    @ManyToOne
    @StartsLine
    @Required
    @SearchFilter
    private Actor supplier;

    @OneToMany
    private List<Actor> targets = new ArrayList<>();

    @Ignored
    private double averagePrice;

}
