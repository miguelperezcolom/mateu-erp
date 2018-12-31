package io.mateu.erp.model.product.transfer;

import io.mateu.erp.model.product.generic.SupplementType;
import io.mateu.mdd.core.model.multilanguage.Literal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by miguel on 31/1/17.
 */
@Entity(name = "TransferExtra")
@Getter
@Setter
public class Extra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Literal name;

    private SupplementType type;

    private boolean optional;

    private boolean internalUseOnly;

}
