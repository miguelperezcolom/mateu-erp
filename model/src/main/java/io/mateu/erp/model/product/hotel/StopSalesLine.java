package io.mateu.erp.model.product.hotel;

import io.mateu.erp.dispo.interfaces.product.IStopSaleLine;
import io.mateu.erp.model.partners.Actor;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.ui.mdd.server.annotations.SearchFilter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
public class StopSalesLine implements IStopSaleLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @SearchFilter
    @ManyToOne
    private StopSales stopSales;

    @SearchFilter
    private LocalDate start;

    @SearchFilter
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

    @Override
    public List<String> getRoomIds() {
        return getRooms().stream().map((a) -> a.getCode()).collect(Collectors.toList());

    }

    @Override
    public List<Long> getActorIds() {
        return getActors().stream().map((a) -> a.getId()).collect(Collectors.toList());
    }

    @Override
    public List<Long> getContractIds() {
        return getContracts().stream().map((a) -> a.getId()).collect(Collectors.toList());
    }
}
