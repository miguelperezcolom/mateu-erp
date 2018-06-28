package io.mateu.erp.model.financials;

import io.mateu.common.model.authentication.Audit;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class RebateSettlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Embedded
    @NotNull
    private Audit audit;

    private LocalDate toDate;

    @NotNull
    @ManyToOne
    private Currency accountingCurrency;

    private double totalInAccountingCurrency;

    private double accountingCurrencyExchangeRate;

}
