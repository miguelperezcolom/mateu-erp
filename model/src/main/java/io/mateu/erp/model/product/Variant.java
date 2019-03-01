package io.mateu.erp.model.product;

import io.mateu.erp.model.product.generic.GenericProduct;
import io.mateu.mdd.core.model.multilanguage.Literal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity@Getter@Setter
public class Variant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private AbstractProduct product;

    @ManyToOne(cascade = CascadeType.ALL)
    private Literal name;

    @ManyToOne(cascade = CascadeType.ALL)
    private Literal description;


    @Override
    public String toString() {
        return (getName() != null)?getName().toString():"No name";
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof Variant && id != 0 && id == ((Variant) obj).getId());
    }
}
