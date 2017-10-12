package io.mateu.erp.model.product.hotel;

import io.mateu.erp.dispo.interfaces.product.IStopSaleLine;
import io.mateu.erp.model.financials.Actor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class StopSalesLine implements IStopSaleLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

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

    @Override
    public List<String> getRoomIds() {
        List<String> l = new ArrayList<>();
        for (RoomType r : getRooms()) l.add(r.getCode());
        return l;
    }

    @Override
    public List<Long> getActorIds() {
        List<Long> l = new ArrayList<>();
        for (Actor a : getActors()) l.add(a.getId());
        return l;
    }
}
