package io.mateu.erp.model.product.hotel;

import io.mateu.erp.dispo.interfaces.portfolio.IHotel;
import io.mateu.erp.dispo.interfaces.product.IStopSaleLine;
import io.mateu.erp.model.mdd.ActiveCellStyleGenerator;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.erp.model.product.hotel.offer.AbstractHotelOffer;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.erp.model.world.City;
import io.mateu.ui.mdd.server.annotations.*;
import io.mateu.ui.mdd.server.interfaces.WithTriggers;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.persistence.annotations.CacheIndex;
import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.config.QueryHints;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Getter
@Setter
public class Hotel implements IHotel, WithTriggers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Tab("General")
    @ListColumn
    @NotNull
    @SearchFilter
    private String name;

    @ManyToOne
    @NotNull
    private HotelCategory category;

    @ManyToOne
    private HotelChain chain;

    @ManyToOne
    @NotNull
    @ListColumn
    @SearchFilter
    private Office office;


    @ListColumn
    @CellStyleGenerator(ActiveCellStyleGenerator.class)
    private boolean active;

    @Tab("Location")
    @ManyToOne
    @NotNull
    @ListColumn
    @SearchFilter
    private City city;

    private String lon;

    private String lat;

    private String address;

    private String zip;

    private String telephone;

    private String fax;

    private String email;

    @Tab("Ages")
    /**
     * inclusive
     */
    private int childStartAge;

    /**
     * inclusive
     */
    private int juniorStartAge;

    /**
     * inclusive
     */
    private int adultStartAge;


    @Tab("Transfers")
    @ManyToOne
    private TransferPoint transferPoint;



    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
    @Ignored
    private List<Room> rooms = new ArrayList<>();

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
    @Ignored
    private List<Board> boards = new ArrayList<>();

    @ManyToOne
    @Output
    private StopSales stopSales;

    @OneToMany(mappedBy = "hotel")
    @Ignored
    private List<Inventory> inventories = new ArrayList<>();

    @ManyToOne
    @Output
    private Inventory realInventory;

    @OneToMany(mappedBy = "hotel")
    @Ignored
    private List<HotelContract> contracts = new ArrayList<>();

    @OneToMany(mappedBy = "hotel")
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


    @Override
    public void beforeSet(EntityManager entityManager, boolean b) throws Throwable {

    }

    @Override
    public void afterSet(EntityManager entityManager, boolean b) throws Exception, Throwable {
        if (getStopSales() == null) {
            setStopSales(new StopSales());
            getStopSales().setHotel(this);
            entityManager.persist(getStopSales());
        }

        if (getRealInventory() == null) {
            setRealInventory(new Inventory());
            getRealInventory().setHotel(this);
            getRealInventory().setName("Real inventory");
            entityManager.persist(getRealInventory());
        }
    }

    @Override
    public void beforeDelete(EntityManager entityManager) throws Throwable {

    }

    @Override
    public void afterDelete(EntityManager entityManager) throws Throwable {

    }
}
