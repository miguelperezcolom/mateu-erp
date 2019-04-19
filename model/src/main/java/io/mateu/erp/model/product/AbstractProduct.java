package io.mateu.erp.model.product;

import io.mateu.erp.model.mdd.ActiveCellStyleGenerator;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.partners.Provider;
import io.mateu.erp.model.revenue.ProductLine;
import io.mateu.erp.model.world.Resort;
import io.mateu.mdd.core.annotations.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Product")
@Getter@Setter
public abstract class AbstractProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ListColumn
    @NotNull
    @SearchFilter
    private String name;

    @ManyToOne
    @NotNull
    @ListColumn
    @ColumnWidth(120)
    private ProductType type;

    @ListColumn
    @CellStyleGenerator(ActiveCellStyleGenerator.class)
    @ColumnWidth(70)
    private boolean active = true;

    @ManyToOne
    @NotNull
    @ListColumn
    @SearchFilter
    @ColumnWidth(120)
    private Office office;


    @ManyToOne
    @ListColumn
    @SearchFilter
    private Provider providedBy;

    @ManyToOne@NotNull
    private ProductLine productLine;


    @ManyToOne
    @NotNull
    @ListColumn
    @SearchFilter
    @NoChart
    private Resort resort;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
    @Ignored
    private List<Variant> variants = new ArrayList<>();



    @ManyToOne(cascade = CascadeType.ALL)
    private DataSheet dataSheet;

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof AbstractProduct && id != 0 && id == ((AbstractProduct) obj).getId());
    }
}
