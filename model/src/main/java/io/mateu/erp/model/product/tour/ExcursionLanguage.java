package io.mateu.erp.model.product.tour;

import io.mateu.mdd.core.model.multilanguage.Literal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity@Getter@Setter
public class ExcursionLanguage {

    @Id
    private String code;

    @ManyToOne(cascade = CascadeType.ALL)
    private Literal name;

}
