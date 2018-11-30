package io.mateu.erp.model.booking.parts;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.product.ContractType;
import io.mateu.erp.model.product.hotel.Board;
import io.mateu.erp.model.product.hotel.Inventory;
import io.mateu.erp.model.product.hotel.Room;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.mdd.core.annotations.DependsOn;
import io.mateu.mdd.core.annotations.Output;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Entity
@Getter@Setter
public class HotelBookingLine {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    @Output
    private HotelBooking booking;

    public void setBooking(HotelBooking booking) {
        this.booking = booking;
        if (start == null && booking.getStart() != null) start = booking.getStart();
        if (end == null && booking.getEnd() != null) end = booking.getEnd();
    }

    @NotNull
    private LocalDate start;
    @NotNull@Column(name = "_end")
    private LocalDate end;

    @ManyToOne
    private Room room;

    public DataProvider getRoomDataProvider() {
        return new ListDataProvider(booking != null && booking.getHotel() != null?booking.getHotel().getRooms().stream().map(r -> r.getType()).collect(Collectors.toList()):new ArrayList());
    }

    @ManyToOne
    private Board board;

    public DataProvider getBoardDataProvider() {
        return new ListDataProvider(booking != null && booking.getHotel() != null?booking.getHotel().getBoards().stream().map(r -> r.getType()).collect(Collectors.toList()):new ArrayList());
    }

    private int rooms;
    private int adultsPerRoon;
    private int childrenPerRoom;
    private int[] ages;

    private boolean active = true;

    @ManyToOne
    private HotelContract contract;

    public DataProvider getContractDataProvider() {
        return new ListDataProvider(booking != null && booking.getHotel() != null?booking.getHotel().getContracts().stream().filter(c -> ContractType.SALE.equals(c.getType())).collect(Collectors.toList()):new ArrayList());
    }

    @ManyToOne
    private Inventory inventory;

    public DataProvider getInventoryDataProvider() {
        return new ListDataProvider(booking != null && booking.getHotel() != null?booking.getHotel().getInventories():new ArrayList());
    }


}
