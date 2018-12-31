package io.mateu.erp.model.booking.hotel;

import com.google.common.collect.Lists;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import io.mateu.erp.model.booking.parts.HotelBookingLine;
import io.mateu.erp.model.product.ContractType;
import io.mateu.erp.model.product.hotel.Board;
import io.mateu.erp.model.product.hotel.Inventory;
import io.mateu.erp.model.product.hotel.Room;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.mdd.core.annotations.DependsOn;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.KPI;
import io.mateu.mdd.core.annotations.Output;
import io.mateu.mdd.core.model.util.IntArrayAttributeConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
public class HotelServiceLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private HotelService service;

    @NotNull
    private LocalDate start;

    @Column(name = "_end")
    @NotNull
    private LocalDate end;

    @ManyToOne
    @NotNull
    private Room room;

    @ManyToOne
    @NotNull
    private Board board;

    @NotNull
    private int numberOfRooms;

    @NotNull
    private int adultsPerRoom;

    @NotNull
    private int childrenPerRoom;

    @Convert(converter = IntArrayAttributeConverter.class)
    @Column(name = "_ages")
    private int[] ages;

    @Output
    private String validationMessages;

    @KPI
    private boolean active;

    @KPI
    private boolean occupationOk;

    @KPI
    private boolean salesClosed;

    @KPI
    private boolean enoughRooms;

    @KPI
    private boolean release;

    @KPI
    private boolean minStay;

    @KPI
    private boolean weekDays;

    @KPI
    private int roomsLeft;

    @KPI
    private double value;

    @KPI
    private boolean valued;

    @KPI
    private boolean available;


    @ManyToOne
    private HotelContract contract;

    public DataProvider getContractDataProvider() {
        return new ListDataProvider(service != null && service.getHotel() != null?service.getHotel().getContracts().stream().filter(c -> ContractType.PURCHASE.equals(c.getType())).collect(Collectors.toList()):new ArrayList());
    }

    @ManyToOne
    private Inventory inventory;

    @Ignored
    private transient Inventory oldInventory;

    @DependsOn("contract")
    public DataProvider getInventoryDataProvider() {
        if (contract != null) return new ListDataProvider(Lists.newArrayList(contract.getInventory()));
        else return new ListDataProvider(service != null && service.getHotel() != null?service.getHotel().getInventories():new ArrayList());
    }




    public HotelServiceLine() {

    }

    public HotelServiceLine(HotelService service, HotelBookingLine l) {
        this.service = service;
        start = l.getStart();
        end = l.getEnd();
        room = l.getRoom();
        board = l.getBoard();
        numberOfRooms = l.getRooms();
        adultsPerRoom = l.getAdultsPerRoon();
        childrenPerRoom = l.getChildrenPerRoom();
        ages = l.getAges();
        active = l.isActive();
    }



    @Override
    public String toString() {
        String h = "<table style='border-spacing: 0px; border-top: 1px dashed grey; border-bottom: 1px dashed grey;'>";
        h += "<tr><th width='150px'>Dates:</th><td>From " + start + " to " + end + "</td></tr>";
        h += "<tr><th>Nr of rooms:</th><td>" + numberOfRooms + "</td></tr>";
        h += "<tr><th>Room type:</th><td>" + room + "</td></tr>";
        h += "<tr><th>Board type:</th><td>" + board + "</td></tr>";
        h += "<tr><th>Pax per room:</th><td>" + adultsPerRoom + " adults + " +  childrenPerRoom + " children</td></tr>";
        h += "<tr><th>Children ages:</th><td>" + (ages != null?Arrays.toString(ages):"-") + "</td></tr>";
        h += "<tr><th>Contract:</th><td>" + (contract != null ? contract : "NO CONTRACT") + "</td></tr>";
        h += "<tr><th>Inventory:</th><td>" + (inventory != null ? inventory : "NO INVENTORY") + "</td></tr>";
        h += "<tr><th>Available:</th><td>" + (available?"YES":"ON REQUEST") + "</td></tr>";
        h += "<tr><th>Value:</th><td>" + (valued?value:"-") + "</td></tr>";
        h += "</table>";
        return h;
    }
}
