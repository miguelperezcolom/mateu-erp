package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.multilanguage.Literal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Getter
@Setter
public class BoardType {

    @Id
    private String code;

    @ManyToOne
    private Literal name;

}