package io.mateu.erp.model.financials;

import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.annotations.Output;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Getter
@Setter
public class CurrencyExchange implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Embedded
    @Output
    private Audit audit;

    @ManyToOne
    @NotNull
    private Currency from;

    @ManyToOne
    @NotNull
    private Currency to;

    private double rate;

}
