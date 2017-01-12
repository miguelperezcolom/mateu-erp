package io.mateu.erp.model.product;

import io.mateu.erp.model.multilanguage.Literal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Table(name = "MA_FEATURE")
@Getter
@Setter
public class Feature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FEAIDFEA")
    private long id;

    @ManyToOne
    @JoinColumn(name = "FEANAMEIDLIT")
    private Literal name;

    @ManyToOne
    @JoinColumn(name = "FEAIDFGR")
    private FeatureGroup group;

}
