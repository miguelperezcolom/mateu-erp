package io.mateu.erp.model.product;

import io.mateu.erp.model.authentication.Audit;
import io.mateu.erp.model.financials.*;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.partners.Actor;
import io.mateu.erp.model.revenue.Product;
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
//@Entity
//@Table(name = "contract")
@MappedSuperclass
@Getter
@Setter
public class AbstractContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Tab("General")
    @Embedded
    @Output
    private Audit audit;

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

    @ManyToOne
    @NotNull
    private Product product;

    private boolean VATIncluded;

    @ManyToOne
    private Currency currency;

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

    @Tab("Relations")
    @ManyToOne
    @NotNull
    @SearchFilter
    @ListColumn
    private Actor supplier;

    @ManyToOne
    @NotInEditor
    @SearchFilter
    @ListColumn
    private Office office;

    @OneToMany
    private List<Actor> targets = new ArrayList<>();


    @Tab("Signature")
    private String signedAt;

    private String signedBy;

    private String partnerSignatory;

    private String ownSignatory;

    private LocalDate signatureDate;

    @Tab("Commissions")
    @ManyToOne
    private CommissionTerms commissionTerms;

    @Tab("Payment")
    @ManyToOne
    private PaymentTerms paymentTerms;

    @Ignored
    private double averagePrice;


}
