package io.mateu.erp.model.product.hotel;

import io.mateu.erp.dispo.interfaces.product.IInventory;
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

    @ManyToMany
    private List<Hotel> hotels = new ArrayList<>();

    private String name;

    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL)
    private List<InventoryLine> lines = new ArrayList<>();

    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL)
    private List<InventoryOperation> operations = new ArrayList<>();
}
