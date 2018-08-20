package io.mateu.erp.model.accounting;

import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.Output;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class AccountingEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Embedded
    @Output
    private Audit audit;

    @ManyToOne
    @NotNull
    AccountingPlan plan;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "entry")
    @OrderColumn(name = "orderInsideEntry")
    @Ignored
    private List<LineItem> lines = new ArrayList<>();

    @Output
    private double credit;

    @Output
    private double debit;

    @Output
    private double balance;


    public void update() {
        double c = 0;
        double d = 0;

        for (LineItem l : lines) {
            c += l.getCredit();
            d = l.getDebit();
        };

        setCredit(c);
        setDebit(d);
        setBalance(c - d);
    }
}
