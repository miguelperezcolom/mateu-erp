package io.mateu.erp.model.product;

import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.erp.model.financials.*;
import io.mateu.erp.model.organization.Company;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.financials.CommissionTerms;
import io.mateu.erp.model.financials.PaymentTerms;
import io.mateu.erp.model.partners.Market;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.product.tour.Tour;
import io.mateu.erp.model.revenue.ProductLine;
import io.mateu.mdd.core.annotations.*;
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
    private ProductLine product;

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
    private Partner supplier;

    @ManyToOne
    @NotInEditor
    @SearchFilter
    @ListColumn
    private Office office;


    @OneToMany
    private List<Partner> partners = new ArrayList<>();

    @OneToMany
    private List<Market> markets = new ArrayList<>();

    @OneToMany
    private List<Company> companies = new ArrayList<>();

    @Tab("Tour")
    @OneToMany
    private List<Tour> tours = new ArrayList<>();

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

    @Output
    private double averagePrice;

    @ManyToOne
    private CancellationRules cancellationRules;
}
