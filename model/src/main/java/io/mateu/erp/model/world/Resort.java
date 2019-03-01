package io.mateu.erp.model.world;

import io.mateu.erp.model.product.AbstractProduct;
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
 * holder for resorts
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Getter@Setter
public class Resort {

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
     * sometimes the same resort is known under different names, aka aliases (e.g. Palma de Mallorca is also known as Cuitat)
     */
    private List<String> aliases = new ArrayList<>();

    @OneToMany(mappedBy = "resort")
    @Ignored
    private List<TransferPoint> transferPoints = new ArrayList<>();

    @OneToMany(mappedBy = "resort")
    @Ignored
    private List<AbstractProduct> products = new ArrayList<>();

    @Column(name = "_order")
    private int order;


    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj != null && obj instanceof Resort && id == ((Resort) obj).getId());
    }
}
