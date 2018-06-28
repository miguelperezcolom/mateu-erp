package io.mateu.erp.model.product.tour;

import io.mateu.common.model.multilanguage.Literal;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.product.generic.GenericProduct;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by miguel on 31/1/17.
 */
@Entity(name = "GenericExtra")
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

    private SupplementType type;

    private boolean internalUseOnly;

    @ManyToOne
    private GenericProduct product;

    @ManyToOne
    private Partner supplier;

}
