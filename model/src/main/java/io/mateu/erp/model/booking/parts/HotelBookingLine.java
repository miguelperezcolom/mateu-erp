package io.mateu.erp.model.booking.parts;

import com.google.common.collect.Lists;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.ValidationStatus;
import io.mateu.erp.model.product.ContractType;
import io.mateu.erp.model.product.hotel.*;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.DependsOn;
import io.mateu.mdd.core.annotations.KPI;
import io.mateu.mdd.core.annotations.Output;
import io.mateu.mdd.core.util.DatesRange;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.zip.Adler32;

import static java.time.temporal.ChronoUnit.DAYS;

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

    @ManyToOne@NotNull
    private Room room;

    public DataProvider getRoomDataProvider() {
        return new ListDataProvider(booking != null && booking.getHotel() != null?booking.getHotel().getRooms():new ArrayList());
    }

    @ManyToOne@NotNull
    private Board board;

    public DataProvider getBoardDataProvider() {
        return new ListDataProvider(booking != null && booking.getHotel() != null?booking.getHotel().getBoards():new ArrayList());
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

    @DependsOn("contract")
    public DataProvider getInventoryDataProvider() {
        if (contract != null) return new ListDataProvider(Lists.newArrayList(contract.getInventory()));
        else return new ListDataProvider(booking != null && booking.getHotel() != null?booking.getHotel().getInventories():new ArrayList());
    }


    @Output
    private String validationMessages;


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

    @Action()
    public void check() {

        occupationOk = true;
        salesClosed = true;
        enoughRooms = true;
        release = true;
        minStay = true;
        weekDays = true;

        LocalDate effectiveEnd = end != null?end.minusDays(1):null;

        if (booking.getHotel() != null) {

            if (room != null) {

                int pax = adultsPerRoon + childrenPerRoom;
                int infants = 0;
                if (ages != null) for (int i = 0; i < ages.length; i++) if (ages[i] < 2) infants++;
                infants = infants / rooms;
                int effectiveChildren = childrenPerRoom - infants;

                if (pax < room.getMinPax()) occupationOk = false;
                else if (infants > 0 && !room.isInfantsAllowed()) occupationOk = false;
                else if (effectiveChildren > 0 && !room.isChildrenAllowed()) occupationOk = false;
                else {
                    for (MaxCapacity c : room.getMaxCapacities().getCapacities()) {
                        int resChildren = effectiveChildren;
                        if (room.isInfantsInBed() && infants > c.getInfants()) resChildren += infants - c.getInfants();
                        int resAdults = adultsPerRoon;
                        if (resChildren > c.getChildren()) resAdults += resChildren - c.getChildren();
                        if (c.getAdults() < resAdults) {
                            occupationOk = false;
                            break;
                        }
                    }
                }
            }

            for (StopSalesLine l : booking.getHotel().getStopSales().getLines()) {
                if (start == null || end == null || l.getStart().isBefore(effectiveEnd) && l.getEnd().isAfter(start)) {
                    if (booking.getAgency() == null || l.getActors().size() == 0 || l.getActors().contains(booking.getAgency())) {
                        if (room == null || l.getRooms().size() == 0 || l.getRooms().contains(room.getType())) {
                            if (contract == null || l.getContracts().size() == 0 || l.getContracts().contains(contract)) {
                                salesClosed = false;
                                break;
                            }
                        }
                    }
                }
            }
            if (contract != null) {
                if (start != null) {
                    long releaseReserva = DAYS.between(LocalDate.now(), start);
                    if (booking.getFormalizationDate() != null) releaseReserva = DAYS.between(booking.getFormalizationDate(), start);
                    else if (booking.getAudit() != null && booking.getAudit().getCreated() != null) releaseReserva = DAYS.between(booking.getAudit().getCreated().toLocalDate(), start);
                    if (releaseReserva < 0) release = false;
                    else for (ReleaseRule r : contract.getTerms().getReleaseRules()) {
                        if (start == null || end == null || ((r.getStart() == null || r.getStart().isBefore(effectiveEnd)) && (r.getEnd() == null || r.getEnd().isAfter(start)))) {
                            if (room == null || r.getRooms().size() == 0 || r.getRooms().contains(room.getType().getCode())) {
                                release = releaseReserva <= r.getRelease();
                                break;
                            }
                        }
                    }
                    if (end != null) {
                        long noches = DAYS.between(start, end);
                        for (MinimumStayRule r : contract.getTerms().getMinimumStayRules()) {
                            if (start == null || end == null || ((r.getStart() == null || r.getStart().isBefore(effectiveEnd)) && (r.getEnd() == null || r.getEnd().isAfter(start)))) {
                                if (room == null || r.getRooms().size() == 0 || r.getRooms().contains(room.getType().getCode())) {
                                    if (board == null || r.getBoards().size() == 0 || r.getBoards().contains(board.getType().getCode())) {
                                        minStay = false;
                                        break;
                                    }
                                }
                            }
                        }
                        minStay = true;

                        for (WeekDaysRule r : contract.getTerms().getWeekDaysRules()) {
                            if (start == null || end == null || ((r.getStart() == null || r.getStart().isBefore(effectiveEnd)) && (r.getEnd() == null || r.getEnd().isAfter(start)))) {
                                weekDays = false;
                                break;
                            }
                        }

                    }
                }
            }
            if (inventory != null && start != null && end != null && room != null) {

                int noches = new Long(DAYS.between(start, end)).intValue() - 1;

                int[] cupo = new int[noches];

                for (InventoryLine l : inventory.getLines()) {
                    if (l.getRoom().equals(room.getType())) {
                        if (l.getStart().isBefore(effectiveEnd) && l.getEnd().isAfter(start)) {
                            int desde = new Long(DAYS.between(start, l.getStart())).intValue();
                            if (desde < 0) desde = 0;
                            int hasta = new Long(DAYS.between(start, l.getEnd())).intValue();
                            if (hasta > noches) hasta = noches;
                            for (int i = desde; i < hasta; i++) {
                                cupo[i] += l.getQuantity();
                            }
                        }
                    }
                }

                int max = 0;
                for (int i =  0; i < cupo.length; i++) {
                    if (i == 0 || max > cupo[i]) max = cupo[i];
                }
                roomsLeft = max;
                enoughRooms = rooms <= max;
            }
        }

        validationMessages = "checked! ;)";

        booking.updateData();

    }

    @Action(order = 2)
    public void price() {
        if (start != null && end != null && room != null && board != null && contract != null && contract.getTerms() != null) {

            LocalDate effectiveEnd = end != null?end.minusDays(1):null;

            int noches = new Long(DAYS.between(start, end)).intValue();

            int y = adultsPerRoon + childrenPerRoom + 1;
            int totaly = rooms * y;
            double[][] v = new double[noches][totaly];
            for (int i = 0; i < v.length; i++) v[i] = new double[totaly];

            for (LinearFare f : contract.getTerms().getFares()) {
                for (DatesRange dr : f.getDates()) {
                    if (dr.getStart().isBefore(effectiveEnd) && dr.getEnd().isAfter(start)) {
                        int desde = new Long(DAYS.between(start, dr.getStart())).intValue();
                        if (desde < 0) desde = 0;
                        int hasta = new Long(DAYS.between(start, dr.getEnd())).intValue();
                        if (hasta > noches) hasta = noches;
                        for (int noche = desde; noche < hasta; noche++) {

                            for (LinearFareLine l : f.getLines()) {

                                if (l.getRoomTypeCode() == null || l.getRoomTypeCode().equals(room.getType())) {

                                    if (l.getBoardTypeCode() == null || l.getBoardTypeCode().equals(board.getType())) {

                                        for (int hab = 0; hab < rooms; hab++) {
                                            v[noche][hab * y] += l.getLodgingPrice();

                                            for (int adult = 0; adult < adultsPerRoon; adult++) {
                                                v[noche][hab * y + 1 + adult] += l.getAdultPrice();
                                            }

                                            for (int child = 0; child < childrenPerRoom; child++) {
                                                v[noche][hab * y + 1 + adultsPerRoon + child] += l.getChildPrice().applicarA(l.getAdultPrice());
                                            }

                                        }

                                    }

                                }

                            }

                        }
                    }
                }
            }

            double total = 0;
            for (int i = 0; i < v.length; i++) for (int j = 0; j < v[i].length; j++) {
                total += v[i][j];
            }
            value = Helper.roundEuros(total);

        }
        booking.updateData();
    }



    @Override
    public boolean equals(Object obj) {
        return this == obj || id != 0 && id == ((HotelBookingLine)obj).getId();
    }



    public void setStart(LocalDate start) {
        this.start = start;
        if (booking != null) booking.updateData();
    }

    public void setEnd(LocalDate end) {
        this.end = end;
        if (booking != null) booking.updateData();
    }

    public void setRoom(Room room) {
        this.room = room;
        if (booking != null) booking.updateData();
    }

    public void setBoard(Board board) {
        this.board = board;
        if (booking != null) booking.updateData();
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
        if (booking != null) booking.updateData();
    }

    public void setAdultsPerRoon(int adultsPerRoon) {
        this.adultsPerRoon = adultsPerRoon;
        if (booking != null) booking.updateData();
    }

    public void setChildrenPerRoom(int childrenPerRoom) {
        this.childrenPerRoom = childrenPerRoom;
        if (booking != null) booking.updateData();
    }

    public void setAges(int[] ages) {
        this.ages = ages;
        if (booking != null) booking.updateData();
    }

    public void setActive(boolean active) {
        this.active = active;
        if (booking != null) booking.updateData();
    }

    public void setContract(HotelContract contract) {
        this.contract = contract;
        if (booking != null) booking.updateData();
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
        if (booking != null) booking.updateData();
    }


    @Override
    public String toString() {
        String h = rooms + " x " + room + " in " + board + " from " + start + " to " + end + " occupied by " + adultsPerRoon + " adults";
        if (childrenPerRoom > 0) h += " and " + childrenPerRoom + " children";
        h += " per room ";
        if (ages != null) h += " (" + Arrays.toString(ages) + " years old)";
        h += " (" + (contract != null ? contract : "NO CONTRACT");
        h += "/" + (inventory != null ? inventory : "NO INVENTORY") + ")";
        return h;
    }
}
