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
@Table(name = "MA_ITEM")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="ITMTYPE", discriminatorType=DiscriminatorType.STRING,length=20)
@DiscriminatorValue("ITEM")
@Getter
@Setter
public class AbstractItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="item_seq_gen")
    @SequenceGenerator(name="item_seq_gen", sequenceName="ITM_SEQ", allocationSize = 1)
    @Column(name = "ITMIDITM")
    private long id;

    @Column(name = "ITMNAME")
    private String name;


    @Column(name = "ITMFAMILY")
    private Family family;

    @ManyToOne
    @JoinColumn(name = "ITMNAMETRANSLATEDIDLIT")
    private Literal nameTranslated;


}
