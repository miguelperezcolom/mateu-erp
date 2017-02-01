package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.multilanguage.Literal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Getter
@Setter
public class HotelCategory {

    @Id
    private String code;

    @ManyToOne
    private Literal name;
}
