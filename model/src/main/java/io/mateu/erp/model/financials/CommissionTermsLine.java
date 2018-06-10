package io.mateu.erp.model.financials;

import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.revenue.Product;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class CommissionTermsLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private CommissionTerms terms;

    @ManyToOne
    @NotNull
    private Partner agent;

    @ManyToOne
    @NotNull
    private Product product;

    @Column(name = "_start")
    private LocalDate start;

    @Column(name = "_end")
    private LocalDate end;

    private double percent;

}
