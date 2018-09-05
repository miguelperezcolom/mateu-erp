package io.mateu.erp.model.product.hotel;

import io.mateu.mdd.core.annotations.Keep;
import io.mateu.mdd.core.annotations.NotInEditor;
import io.mateu.mdd.core.annotations.Output;
import io.mateu.mdd.core.annotations.SearchFilter;
import io.mateu.mdd.core.util.Helper;
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
    @Output
    private Inventory inventory;


    @SearchFilter
    @NotInEditor
    private LocalDateTime created = LocalDateTime.now();

    @ManyToOne
    @NotInEditor
    private io.mateu.erp.model.authentication.User createdBy;

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



    @PostPersist@PostUpdate@PostRemove
    public void post() {
        WorkflowEngine.add(new Runnable() {
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
}
