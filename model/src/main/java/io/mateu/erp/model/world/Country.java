package io.mateu.erp.model.world;

import io.mateu.erp.model.multilanguage.Literal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * holder for countries
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Getter@Setter
public class Country {

    @Id
    private String isoCode;

    @ManyToOne
    private Literal name;

    @OneToMany(mappedBy = "country")
    private List<State> states = new ArrayList<>();
}
