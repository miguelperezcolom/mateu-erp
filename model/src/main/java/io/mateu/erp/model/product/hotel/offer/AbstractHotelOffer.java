package io.mateu.erp.model.product.hotel.offer;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import io.mateu.erp.dispo.Condiciones;
import io.mateu.erp.dispo.LineaReserva;
import io.mateu.erp.dispo.ValoracionLineaReserva;
import io.mateu.erp.dispo.interfaces.product.IBoard;
import io.mateu.erp.dispo.interfaces.product.IHotelOffer;
import io.mateu.erp.dispo.interfaces.product.IRoom;
import io.mateu.erp.model.booking.parts.HotelBookingLine;
import io.mateu.erp.model.partners.Agency;
import io.mateu.erp.model.product.DatesRanges;
import io.mateu.erp.model.product.hotel.BoardType;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.erp.model.product.hotel.RoomType;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.model.multilanguage.Literal;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Element;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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


    @TextArea
    @ManyToOne
    private Literal description;


    private boolean extras;
    @TextArea
    @ManyToOne
    private Literal extrasDescription;

    @SearchFilter
    @OneToMany
    @UseChips
    private Set<Agency> targets = new HashSet<>();


    @SearchFilter
    @ManyToOne
    @NotNull
    private Hotel hotel;

    @SearchFilter
    @ManyToMany
    @UseCheckboxes
    private Set<HotelContract> contracts = new HashSet<>();

    @DependsOn("hotel")
    public DataProvider getContractsDataProvider() {
        return new ListDataProvider(hotel != null?hotel.getContracts():new ArrayList());
    }

    @SearchFilter
    @OneToMany
    @UseCheckboxes
    private Set<RoomType> rooms = new HashSet<>();

    @DependsOn("hotel")
    public DataProvider getRoomsDataProvider() {
        return new ListDataProvider(hotel != null?hotel.getRooms().stream().map(r -> r.getType()).collect(Collectors.toList()):new ArrayList());
    }

    @SearchFilter
    @OneToMany
    @UseCheckboxes
    private Set<BoardType> boards = new HashSet<>();

    @DependsOn("hotel")
    public DataProvider getBoardsDataProvider() {
        return new ListDataProvider(hotel != null?hotel.getBoards().stream().map(r -> r.getType()).collect(Collectors.toList()):new ArrayList());
    }

    @SearchFilter
    @ManyToMany
    @UseCheckboxes
    private Set<AbstractHotelOffer> cumulativeTo = new HashSet<>();


    @DependsOn("hotel")
    public DataProvider getCumulativeToDataProvider() {
        return new ListDataProvider(hotel != null?hotel.getOffers().stream().filter(o -> !o.equals(this)).collect(Collectors.toList()):new ArrayList());
    }


    @Override
    public String toString() {
        return name;
    }

    @Override
    public double aplicar(IBoard board, IRoom room, LineaReserva lineaReserva, ValoracionLineaReserva vlr, IHotelOffer o, Condiciones cpr) {



        return 0;
    }

    public double aplicar(HotelBookingLine l, double[][] valorEstancia, double[][] valorRegimen, double[] valorOfertas) {
        return 0;
    }


    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj != null && obj instanceof  AbstractHotelOffer && id == ((AbstractHotelOffer)obj).id);
    }

    public Element toXml() {
        Element xml = new Element("offer");
        String d = "" + name + ".";
        if (prepayment) d += " When this offer is applied the bookings become PREPAYMENT.";
        if (bookingWindowFrom != null || bookingWindowTo != null) d += " Booking window: " + (bookingWindowFrom != null?bookingWindowFrom.format(DateTimeFormatter.ISO_DATE):"") + " - " + (bookingWindowTo != null?bookingWindowTo.format(DateTimeFormatter.ISO_DATE):"") + ".";

        xml.setAttribute("description", d);
        return xml;
    }
}
