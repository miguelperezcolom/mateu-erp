package io.mateu.erp.model.product.hotel.contracting;

import io.mateu.erp.dispo.interfaces.product.IHotelContract;
import io.mateu.erp.model.product.AbstractContract;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.erp.model.product.hotel.HotelContractPhoto;
import io.mateu.erp.model.product.hotel.RoomType;
import io.mateu.erp.model.product.hotel.offer.AbstractHotelOffer;
import io.mateu.ui.mdd.server.annotations.SearchFilter;
import io.mateu.ui.mdd.server.annotations.Tab;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.persistence.annotations.CacheIndex;
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
        @NamedQuery( name = "HotelContract.getByQuoonId", query = "select h from io.mateu.erp.model.product.hotel.contracting.HotelContract h where h.quoonId = :qid",
                hints={
                        @QueryHint(name= QueryHints.QUERY_RESULTS_CACHE, value="TRUE"),
                        @QueryHint(name= QueryHints.QUERY_RESULTS_CACHE_SIZE, value="500")
                })
)
public class HotelContract extends AbstractContract implements IHotelContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @org.eclipse.persistence.annotations.Index
    @CacheIndex
    private String quoonId;

    @SearchFilter
    @Tab("Related to")
    @ManyToMany
    private List<Hotel> hotels = new ArrayList<>();

    @SearchFilter
    @ManyToMany(mappedBy = "contracts")
    private List<AbstractHotelOffer> offers = new ArrayList<>();

    @Tab("Terms")
    @Column(name = "terms2")
    @Convert(converter = HotelContractPhotoConverter.class)
    private HotelContractPhoto terms;

    public static HotelContract getByQuoonId(EntityManager em, String quoonId) {
        HotelContract h = null;
        try {
            h = (HotelContract) em.createNamedQuery("HotelContract.getByQuoonId").setParameter("qid", quoonId).getResultList().get(0);
        } catch (Exception e) {
        }
        return h;
    }
}
