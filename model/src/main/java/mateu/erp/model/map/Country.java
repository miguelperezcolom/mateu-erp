package mateu.erp.model.map;

import lombok.Getter;
import lombok.Setter;
import mateu.erp.model.multilanguage.Translation;

import javax.persistence.*;

/**
 * holder for countries
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Getter@Setter
@Table(name = "MA_COUNTRY")
public class Country {

    @Id
    @Column(name = "COUISOCODE", length = -1)
    private String isoCode;

    @ManyToOne
    @Column(name = "COUNAMEIDTRA")
    private Translation name;

}
