package io.mateu.erp.model.product.hotel;

import io.mateu.erp.dispo.interfaces.product.IInventoryLine;
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
    private Inventory inventory;

    private LocalDate start;

    @Column(name = "_end")
    private LocalDate end;

    private int quantity;

    @ManyToOne
    private RoomType room;

    @Override
    public String getRoomCode() {
        return getRoom().getCode();
    }
}