package io.mateu.erp.model.product;

import io.mateu.common.model.multilanguage.Literal;
import io.mateu.erp.model.product.AbstractProduct;
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
public class Extra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private AbstractProduct product;

    @ManyToOne
    private Literal name;

    private boolean internalUseOnly;
}
