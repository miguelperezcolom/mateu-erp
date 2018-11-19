package io.mateu.erp.model.product.hotel;


import io.mateu.mdd.core.annotations.NoChart;
import io.mateu.mdd.core.model.multilanguage.Literal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Getter@Setter
public class HotelExtraType {

    @Id
    @NotEmpty
    private String code;

    @ManyToOne(cascade = CascadeType.ALL)
    @NotNull
    @NoChart
    private Literal name;


    @Override
    public String toString() {
        return (getName() != null)?getName().toString():"No name";
    }



    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj != null && obj instanceof HotelExtraType && code == ((HotelExtraType)obj).code);
    }

}
