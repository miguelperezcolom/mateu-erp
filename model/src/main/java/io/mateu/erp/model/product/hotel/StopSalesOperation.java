package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.product.hotel.RoomType;
import io.mateu.erp.model.partners.Actor;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.ui.mdd.server.annotations.SearchFilter;
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

    @SearchFilter
    private LocalDateTime created = LocalDateTime.now();

    @SearchFilter
    @ManyToOne
    private io.mateu.erp.model.authentication.User createdBy;

    @SearchFilter
    @ManyToOne
    private StopSales stopSales;

    @SearchFilter
    private LocalDate start;

    @Column(name = "_end")
    private LocalDate end;

    private boolean onNormalInventory = true;
    private boolean onSecurityInventory;

    @SearchFilter
    @OneToMany
    private List<RoomType> rooms = new ArrayList<>();

    @SearchFilter
    @OneToMany
    private List<Actor> actors = new ArrayList<>();

    @SearchFilter
    @OneToMany
    private List<HotelContract> contracts = new ArrayList<>();

    @SearchFilter
    private StopSalesAction action;

}
