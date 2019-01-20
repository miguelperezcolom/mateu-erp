package io.mateu.erp.model.world;

import io.mateu.erp.model.taxes.VAT;
import io.mateu.mdd.core.annotations.Ignored;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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
    @NotNull
    private String isoCode;

    @NotNull
    private String name;

    @OneToMany(mappedBy = "country")
    @Ignored
    private List<Destination> destinations = new ArrayList<>();

    @ManyToOne
    private VAT vat;

    @ElementCollection
    private List<String> nationalAirportsIATACodes;

    @Column(name = "_order")
    private int order;


    @Override
    public String toString() {
        return getName();
    }


    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj != null && obj instanceof Country && isoCode == ((Country)obj).isoCode);
    }

}