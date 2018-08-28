package io.mateu.erp.model.accounting;

import io.mateu.erp.model.financials.Currency;
import io.mateu.mdd.core.annotations.Unmodifiable;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class AccountingPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotEmpty
    private String name;

    @NotNull
    @Unmodifiable
    @ManyToOne
    private Currency currency;

    @Override
    public String toString() {
        return getName();
    }
}
