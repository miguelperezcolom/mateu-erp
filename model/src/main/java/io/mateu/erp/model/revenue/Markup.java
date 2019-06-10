package io.mateu.erp.model.revenue;

import io.mateu.erp.model.partners.Agency;
import io.mateu.erp.model.partners.AgencyGroup;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.ListColumn;
import io.mateu.mdd.core.annotations.SearchFilter;
import io.mateu.mdd.core.annotations.UseChips;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class Markup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @SearchFilter
    @ListColumn
    private String name;

    private boolean active;

    @OneToMany(mappedBy = "markup")@UseChips
    private Set<AgencyGroup> partnerGroups = new HashSet<>();


    @OneToMany(mappedBy = "markup")
    private List<Agency> agencies = new ArrayList<>();

    @OneToMany(mappedBy = "markup")
    @Ignored
    private List<MarkupLine> lines = new ArrayList<>();


    @Override
    public String toString() {
        return getName();
    }

    public MarkupLine getLine(ProductLine productLine) {
        for (MarkupLine l : lines) if (l.getProductLine().equals(productLine)) return l;
        return null;
    }
}
