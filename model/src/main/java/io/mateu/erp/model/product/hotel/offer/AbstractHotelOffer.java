package io.mateu.erp.model.product.hotel.offer;

import io.mateu.erp.dispo.CondicionesPorRegimen;
import io.mateu.erp.dispo.LineaReserva;
import io.mateu.erp.dispo.ValoracionLineaReserva;
import io.mateu.erp.dispo.interfaces.product.IBoard;
import io.mateu.erp.dispo.interfaces.product.IHotelOffer;
import io.mateu.erp.dispo.interfaces.product.IRoom;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.product.hotel.BoardType;
import io.mateu.erp.model.product.hotel.DatesRanges;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.erp.model.product.hotel.RoomType;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.ui.mdd.server.annotations.ListColumn;
import io.mateu.ui.mdd.server.annotations.SearchFilter;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.persistence.annotations.CacheIndex;
import org.eclipse.persistence.config.QueryHints;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "hoteloffer")
@NamedQueries(
        @NamedQuery( name = "AbstractHotelOffer.getByQuoonId", query = "select h from io.mateu.erp.model.product.hotel.offer.AbstractHotelOffer h where h.quoonId = :qid",
                hints={
                        @QueryHint(name= QueryHints.QUERY_RESULTS_CACHE, value="TRUE"),
                        @QueryHint(name= QueryHints.QUERY_RESULTS_CACHE_SIZE, value="500")
                })
)
public class AbstractHotelOffer implements IHotelOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @org.eclipse.persistence.annotations.Index
    @CacheIndex
    private String quoonId;

    @SearchFilter
    @ListColumn
    private String name;

    @ListColumn
    private boolean includedInContractPdf;

    @ListColumn
    private boolean prepayment;

    @ListColumn
    private boolean active;

    @ListColumn
    private LocalDate bookingWindowFrom;
    @ListColumn
    private LocalDate bookingWindowTo;


    @Convert(converter = DatesRangeListConverter.class)
    private DatesRanges checkinDates = new DatesRanges();

    @Convert(converter = DatesRangeListConverter.class)
    private DatesRanges stayDates = new DatesRanges();

    private LocalDate lastCheckout;

    @ListColumn
    private int applicationMinimumNights;
    @ListColumn
    private int applicationMaximumStay;
    @ListColumn
    private int applicationRelease;

    private int minimumStayOverride;
    private int releaseOverride;

    private boolean onRoom;
    private boolean onBoardBasis;
    private boolean onDiscounts;

    private boolean extras;
    private String extrasDescription;

    @SearchFilter
    @ManyToMany
    private List<Hotel> hotels = new ArrayList<>();

    @SearchFilter
    @ManyToMany
    private List<HotelContract> contracts = new ArrayList<>();

    @SearchFilter
    @OneToMany
    private List<Actor> targets = new ArrayList<>();

    @SearchFilter
    @OneToMany
    private List<RoomType> rooms = new ArrayList<>();

    @SearchFilter
    @OneToMany
    private List<BoardType> boards = new ArrayList<>();

    @SearchFilter
    @ManyToMany
    private List<AbstractHotelOffer> cumulativeTo = new ArrayList<>();


    public static AbstractHotelOffer getByQuoonId(EntityManager em, String quoonId) {
        AbstractHotelOffer h = null;
        try {
            h = (AbstractHotelOffer) em.createNamedQuery("AbstractHotelOffer.getByQuoonId").setParameter("qid", quoonId).getResultList().get(0);
        } catch (Exception e) {
        }
        return h;
    }



    @Override
    public double aplicar(IBoard board, IRoom room, LineaReserva lineaReserva, ValoracionLineaReserva vlr, IHotelOffer o, CondicionesPorRegimen cpr) {



        return 0;
    }
}
