package io.mateu.erp.model.product;


import io.mateu.mdd.core.annotations.TextArea;
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
    //@JoinColumn(name = "_group")
    //todo: ver por que no podemos llamarle "group". Luego falla al abrir el mantenimiento
    private ContractClauseGroup owner;

    @Column(name = "_order")
    private int order;

    @NotNull
    @TextArea
    private String text;


}
