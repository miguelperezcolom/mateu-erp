package io.mateu.erp.model.product.hotel;

import io.mateu.erp.dispo.interfaces.portfolio.IHotel;
import io.mateu.erp.dispo.interfaces.product.IStopSaleLine;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.erp.model.product.hotel.offer.AbstractHotelOffer;
import io.mateu.erp.model.util.Helper;
import io.mateu.erp.model.util.JPATransaction;
import io.mateu.erp.model.world.City;
import io.mateu.ui.mdd.server.annotations.Ignored;
import io.mateu.ui.mdd.server.annotations.ListColumn;
import io.mateu.ui.mdd.server.annotations.Required;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.persistence.annotations.CacheIndex;
import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.config.QueryHints;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Getter
@Setter
@NamedQueries(
        @NamedQuery( name = "Hotel.getByQuoonId", query = "select h from io.mateu.erp.model.product.hotel.Hotel h where h.quoonId = :qid",
                hints={
                        @QueryHint(name= QueryHints.QUERY_RESULTS_CACHE, value="TRUE"),
                        @QueryHint(name= QueryHints.QUERY_RESULTS_CACHE_SIZE, value="500")
                })
)
public class Hotel implements IHotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ListColumn
    @Required
    private String name;

    @ManyToOne
    @Required
    private Office office;

    @ManyToOne
    @Required
    private City city;

    private String lon;

    private String lat;

    private boolean active;

    @Index
    @CacheIndex
    private String quoonId;

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




    public static Hotel getByQuoonId(EntityManager em, String quoonId) {
        Hotel h = null;
        try {
            h = (Hotel) em.createNamedQuery("Hotel.getByQuoonId").setParameter("qid", quoonId).getSingleResult();
        } catch (Exception e) {
        }
        return h;
    }

}
