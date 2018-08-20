package io.mateu.erp.model.financials;

import io.mateu.erp.model.payments.Deposit;
import io.mateu.erp.model.taxes.VAT;
import io.mateu.mdd.core.annotations.Tab;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * holder for a admin agent, tipically associated to a VAT ID (e.g. a customer, a supplier, ourselves)
 *
 * Sometimes the same admin agent will act as customer and as supplier. This is interesting to manage a balance of payments
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Getter@Setter
public class FinancialAgent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Tab("Info")
    private String name;

    private String businessName;

    private String address;

    private String country;

    private String city;

    private String postalCode;

    private String email;

    @NotNull
    @ManyToOne
    private Currency currency;


    @Tab("Taxes")
    private String vatIdentificationNumber;

    private VAT vat;

    private boolean EU;

    @Tab("As customer")
    @NotNull
    private AutomaticInvoiceBasis automaticInvoiceBasis;

    private boolean lasMonthDay;

    private boolean fortnight;

    @NotNull
    private InvoiceGrouping invoiceGrouping;

    @Tab("As provider")
    private boolean specialRegime;

    private boolean invoicesBeforeCheckinAllowed;

    @Tab("Deposits")
    @OneToMany(mappedBy = "agent")
    private List<Deposit> deposits = new ArrayList<>();

    @Tab("Payments")
    @ManyToOne
    private PaymentTerms paymentTerms;

    @ManyToOne
    private RetentionTerms retention;

    @ManyToOne
    private PaymentMethod paymentMethod;

    private boolean paymentsBlocked;

    private String cc;

    private String IBAN;

    private String SWIFT;


    @Tab("Credit")
    @NotNull
    private RiskType riskType;

    @ManyToMany
    private List<CreditLimit> creditLimits = new ArrayList<>();

    @Tab("Voxel")
    private String voxelId;


    @Override
    public String toString() {
        return getName();
    }
}
