package io.mateu.erp.model.world;

import lombok.Getter;
import lombok.Setter;
import io.mateu.erp.model.multilanguage.Literal;

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
@Table(name = "MA_CITY")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="city_seq_gen")
    @SequenceGenerator(name="city_seq_gen", sequenceName="CTYIDCTY_SEQ", allocationSize = 1)
    @Column(name = "CTYIDCTY")
    private long id;

    @ManyToOne
    @JoinColumn(name = "CTYNAMEIDTRA")
    private Literal name;


    @ElementCollection
    @CollectionTable(
            name="MA_CITY_ALIAS",
            joinColumns=@JoinColumn(name="XXXIDCTY")
    )
    @Column(name="XXXALIAS")
    /**
     * sometimes the same city is known under different names, aka aliases (e.g. Palma de Mallorca is also known as Cuitat)
     */
    private List<String> aliases = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "CTYIDSTT")
    private State state;

}
