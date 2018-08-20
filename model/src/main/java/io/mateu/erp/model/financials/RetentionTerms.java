package io.mateu.erp.model.financials;

import io.mateu.mdd.core.annotations.Tab;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter@Setter
public class RetentionTerms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Tab("Info")
    private String name;

    //todo: convertir en enumeración????
    @ManyToOne
    @NotNull
    private RetentionType type;

    private double percent;

}
