package io.mateu.erp.model.payments;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class BankAccount extends Account {

    private String IBAN;


}
