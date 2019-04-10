package io.mateu.erp.model.product;

import io.mateu.mdd.core.model.multilanguage.Literal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity@Getter@Setter
public class ProductLabel {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotEmpty
    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    private Literal description;

    @Override
    public String toString() {
        return name != null?name:"Product label " + id;
    }
}
