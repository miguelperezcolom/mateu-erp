package io.mateu.erp.model.partners;

import io.mateu.erp.model.financials.FinancialAgent;
import io.mateu.mdd.core.annotations.ColumnWidth;
import io.mateu.mdd.core.annotations.ListColumn;
import io.mateu.mdd.core.annotations.Section;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity@Getter@Setter
public class CommissionAgent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Section("General")
    @NotNull
    @ListColumn
    private String name;

    @NotNull
    @ListColumn
    @ColumnWidth(100)
    private CommissionAgentStatus status;

    @ManyToOne
    private FinancialAgent financialAgent;


    private String fullAddress;

    private String telephone;

    private String email;

    private String comments;


    @Override
    public String toString() {
        return name;
    }
}
