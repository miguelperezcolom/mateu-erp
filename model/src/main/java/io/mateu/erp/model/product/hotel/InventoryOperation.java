package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.authentication.User;
import io.mateu.ui.mdd.server.annotations.SearchFilter;
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
    @SearchFilter
    private Inventory inventory;


    @SearchFilter
    private LocalDateTime created = LocalDateTime.now();

    @ManyToOne
    private User createdBy;

    private LocalDate start;
    @Column(name = "_end")
    private LocalDate end;

    @ManyToOne
    @SearchFilter
    private RoomType room;
    private int quantity;

    private InventoryAction action;
}
