package io.mateu.erp.model.payments;

import io.mateu.erp.model.financials.Currency;
import io.mateu.ui.mdd.server.annotations.ListColumn;
import io.mateu.ui.mdd.server.annotations.SearchFilter;
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

    private double input;

    private double balance;

    private double output;

    private String comments;

}
