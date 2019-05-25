package io.mateu.erp.model.product.tour;

import io.mateu.erp.model.partners.Provider;
import io.mateu.erp.model.product.Variant;
import io.mateu.erp.model.product.generic.GenericProduct;
import io.mateu.mdd.core.annotations.Section;
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

    @Section("Associated cost")
    @ManyToOne
    private GenericProduct product;

    @ManyToOne
    private Variant variant;

    @ManyToOne
    private Provider provider;




    @Override
    public String toString() {
        return (getName() != null)?getName().toString():"No name";
    }
}
