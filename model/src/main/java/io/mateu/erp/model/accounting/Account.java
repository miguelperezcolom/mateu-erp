package io.mateu.erp.model.accounting;

import io.mateu.mdd.core.annotations.Output;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity(name = "AccountingAccount")
@Getter
@Setter
public class Account {

    @Id
    private String number;

    @ManyToOne
    @NotNull
    private AccountingPlan plan;

    private String name;

    @Output
    private double debit;

    @Output
    private double credit;

    @Output
    private double balance;

    @PreUpdate@PrePersist
    public void pre() {
        setBalance(getCredit() - getDebit());
    }

}
