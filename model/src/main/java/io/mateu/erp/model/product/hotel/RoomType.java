package io.mateu.erp.model.product.hotel;


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
public class RoomType {

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
