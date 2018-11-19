package io.mateu.erp.model.product.hotel;

import io.mateu.erp.dispo.interfaces.portfolio.IHotel;
import io.mateu.erp.dispo.interfaces.product.IStopSaleLine;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.product.AbstractProduct;
import io.mateu.erp.model.world.Zone;
import io.mateu.erp.model.mdd.ActiveCellStyleGenerator;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.erp.model.product.hotel.offer.AbstractHotelOffer;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;

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
public class Hotel extends AbstractProduct implements IHotel {

    private String lon;

    private String lat;

    private String address;

    private String zip;

    private String telephone;

    private String fax;

    private String email;

    @ManyToOne
    @NotNull
    private HotelCategory category;

    @ManyToOne
    private Partner hotelChain;


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


    @ManyToOne
    private TransferPoint transferPoint;



    @Section("Related")
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
    @UseLinkToListView(addEnabled = true, deleteEnabled = true)
    private List<Room> rooms = new ArrayList<>();

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
    @UseLinkToListView(addEnabled = true, deleteEnabled = true)
    private List<Board> boards = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL)
    @Output
    private StopSales stopSales;

    @OneToMany(mappedBy = "hotel")
    @UseLinkToListView(addEnabled = true, deleteEnabled = true)
    private List<Inventory> inventories = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL)
    @Output
    private Inventory realInventory;

    @OneToMany(mappedBy = "hotel")
    @UseLinkToListView(addEnabled = true, deleteEnabled = true)
    private List<HotelContract> contracts = new ArrayList<>();

    @OneToMany(mappedBy = "hotel")
    @UseLinkToListView(addEnabled = true, deleteEnabled = true)
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



    @PrePersist@PreUpdate
    public void afterSet() throws Exception, Throwable {

        EntityManager em = Helper.getEMFromThreadLocal();

        if (getStopSales() == null) {
            setStopSales(new StopSales());
            getStopSales().setHotel(this);
            em.persist(getStopSales());
        }

        if (getRealInventory() == null) {
            setRealInventory(new Inventory());
            getRealInventory().setHotel(this);
            getRealInventory().setName("Real inventory");
            em.persist(getRealInventory());
        }

    }

}
