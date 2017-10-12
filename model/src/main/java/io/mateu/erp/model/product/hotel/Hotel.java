package io.mateu.erp.model.product.hotel;

import io.mateu.erp.dispo.interfaces.portfolio.IHotel;
import io.mateu.erp.dispo.interfaces.product.IStopSaleLine;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.erp.model.product.hotel.offer.AbstractHotelOffer;
import io.mateu.erp.model.world.City;
import io.mateu.ui.mdd.server.annotations.Ignored;
import io.mateu.ui.mdd.server.annotations.ListColumn;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Getter
@Setter
public class Hotel implements IHotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ListColumn
    private String name;

    @ManyToOne
    private City city;

    private String lon;

    private String lat;

    private boolean active;

    @ManyToOne
    private HotelCategory category;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
    @Ignored
    private List<Room> rooms = new ArrayList<>();

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
    @Ignored
    private List<Board> boards = new ArrayList<>();

    @ManyToOne
    @Ignored
    private StopSales stopSales;

    @ManyToMany(mappedBy = "hotels")
    @Ignored
    private List<Inventory> inventories = new ArrayList<>();

    @ManyToMany(mappedBy = "hotels")
    @Ignored
    private List<HotelContract> contracts = new ArrayList<>();

    @ManyToMany(mappedBy = "hotels")
    @Ignored
    private List<AbstractHotelOffer> offers = new ArrayList<>();




    @Override
    public String getCategoryId() {
        return (getCategory() != null)?getCategory().getCode():null;
    }

    @Override
    public String getCategoryName() {
        return (getCategory() != null)?getCategory().getName().getEs():null;
    }

    @Override
    public List<? extends IStopSaleLine> getStopSalesLines() {
        return getStopSales().getLines();
    }
}
