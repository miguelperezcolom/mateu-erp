package io.mateu.erp.model.product.hotel;

import io.mateu.erp.dispo.interfaces.product.IInventoryLine;
import io.mateu.mdd.core.annotations.SearchFilter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class InventoryLine implements IInventoryLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @SearchFilter
    private Inventory inventory;

    @SearchFilter
    private LocalDate start;

    @Column(name = "_end")
    private LocalDate end;

    private int quantity;

    @ManyToOne
    @SearchFilter
    private RoomType room;

    @Override
    public String getRoomCode() {
        return getRoom().getCode();
    }
}
