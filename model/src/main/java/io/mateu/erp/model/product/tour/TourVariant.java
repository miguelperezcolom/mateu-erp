package io.mateu.erp.model.product.tour;

import io.mateu.mdd.core.model.multilanguage.Literal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class TourVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private Tour tour;

    @ManyToOne(cascade = CascadeType.ALL)
    private Literal name;

    @ManyToOne(cascade = CascadeType.ALL)
    private Literal description;


    @Override
    public String toString() {
        return (getName() != null)?getName().toString():"No name";
    }
}
