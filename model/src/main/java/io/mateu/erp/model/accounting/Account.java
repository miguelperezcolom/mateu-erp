package io.mateu.erp.model.accounting;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

@Entity(name = "AccountingAccount")
@Getter
@Setter
public class Account {

    @Id
    private String number;

    private String name;

    private double debit;

    private double credit;

    private double balance;

    @PreUpdate@PrePersist
    public void pre() {
        setBalance(getCredit() - getDebit());
    }

}
