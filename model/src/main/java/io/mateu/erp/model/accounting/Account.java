package io.mateu.erp.model.accounting;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "AccountingAccount")
@Getter
@Setter
public class Account {

    @Id
    private String number;

    private String name;

    private double balance;

}
