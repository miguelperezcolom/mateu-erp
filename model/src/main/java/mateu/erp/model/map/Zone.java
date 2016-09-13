package mateu.erp.model.map;

import lombok.Getter;
import lombok.Setter;
import mateu.erp.model.multilanguage.Translation;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * holder for countries
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Getter@Setter
@Table(name = "MA_ZONE")
public class Zone {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO, generator="zone_seq_gen")
    @SequenceGenerator(name="zone_seq_gen", sequenceName="ZONIDZON_SEQ")
    @Column(name = "ZONIDZON")
    private long id;

    @ManyToOne
    @Column(name = "ZONNAMEIDTRA")
    private Translation name;


    @OneToMany
    @JoinTable(name = "MA_ZONE_CITY", joinColumns = @JoinColumn(name = "XXXIDZON"), inverseJoinColumns = @JoinColumn(name = "XXXIDCTY"))
    private List<City> cities = new ArrayList<>();

}
