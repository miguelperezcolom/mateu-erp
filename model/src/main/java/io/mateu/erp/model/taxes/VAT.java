package io.mateu.erp.model.taxes;

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
public class VAT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @SearchFilter
    @ListColumn
    private String name;

    private double specialRegimePercent;

    @OneToMany(mappedBy = "vat")
    @Ignored
    private List<VATPercent> percents = new ArrayList<>();
}
