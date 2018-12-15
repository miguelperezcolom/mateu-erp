package io.mateu.erp.model.product;

import io.mateu.erp.model.mdd.ActiveCellStyleGenerator;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.world.Zone;
import io.mateu.mdd.core.annotations.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

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
    private Office partnerByDefault;



    @ManyToOne
    @NotNull
    @ListColumn
    @SearchFilter
    @NoChart
    private Zone zone;


    @ManyToOne(cascade = CascadeType.ALL)
    private DataSheet dataSheet;

    @Override
    public String toString() {
        return getName();
    }
}
