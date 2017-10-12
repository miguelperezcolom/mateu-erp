package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.financials.Actor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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

    private LocalDateTime created;
    @ManyToOne
    private User createdBy;


    @ManyToOne
    private StopSales stopSales;

    private LocalDate start;

    @Column(name = "_end")
    private LocalDate end;

    private boolean onNormalInventory = true;
    private boolean onSecurityInventory;

    @OneToMany
    private List<RoomType> rooms = new ArrayList<>();

    @OneToMany
    private List<Actor> actors = new ArrayList<>();

    private StopSalesAction action;

}
