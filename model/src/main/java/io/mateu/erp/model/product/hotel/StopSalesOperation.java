package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
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
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class StopSalesOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @SearchFilter
    @NotInEditor
    private LocalDateTime created = LocalDateTime.now();

    @SearchFilter
    @ManyToOne
    @NotInEditor
    @Keep
    private io.mateu.erp.model.authentication.User createdBy;

    @SearchFilter
    @ManyToOne
    @Output
    @Keep
    private StopSales stopSales;

    @SearchFilter
    private LocalDate start;

    @Column(name = "_end")
    private LocalDate end;

    @SearchFilter
    @NotNull
    @Keep
    private StopSalesAction action;

    private boolean onNormalInventory = true;
    private boolean onSecurityInventory;

    @SearchFilter
    @OneToMany
    private List<RoomType> rooms = new ArrayList<>();

    @SearchFilter
    @OneToMany
    private List<Partner> actors = new ArrayList<>();

    @SearchFilter
    @OneToMany
    private List<HotelContract> contracts = new ArrayList<>();


    @PostPersist@PostUpdate@PostRemove
    public void post() {
        WorkflowEngine.add(new Runnable() {
            @Override
            public void run() {
                try {
                    Helper.transact(em -> em.find(StopSales.class, getStopSales().getId()).build(em));
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
    }
}
