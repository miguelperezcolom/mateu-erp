package io.mateu.erp.model.booking.hotel;

import com.google.common.collect.Lists;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import io.mateu.erp.model.booking.parts.HotelBookingLine;
import io.mateu.erp.model.product.ContractType;
import io.mateu.erp.model.product.hotel.*;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.erp.model.product.hotel.offer.AbstractHotelOffer;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.model.util.IntArrayAttributeConverter;
import io.mateu.mdd.core.util.DatesRange;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

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

    @Ignored
    private transient int roomsBefore;


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
        adultsPerRoom = l.getAdultsPerRoom();
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





    public void check() throws Throwable {

        if (active) {

            occupationOk = true;
            salesClosed = true;
            enoughRooms = true;
            release = true;
            minStay = true;
            weekDays = true;

            LocalDate effectiveEnd = end != null?end.minusDays(1):null;

            if (service.getHotel() != null) {

                if (room != null) {

                    int pax = adultsPerRoom + childrenPerRoom;
                    int infants = 0;
                    if (ages != null) for (int i = 0; i < ages.length; i++) if (ages[i] < 2) infants++;
                    if (numberOfRooms > 0) infants = infants / numberOfRooms;
                    int effectiveChildren = childrenPerRoom - infants;

                    if (pax < room.getMinPax()) occupationOk = false;
                    else if (infants > 0 && !room.isInfantsAllowed()) occupationOk = false;
                    else if (effectiveChildren > 0 && !room.isChildrenAllowed()) occupationOk = false;
                    else {
                        for (MaxCapacity c : room.getMaxCapacities().getCapacities()) {
                            int resChildren = effectiveChildren;
                            if (room.isInfantsInBed() && infants > c.getInfants()) resChildren += infants - c.getInfants();
                            int resAdults = adultsPerRoom;
                            if (resChildren > c.getChildren()) resAdults += resChildren - c.getChildren();
                            if (c.getAdults() < resAdults) {
                                occupationOk = false;
                                break;
                            }
                        }
                    }

                    if (board != null && service != null) {

                        List<StopSalesOperation> ops = new ArrayList<>();
                        try {
                            ops = StopSalesCalendar.getOperations(service.getHotel(), start, effectiveEnd);
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                        Map<LocalDate, Boolean>[] cuboParos = StopSalesCalendar.construirCubo(ops, start, effectiveEnd, room.getType(), board.getType(), service.getBooking().getAgency());


                        for (LocalDate d = start.plusDays(0); d.isBefore(end); d = d.plusDays(1)) {
                            if (cuboParos[0].getOrDefault(d, false)) {
                                salesClosed = false;
                                break;
                            }
                        }

                    }


                }

                if (contract != null) {
                    if (start != null) {
                        long releaseReserva = DAYS.between(LocalDate.now(), start);
                        if (service.getBooking().getFormalizationDate() != null) releaseReserva = DAYS.between(service.getBooking().getFormalizationDate().toLocalDate(), start);
                        else if (service.getBooking().getAudit() != null && service.getBooking().getAudit().getCreated() != null) releaseReserva = DAYS.between(service.getBooking().getAudit().getCreated().toLocalDate(), start);
                        if (releaseReserva < 0) release = false;
                        else for (ReleaseRule r : contract.getTerms().getReleaseRules()) {
                            if (start == null || end == null || ((r.getStart() == null || r.getStart().isBefore(effectiveEnd)) && (r.getEnd() == null || r.getEnd().isAfter(start)))) {
                                if (room == null || r.getRooms().size() == 0 || r.getRooms().contains(room.getType().getCode())) {
                                    release = releaseReserva >= r.getRelease();
                                    break;
                                }
                            }
                        }
                        if (end != null) {
                            long noches = DAYS.between(start, end) - 1;
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

                    if (noches > 0) {

                        int[] cupo = getAvailableInventory();

                        int max = 0;
                        for (int i =  0; i < cupo.length; i++) {
                            if (i == 0 || max > cupo[i]) max = cupo[i];
                        }

                        roomsLeft = max + roomsBefore - numberOfRooms;

                        enoughRooms = numberOfRooms - roomsBefore <= 0 || (numberOfRooms - roomsBefore) <= roomsLeft;

                    }

                }
            }

            validationMessages = "checked! ;)";

            available = occupationOk && salesClosed && enoughRooms && release && minStay && weekDays;

        }

    }

    private int[] getAvailableInventory() throws Throwable {
        int noches = new Long(DAYS.between(start, end)).intValue() - 1;
        int[] cupo = getAvailableInventory(inventory);
        if (contract != null && contract.getSaleOf() != null && !contract.getSaleOf().getInventory().equals(inventory)) {
            int[] cupoCompra = getAvailableInventory(contract.getSaleOf().getInventory());
            for (int i = 0; i < noches; i++) if (cupoCompra[i] < cupo[i]) cupo[i] = cupoCompra[i];
        }
        return cupo;
    }

    private int[] getAvailableInventory(Inventory inventory) throws Throwable {
        LocalDate effectiveEnd = end.minusDays(1);
        int noches = new Long(DAYS.between(start, effectiveEnd)).intValue();
        int[] cupo = new int[noches];

        for (HotelContract c : inventory.getContracts()) {
            if (c.getTerms() != null) for (Allotment a : c.getTerms().getAllotment()) if (a.getRoom().equals(room.getType())) {
                if (!a.getStart().isAfter(effectiveEnd) && !a.getEnd().isBefore(start)) {
                    int desde = new Long(DAYS.between(start, a.getStart())).intValue();
                    if (desde < 0) desde = 0;
                    int hasta = new Long(DAYS.between(start, a.getEnd())).intValue();
                    if (hasta > noches) hasta = noches;
                    for (int i = desde; i < hasta; i++) {
                        cupo[i] += a.getQuantity();
                    }
                }
            }
        }

        for (Inventory dependant : inventory.getDependantInventories()) {
            for (HotelContract c : dependant.getContracts()) if (!inventory.getContracts().contains(c)) {
                if (c.getTerms() != null) for (Allotment a : c.getTerms().getAllotment()) if (a.getRoom().equals(room.getType())) {
                    if (!a.getStart().isAfter(effectiveEnd) && !a.getEnd().isBefore(start)) {
                        int desde = new Long(DAYS.between(start, a.getStart())).intValue();
                        if (desde < 0) desde = 0;
                        int hasta = new Long(DAYS.between(start, a.getEnd())).intValue();
                        if (hasta > noches) hasta = noches;
                        for (int i = desde; i < hasta; i++) {
                            cupo[i] += a.getQuantity();
                        }
                    }
                }
            }
        }

        for (InventoryOperation a : inventory.getOperations()) {
            if (a.getRoom().equals(room.getType())) {
                if (!a.getStart().isAfter(effectiveEnd) && !a.getEnd().isBefore(start)) {
                    int desde = new Long(DAYS.between(start, a.getStart())).intValue();
                    if (desde < 0) desde = 0;
                    int hasta = new Long(DAYS.between(start, a.getEnd())).intValue();
                    if (hasta > noches) hasta = noches;
                    for (int i = desde; i < hasta; i++) {
                        if (InventoryAction.ADD.equals(a.getAction())) cupo[i] += a.getQuantity();
                        else if (InventoryAction.SUBSTRACT.equals(a.getAction())) cupo[i] -= a.getQuantity();
                        if (InventoryAction.SET.equals(a.getAction())) cupo[i] = a.getQuantity();
                    }
                }
            }
        }

        for (HotelBookingLine a : inventory.getBookings()) {
            if (a.getBooking().isActive() && a.isActive()) {
                if (a.getRoom().getType().equals(room.getType())) {
                    if (!a.getStart().isAfter(effectiveEnd) && !a.getEnd().isBefore(start)) {
                        int desde = new Long(DAYS.between(start, a.getStart())).intValue();
                        if (desde < 0) desde = 0;
                        int hasta = new Long(DAYS.between(start, a.getEnd())).intValue();
                        if (hasta > noches) hasta = noches;
                        for (int i = desde; i < hasta; i++) {
                            cupo[i] -= a.getRoomsBefore();
                        }
                    }
                }
            }
        }

        return cupo;
    }

    public void price() {
        double offersValue = 0;
        String appliedOffers = "";
        value = 0;
        valued = false;
        if (start != null && end != null && room != null && board != null && contract != null && contract.getTerms() != null) {

            LocalDate effectiveEnd = end != null?end.minusDays(1):null;

            int noches = new Long(DAYS.between(start, end)).intValue();

            if (noches > 0) {
                int paxPerRoom = adultsPerRoom + childrenPerRoom + 1;
                int totalPax = numberOfRooms * paxPerRoom;
                double[][] valorEstancia = new double[noches][totalPax];
                double[][] valorRegimen = new double[noches][totalPax];
                boolean[] vd = new boolean[noches];
                for (int i = 0; i < valorEstancia.length; i++) valorEstancia[i] = new double[totalPax];

                for (LinearFare f : contract.getTerms().getFares()) {
                    for (DatesRange dr : f.getDates()) {
                        if (!dr.getStart().isAfter(effectiveEnd) && !dr.getEnd().isBefore(start)) {
                            int desde = new Long(DAYS.between(start, dr.getStart())).intValue();
                            if (desde < 0) desde = 0;
                            int hasta = new Long(DAYS.between(start, dr.getEnd())).intValue();
                            if (hasta >= noches) hasta = noches - 1;
                            for (int noche = desde; noche <= hasta; noche++) {

                                for (LinearFareLine l : f.getLines()) {

                                    if (l.getRoomTypeCode() == null || l.getRoomTypeCode().equals(room.getType())) {

                                        if (l.getBoardTypeCode() == null || l.getBoardTypeCode().equals(board.getType())) {

                                            vd[noche] = true;

                                            for (int hab = 0; hab < numberOfRooms; hab++) {
                                                valorEstancia[noche][hab * paxPerRoom] += l.getLodgingPrice();

                                                for (int adult = 0; adult < adultsPerRoom; adult++) {
                                                    valorEstancia[noche][hab * paxPerRoom + 1 + adult] += l.getAdultPrice();
                                                    valorRegimen[noche][hab * paxPerRoom + 1 + adult] += l.getMealAdultPrice();
                                                }

                                                for (int child = 0; child < childrenPerRoom; child++) {
                                                    if (l.getChildPrice() != null) valorEstancia[noche][hab * paxPerRoom + 1 + adultsPerRoom + child] += l.getChildPrice().applicarA(l.getAdultPrice());
                                                    if (l.getMealChildPrice() != null) valorRegimen[noche][hab * paxPerRoom + 1 + adultsPerRoom + child] += l.getMealChildPrice().applicarA(l.getMealAdultPrice());
                                                }

                                                for (int pax = adultsPerRoom; pax > 2; pax--) {
                                                    if (l.getExtraAdultPrice() != null) valorEstancia[noche][hab * paxPerRoom + 1 + pax] += l.getExtraAdultPrice().applicarA(l.getAdultPrice());
                                                }

                                            }

                                        }

                                    }

                                }

                            }
                        }
                    }
                }

                double[] valorOfertas = new double[noches];

                aplicarOfertas(valorEstancia, valorRegimen, valorOfertas);


                double total = 0;
                double totalOfertas = 0;
                for (int i = 0; i < valorEstancia.length; i++) {
                    for (int j = 0; j < valorEstancia[i].length; j++) {
                        total += valorEstancia[i][j];
                        total += valorRegimen[i][j];
                    }
                    total += valorOfertas[i];
                    totalOfertas += valorOfertas[i];
                }
                value = Helper.roundEuros(total);
                offersValue = Helper.roundEuros(totalOfertas);
                valued = true;
                for (int i =  0; i < vd.length; i++) if (!vd[i]) {
                    valued = false;
                    break;
                }
            }


        }
    }

    private void aplicarOfertas(double[][] valorEstancia, double[][] valorRegimen, double[] valorOfertas) {
        for (AbstractHotelOffer o : service.getHotel().getOffers()) {
            //todo: aplicar oferta
            double totalOferta = 0; //o.aplicar(this, valorEstancia, valorRegimen, valorOfertas);
            if (totalOferta != 0) {
                //todo: gestionar oferta aplicada
                //if (!"".equals(appliedOffers)) appliedOffers += ", ";
                //appliedOffers += o.getName();
            }
        }
    }


    @PostLoad
    public void postLoad() {
        oldInventory = inventory;
        roomsBefore = numberOfRooms;
    }

}
