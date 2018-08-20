package io.mateu.erp.model.taxes;

import io.mateu.erp.model.financials.Currency;
import io.mateu.mdd.core.annotations.SearchFilter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class VATSettlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @SearchFilter
    @ManyToOne
    private VAT vat;

    LocalDate date;

    @ManyToOne
    private Currency currency;

    private double value;
}
