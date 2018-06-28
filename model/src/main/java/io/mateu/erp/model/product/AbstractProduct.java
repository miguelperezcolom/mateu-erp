package io.mateu.erp.model.product;

import io.mateu.erp.model.mdd.ActiveCellStyleGenerator;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.world.Zone;
import io.mateu.ui.mdd.server.annotations.CellStyleGenerator;
import io.mateu.ui.mdd.server.annotations.ListColumn;
import io.mateu.ui.mdd.server.annotations.SearchFilter;
import io.mateu.ui.mdd.server.annotations.Tab;
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

    @Tab("General")
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


    @Tab("Location")
    @ManyToOne
    @NotNull
    @ListColumn
    @SearchFilter
    private Zone zone;


}
