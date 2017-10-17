package io.mateu.erp.model.product.hotel;

import io.mateu.erp.dispo.interfaces.product.IInventory;
import io.mateu.ui.mdd.server.annotations.Ignored;
import io.mateu.ui.mdd.server.annotations.SearchFilter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Inventory implements IInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @SearchFilter
    @ManyToMany
    private List<Hotel> hotels = new ArrayList<>();

    @SearchFilter
    private String name;

    @Ignored
    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL)
    private List<InventoryLine> lines = new ArrayList<>();

    @Ignored
    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL)
    private List<InventoryOperation> operations = new ArrayList<>();
}
