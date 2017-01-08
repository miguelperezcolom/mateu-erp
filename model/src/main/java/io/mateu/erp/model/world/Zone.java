package io.mateu.erp.model.world;

import io.mateu.erp.model.multilanguage.Literal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * holder for zones. A zone is used to group several cities under the same name (e.g. Calas de Mallorca)
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Getter@Setter
@Table(name = "MA_ZONE")
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="zone_seq_gen")
    @SequenceGenerator(name="zone_seq_gen", sequenceName="ZONIDZON_SEQ", allocationSize = 1)
    @Column(name = "ZONIDZON")
    private long id;

    @ManyToOne
    @JoinColumn(name = "ZONNAMEIDTRA")
    private Literal name;


    @OneToMany
    @JoinTable(name = "MA_ZONE_CITY", joinColumns = @JoinColumn(name = "XXXIDZON"), inverseJoinColumns = @JoinColumn(name = "XXXIDCTY"))
    private List<City> cities = new ArrayList<>();

}
