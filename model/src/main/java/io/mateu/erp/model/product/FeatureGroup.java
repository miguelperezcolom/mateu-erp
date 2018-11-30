package io.mateu.erp.model.product;

import io.mateu.mdd.core.model.multilanguage.Literal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Getter
@Setter
public class FeatureGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(cascade = CascadeType.ALL)
    private Literal name;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<Feature> features = new ArrayList<>();


    @Override
    public String toString() {
        return (getName() != null)?getName().toString():"No name";
    }

}
