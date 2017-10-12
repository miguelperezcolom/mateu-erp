package io.mateu.erp.model.world;

import io.mateu.erp.model.multilanguage.Literal;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.ui.mdd.server.annotations.Ignored;
import io.mateu.ui.mdd.server.annotations.Required;
import io.mateu.ui.mdd.server.annotations.SearchFilter;
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
    @Required
    @SearchFilter
    private State state;

    @Required
    @SearchFilter
    private String name;

    @ElementCollection
    /**
     * sometimes the same city is known under different names, aka aliases (e.g. Palma de Mallorca is also known as Cuitat)
     */
    private List<String> aliases = new ArrayList<>();

    @OneToMany(mappedBy = "city")
    @Ignored
    private List<TransferPoint> transferPoints = new ArrayList<>();

    @OneToMany(mappedBy = "city")
    @Ignored
    private List<Hotel> hotels = new ArrayList<>();

    @Override
    public String toString() {
        return getName();
    }
}
