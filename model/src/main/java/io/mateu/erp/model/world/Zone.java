package io.mateu.erp.model.world;

import io.mateu.erp.model.multilanguage.Literal;
import io.mateu.ui.mdd.server.annotations.Required;
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
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Required
    private String name;


    @OneToMany
     private List<City> cities = new ArrayList<>();

}
