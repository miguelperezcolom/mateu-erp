package io.mateu.erp.model.product;

import io.mateu.mdd.core.model.multilanguage.Literal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Getter
@Setter
public class Feature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(cascade = CascadeType.ALL)
    private Literal name;

    @ManyToOne
    @NotNull
    private FeatureGroup group;

    @Override
    public String toString() {
        return (getName() != null)?getName().toString():"No name";
    }

}
