package io.mateu.erp.model.product.tour;

import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.product.generic.GenericProduct;
import io.mateu.mdd.core.model.multilanguage.Literal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by miguel on 31/1/17.
 */
@Entity
@Getter
@Setter
public class TourExtra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private Tour tour;

    @ManyToOne
    private Literal name;

    @NotNull
    private SupplementType type;

    private boolean optional;

    private boolean internalUseOnly;

    @ManyToOne
    private GenericProduct product;

    @ManyToOne
    private Partner supplier;




    @Override
    public String toString() {
        return (getName() != null)?getName().toString():"No name";
    }
}
