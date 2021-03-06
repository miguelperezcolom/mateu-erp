package io.mateu.erp.model.taxes;

import io.mateu.erp.model.world.Country;
import io.mateu.erp.model.world.Destination;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.ListColumn;
import io.mateu.mdd.core.annotations.SearchFilter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class VAT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @SearchFilter
    @ListColumn
    private String name;

    private double specialRegimePercent;

    private String specialRegimeText;

    @OneToMany(mappedBy = "vat", cascade = CascadeType.ALL, orphanRemoval = true)
    @Ignored
    private List<VATPercent> percents = new ArrayList<>();

    @OneToMany(mappedBy = "vat")
    private List<Country> countries = new ArrayList<>();

    @OneToMany(mappedBy = "vat")
    private List<Destination> destinations = new ArrayList<>();

    @Override
    public String toString() {
        return getName();
    }
}
