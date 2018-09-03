package io.mateu.erp.model.product.hotel;

import io.mateu.mdd.core.model.multilanguage.Literal;
import io.mateu.mdd.core.annotations.QLForCombo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Getter
@Setter
@QLForCombo(ql = "select x.code, x.name.es from HotelCategory x order by x.name.es")
public class HotelCategory {

    @Id
    @NotEmpty
    private String code;

    @ManyToOne(cascade = CascadeType.ALL)
    @NotNull
    private Literal name;

    @Override
    public String toString() {
        return (getName() != null)?getName().toString():"No name";
    }
}
