package io.mateu.erp.model.product;

import io.mateu.mdd.core.annotations.FullWidth;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class ContractClauseGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner")
    @FullWidth
    private List<ContractClause> clauses = new ArrayList<>();


    @Override
    public String toString() {
        return name;
    }
}
