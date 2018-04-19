package io.mateu.erp.model.product.hotel;

import io.mateu.erp.dispo.interfaces.portfolio.IHotel;
import io.mateu.erp.dispo.interfaces.product.IStopSaleLine;
import io.mateu.erp.model.mdd.ActiveCellStyleGenerator;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.partners.Actor;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.erp.model.product.hotel.offer.AbstractHotelOffer;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.erp.model.util.Helper;
import io.mateu.erp.model.util.JPATransaction;
import io.mateu.erp.model.world.City;
import io.mateu.ui.mdd.server.annotations.*;
import io.mateu.ui.mdd.server.workflow.WorkflowEngine;
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
public class Hotel implements IHotel {

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
    private Actor hotelChain;

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



    @PostPersist@PostUpdate
    public void afterSet() throws Exception, Throwable {

        EntityManager em = io.mateu.ui.mdd.server.util.Helper.getEMFromThreadLocal();

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
        
        /*

        WorkflowEngine.add(new Runnable() {

            long hotelId = getId();

            @Override
            public void run() {

                try {
                    Helper.transact(new JPATransaction() {
                        @Override
                        public void run(EntityManager em) throws Throwable {

                            Hotel h = em.find(Hotel.class, hotelId);


                            if (h.getStopSales() == null) {
                                h.setStopSales(new StopSales());
                                h.getStopSales().setHotel(h);
                                em.persist(h.getStopSales());
                            }

                            if (h.getRealInventory() == null) {
                                h.setRealInventory(new Inventory());
                                h.getRealInventory().setHotel(h);
                                h.getRealInventory().setName("Real inventory");
                                em.persist(h.getRealInventory());
                            }


                        }
                    });
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

            }
        });
        
        */

    }

}
