package io.mateu.erp.model.product;

import io.mateu.erp.model.multilanguage.Literal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Table(name = "MA_FEATUREGROUP")
@Getter
@Setter
public class FeatureGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="featuregroup_seq_gen")
    @SequenceGenerator(name="featuregroup_seq_gen", sequenceName="FGR_SEQ", allocationSize = 1)
    @Column(name = "FGRIDFGR")
    private long id;

    @ManyToOne
    @JoinColumn(name = "FGRNAMEIDLIT")
    private Literal name;

    @OneToMany(mappedBy = "group")
    private List<Feature> features = new ArrayList<>();
}
