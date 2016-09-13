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
@Table(name = "MA_CITY")
public class City {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO, generator="city_seq_gen")
    @SequenceGenerator(name="city_seq_gen", sequenceName="CTYIDCTY_SEQ")
    @Column(name = "CTYIDCTY")
    private long id;

    @ManyToOne
    @Column(name = "CTYNAMEIDTRA")
    private Translation name;

    @ElementCollection
    @CollectionTable(
            name="MA_CITY_ALIAS",
            joinColumns=@JoinColumn(name="XXXIDCTY")
    )
    @Column(name="XXXALIAS")
    private List<String> aliases = new ArrayList<>();

}
