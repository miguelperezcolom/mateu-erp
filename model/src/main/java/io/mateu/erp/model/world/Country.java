package io.mateu.erp.model.world;

import io.mateu.erp.model.multilanguage.Literal;
import lombok.Getter;
import lombok.Setter;

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
@Table(name = "MA_COUNTRY")
public class Country {

    @Id
    @Column(name = "COUISOCODE", length = -1)
    private String isoCode;

    @ManyToOne
    @Column(name = "COUNAMEIDTRA")
    private Literal name;

    @OneToMany(mappedBy = "country")
    private List<State> states = new ArrayList<>();
}
