package io.mateu.erp.model.revenue;

import io.mateu.erp.model.partners.Partner;
import io.mateu.ui.mdd.server.annotations.Ignored;
import io.mateu.ui.mdd.server.annotations.ListColumn;
import io.mateu.ui.mdd.server.annotations.SearchFilter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "markup")
    private List<Partner> actors = new ArrayList<>();

    @OneToMany(mappedBy = "markup")
    @Ignored
    private List<MarkupLine> lines = new ArrayList<>();
}
