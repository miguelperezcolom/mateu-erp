package io.mateu.erp.model.product.hotel;

import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.model.authentication.User;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.workflow.Task;
import io.mateu.mdd.core.workflow.WorkflowEngine;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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
    @Keep
    @Unmodifiable
    @NoChart
    @NotNull
    private Inventory inventory;


    @SearchFilter
    @NotInEditor
    private LocalDateTime created = LocalDateTime.now();

    @ManyToOne
    @NotInEditor
    @NoChart
    private User createdBy;

    private LocalDate start;
    @Column(name = "_end")
    private LocalDate end;

    @ManyToOne
    @SearchFilter
    @NotNull
    private RoomType room;
    private int quantity;

    @NotNull
    private InventoryAction action;


    public InventoryOperation() {

    }

    public InventoryOperation(RoomType room, int quantity, InventoryAction action, LocalDate start, LocalDate end) {
        this.room = room;
        this.quantity = quantity;
        this.action = action;
        this.start = start;
        this.end = end;
    }


    @PostPersist@PostUpdate@PostRemove
    public void post() {
        WorkflowEngine.add(new Task() {
            @Override
            public void run() {
                try {
                    Helper.transact(em -> em.find(Inventory.class, getInventory().getId()).build(em));
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
    }


    @Override
    public String toString() {
        return "" + action + " from " + start + " to " + end + " on " + room + (createdBy != null?" by " + createdBy.getLogin():"");
    }
}
