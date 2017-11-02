package io.mateu.erp.model.product;

import io.mateu.erp.model.multilanguage.Literal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.ManyToOne;

/**
 * Created by miguel on 1/10/16.
 */
@Getter
@Setter
public class AbstractItem {



    private String name;


    private Family family;

    @ManyToOne
    private Literal nameTranslated;


}
