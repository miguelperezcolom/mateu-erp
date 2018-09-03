package io.mateu.erp.model.payments;

import io.mateu.erp.model.financials.Currency;
import io.mateu.mdd.core.annotations.ListColumn;
import io.mateu.mdd.core.annotations.Output;
import io.mateu.mdd.core.annotations.SearchFilter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @SearchFilter
    @ListColumn
    private String name;

    @ManyToOne
    @NotNull
    private Currency currency;

    private String comments;

    @Output
    private double input;

    @Output
    private double balance;

    @Output
    private double output;


    @Override
    public String toString() {
        return getName();
    }
}
