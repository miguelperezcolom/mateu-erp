package io.mateu.erp.model.accounting;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class LineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private AccountingEntry entry;

    @ManyToOne
    private Account account;


    private int orderInsideEntry;


    private double debit;

    private double credit;

}
