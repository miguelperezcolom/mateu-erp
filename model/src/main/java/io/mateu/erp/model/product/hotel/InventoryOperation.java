package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.authentication.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class InventoryOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Inventory inventory;


    private LocalDateTime created;
    @ManyToOne
    private User createdBy;

    private LocalDate start;
    @Column(name = "_end")
    private LocalDate end;

    @ManyToOne
    private RoomType room;
    private int quantity;

    private InventoryAction action;
}
