package io.mateu.erp.model.product.hotel.offer;

import io.mateu.erp.dispo.Condiciones;
import io.mateu.erp.dispo.LineaReserva;
import io.mateu.erp.dispo.ValoracionLineaReserva;
import io.mateu.erp.dispo.interfaces.product.IBoard;
import io.mateu.erp.dispo.interfaces.product.IHotelOffer;
import io.mateu.erp.dispo.interfaces.product.IRoom;
import io.mateu.erp.model.product.DatesRanges;
import io.mateu.erp.model.product.hotel.BoardType;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.erp.model.product.hotel.RoomType;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.mdd.core.annotations.ListColumn;
import io.mateu.mdd.core.annotations.SearchFilter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "hoteloffer")
public class AbstractHotelOffer implements IHotelOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

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
    @ManyToOne
    @NotNull
    private Hotel hotel;

    @SearchFilter
    @ManyToMany
    private List<HotelContract> contracts = new ArrayList<>();

    @SearchFilter
    @OneToMany
    private List<Partner> targets = new ArrayList<>();

    @SearchFilter
    @OneToMany
    private List<RoomType> rooms = new ArrayList<>();

    @SearchFilter
    @OneToMany
    private List<BoardType> boards = new ArrayList<>();

    @SearchFilter
    @ManyToMany
    private List<AbstractHotelOffer> cumulativeTo = new ArrayList<>();


    @Override
    public double aplicar(IBoard board, IRoom room, LineaReserva lineaReserva, ValoracionLineaReserva vlr, IHotelOffer o, Condiciones cpr) {



        return 0;
    }
}
