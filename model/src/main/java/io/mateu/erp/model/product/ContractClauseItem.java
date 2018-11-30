package io.mateu.erp.model.product;


import io.mateu.mdd.core.annotations.TextArea;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class ContractClauseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "_order")
    private int order;

    @NotNull
    @TextArea
    private String text;

    public ContractClauseItem() {

    }

    public ContractClauseItem(int order, String text) {
        this.order = order;
        this.text = text;
    }

}
