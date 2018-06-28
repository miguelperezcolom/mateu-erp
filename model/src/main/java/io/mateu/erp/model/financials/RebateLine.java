package io.mateu.erp.model.financials;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class RebateLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private Rebate rebate;

    @Column(name = "_from")
    private double from;

    @Column(name = "_to")
    private double to;

    private double percent;

}
