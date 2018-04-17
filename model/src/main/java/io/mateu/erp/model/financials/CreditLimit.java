package io.mateu.erp.model.financials;

import io.mateu.ui.mdd.server.annotations.Output;
import io.mateu.ui.mdd.server.annotations.Tab;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class CreditLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Tab("Info")
    @NotNull
    private String name;

    @NotNull
    private CreditLimitType type;

    @Column(name = "_limit")
    private double limit;

    @NotNull
    @ManyToOne
    private Currency currency;

    @Output
    private double remaining;

    @Output
    private double percent;

    private double notificationTreshold;

    private String emails;


    @Tab("Agents")
    @ManyToMany(mappedBy = "creditLimits")
    private List<FinancialAgent> agents = new ArrayList<>();

}
