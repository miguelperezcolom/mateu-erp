package io.mateu.erp.model.product.tour;

import io.mateu.erp.model.partners.Agency;
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


    @Override
    public boolean equals(Object obj) {
        return this == obj || (code != null && obj != null && obj instanceof ExcursionLanguage && code.equals(((ExcursionLanguage) obj).getCode()));
    }

    @Override
    public String toString() {
        return code;
    }
}
