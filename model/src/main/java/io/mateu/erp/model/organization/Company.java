package io.mateu.erp.model.organization;

import io.mateu.erp.model.accounting.AccountingPlan;
import io.mateu.erp.model.financials.FinancialAgent;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter@Setter
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @NotNull
    @ManyToOne
    private FinancialAgent financialAgent;

    @NotNull
    @ManyToOne
    private AccountingPlan accountingPlan;

}
