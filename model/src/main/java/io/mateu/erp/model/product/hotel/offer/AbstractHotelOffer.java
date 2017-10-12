package io.mateu.erp.model.product.hotel.offer;

import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.product.hotel.BoardType;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.erp.model.product.hotel.RoomType;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "hoteloffer")
public class AbstractHotelOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    private String name;

    private boolean includedInContractPdf;

    private boolean prepayment;

    private boolean active;

    private LocalDate bookingWindowFrom;
    private LocalDate bookingWindowTo;


    @Convert(converter = DatesRangeListConverter.class)
    private DatesRanges checkinDates = new DatesRanges();


    @Convert(converter = DatesRangeListConverter.class)
    private DatesRanges stayDates = new DatesRanges();

    private LocalDate lastCheckout;

    private int applicationMinimumNights;
    private int applicationMaximumStay;
    private int applicationRelease;

    private int minimumStayOverride;
    private int releaseOverride;

    private boolean onRoom;
    private boolean onBoardBasis;
    private boolean onDiscounts;

    private boolean extras;
    private String extrasDescription;

    @ManyToMany
    private List<Hotel> hotels = new ArrayList<>();

    @ManyToMany
    private List<HotelContract> contracts = new ArrayList<>();

    @OneToMany
    private List<Actor> targets = new ArrayList<>();

    @OneToMany
    private List<RoomType> rooms = new ArrayList<>();

    @OneToMany
    private List<BoardType> boards = new ArrayList<>();

    @ManyToMany
    private List<AbstractHotelOffer> cumulativeTo = new ArrayList<>();



}
