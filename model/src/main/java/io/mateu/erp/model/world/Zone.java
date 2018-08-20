package io.mateu.erp.model.world;

import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.SearchFilter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * holder for cities
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Getter@Setter
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    @SearchFilter
    private Destination destination;

    @NotNull
    @SearchFilter
    private String name;

    @ElementCollection
    /**
     * sometimes the same city is known under different names, aka aliases (e.g. Palma de Mallorca is also known as Cuitat)
     */
    private List<String> aliases = new ArrayList<>();

    @OneToMany(mappedBy = "zone")
    @Ignored
    private List<TransferPoint> transferPoints = new ArrayList<>();

    @OneToMany(mappedBy = "zone")
    @Ignored
    private List<Hotel> hotels = new ArrayList<>();

    @Override
    public String toString() {
        return getName();
    }
}
