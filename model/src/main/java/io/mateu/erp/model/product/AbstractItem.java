package io.mateu.erp.model.product;

import io.mateu.erp.model.multilanguage.Literal;
import io.mateu.erp.model.world.City;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@Getter
@Setter
public class AbstractItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;


    private Family family;

    @ManyToOne
    private Literal nameTranslated;


}
