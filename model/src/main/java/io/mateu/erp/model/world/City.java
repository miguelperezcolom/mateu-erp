package io.mateu.erp.model.world;

import io.mateu.erp.model.multilanguage.Literal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * holder for cities
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Getter@Setter
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Literal name;


    @ElementCollection
    /**
     * sometimes the same city is known under different names, aka aliases (e.g. Palma de Mallorca is also known as Cuitat)
     */
    private List<String> aliases = new ArrayList<>();

    @ManyToOne
    private State state;

}
