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
    private ProductType type;

    @ListColumn
    @CellStyleGenerator(ActiveCellStyleGenerator.class)
    private boolean active = true;

    @ManyToOne
    @NotNull
    @ListColumn
    @SearchFilter
    private Office office;


    @ManyToOne
    @NotNull
    @ListColumn
    @SearchFilter
    @NoChart
    private Zone zone;


    @ManyToOne
    private DataSheet dataSheet;

    @Override
    public String toString() {
        return getName();
    }
}
