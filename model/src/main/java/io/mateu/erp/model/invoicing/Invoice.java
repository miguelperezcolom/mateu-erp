package io.mateu.erp.model.invoicing;

import io.mateu.common.model.authentication.Audit;
import io.mateu.erp.model.financials.Currency;
import io.mateu.erp.model.financials.FinancialAgent;
import io.mateu.ui.mdd.server.annotations.Output;
import io.mateu.ui.mdd.server.annotations.OwnedList;
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
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Embedded
    @Output
    private Audit audit;

    private String number;

    private LocalDate issueDate;

    private LocalDate taxDate;


    @ManyToOne
    private FinancialAgent issuer;

    @ManyToOne
    private FinancialAgent recipient;


    @OneToMany(mappedBy = "invoice")
    @OwnedList
    private List<AbstractInvoiceLine> lines = new ArrayList<>();



    @NotNull
    @ManyToOne
    private Currency currency;

    private double total;

    @NotNull
    @ManyToOne
    private Currency accountingCurrency;

    private double totalInAccountingCurrency;

    private double accountingCurrencyExchangeRate;



}
