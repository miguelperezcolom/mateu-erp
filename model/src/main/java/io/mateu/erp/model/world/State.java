package io.mateu.erp.model.world;

import io.mateu.erp.model.multilanguage.Literal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * holder for states
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Getter@Setter
public class State {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Literal name;

    @ManyToOne
    private Country country;

    @OneToMany(mappedBy = "state")
    private List<City> cities = new ArrayList<>();

}
