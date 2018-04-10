package io.mateu.erp.model.product;


import io.mateu.ui.mdd.server.annotations.TextArea;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class ContractClause {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "_group")
    private ContractClauseGroup group;

    @Column(name = "_order")
    private int order;

    @NotNull
    @TextArea
    private String text;


}
