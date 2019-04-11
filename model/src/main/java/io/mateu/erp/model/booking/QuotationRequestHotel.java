package io.mateu.erp.model.booking;


import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import io.mateu.erp.dispo.Helper;
import io.mateu.erp.model.product.hotel.Board;
import io.mateu.erp.model.product.hotel.BoardType;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.erp.model.product.hotel.Room;
import io.mateu.mdd.core.annotations.DependsOn;
import io.mateu.mdd.core.annotations.Output;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Element;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import static java.time.temporal.ChronoUnit.DAYS;

@Entity@Getter@Setter
public class QuotationRequestHotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private QuotationRequest rq;

    private boolean active = true;

    @NotNull
    @ManyToOne
    private Hotel hotel;

    @Column(name = "_start")
    @NotNull
    private LocalDate start;

    @Column(name = "_end")
    @NotNull
    private LocalDate end;

    @ManyToOne@NotNull
    private Room room;

    public DataProvider getRoomDataProvider() {
        return new ListDataProvider(hotel != null?hotel.getRooms():new ArrayList());
    }

    @ManyToOne@NotNull
    private Board board;

    public DataProvider getBoardDataProvider() {
        return new ListDataProvider(hotel != null?hotel.getBoards():new ArrayList());
    }


    private int numberOfRooms;

    public void setNumberOfRooms(int numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
        updateTotal();
    }

    private int adultsPerRoom;

    public void setAdultsPerRoom(int adultsPerRoom) {
        this.adultsPerRoom = adultsPerRoom;
        updateTotal();
    }

    private int childrenPerRoom;

    public void setChildrenPerRoom(int childrenPerRoom) {
        this.childrenPerRoom = childrenPerRoom;
        updateTotal();
    }

    private int[] ages;

    private boolean saleOverrided;

    public void setSaleOverrided(boolean saleOverrided) {
        this.saleOverrided = saleOverrided;
        updateTotal();
    }

    private double pricePerRoom;

    public void setPricePerRoom(double pricePerRoom) {
        this.pricePerRoom = pricePerRoom;
        updateTotal();
    }

    @DependsOn("saleOverrided")
    public boolean isPricePerRoomVisible() {
        return saleOverrided;
    }

    private double pricePerAdult;

    public void setPricePerAdult(double pricePerAdult) {
        this.pricePerAdult = pricePerAdult;
        updateTotal();
    }

    @DependsOn("saleOverrided")
    public boolean isPricePerAdultVisible() {
        return saleOverrided;
    }

    private double pricePerChild;

    public void setPricePerChild(double pricePerChild) {
        this.pricePerChild = pricePerChild;
        updateTotal();
    }

    @DependsOn("saleOverrided")
    public boolean isPricePerChildVisible() {
        return saleOverrided;
    }

    @Output
    private double totalSale;


    private boolean costOverrided;

    public void setCostOverrided(boolean costOverrided) {
        this.costOverrided = costOverrided;
        updateTotal();
    }

    private double costPerRoom;

    public void setCostPerRoom(double costPerRoom) {
        this.costPerRoom = costPerRoom;
        updateTotal();
    }

    @DependsOn("costOverrided")
    public boolean isCostPerRoomVisible() {
        return saleOverrided;
    }


    private double costPerAdult;

    public void setCostPerAdult(double costPerAdult) {
        this.costPerAdult = costPerAdult;
        updateTotal();
    }

    @DependsOn("costOverrided")
    public boolean isCostPerAdultVisible() {
        return saleOverrided;
    }

    private double costPerChild;

    public void setCostPerChild(double costPerChild) {
        this.costPerChild = costPerChild;
        updateTotal();
    }

    @DependsOn("costOverrided")
    public boolean isCostPerChildVisible() {
        return saleOverrided;
    }


    @Output
    private double totalCost;


    public void setTotalSale(double totalSale) {
        this.totalSale = totalSale;
        if (rq != null) {
            rq.updateTotal();
        }
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
        if (rq != null) {
            rq.updateTotal();
        }
    }


    private void updateTotal() {
        if (isSaleOverrided()) setTotalSale(Helper.roundEuros(getNumNights() * (numberOfRooms * pricePerRoom + (numberOfRooms * adultsPerRoom) * pricePerAdult) + (numberOfRooms * childrenPerRoom) * pricePerChild));
        if (isCostOverrided()) setTotalCost(Helper.roundEuros(getNumNights() * (numberOfRooms * costPerRoom + (numberOfRooms * adultsPerRoom) * costPerAdult) + (numberOfRooms * childrenPerRoom) * costPerChild));
    }

    private int getNumNights() {
        int n = 1;
        if (start != null && end != null) {
            n = (int) DAYS.between(start, end);
            if (n < 1) n = 1;
        }
        return n;
    }


    @Override
    public String toString() {
        String h = "<table style='border-spacing: 0px; border-top: 1px dashed grey; border-bottom: 1px dashed grey;'>";
        h += "<tr><th width='150px'>Dates:</th><td>From " + start + " to " + end + "</td></tr>";
        h += "<tr><th>Hotel:</th><td>" + hotel.getName() + "</td></tr>";
        h += "<tr><th>Nr of rooms:</th><td>" + numberOfRooms + "</td></tr>";
        h += "<tr><th>Room:</th><td>" + room.getName() + "</td></tr>";
        h += "<tr><th>Board:</th><td>" + board.getName() + "</td></tr>";
        h += "<tr><th>Adults per room:</th><td>" + adultsPerRoom + "</td></tr>";
        h += "<tr><th>Children per room:</th><td>" + childrenPerRoom + "</td></tr>";
        h += "<tr><th>Total sale:</th><td>" + totalSale + "</td></tr>";
        h += "<tr><th>Total cost:</th><td>" + totalCost + "</td></tr>";
        h += "<tr><th>Total markup:</th><td>" + Helper.roundEuros(totalSale - totalCost) + "</td></tr>";
        h += "</table>";
        return h;
    }

    public String toSimpleString() {
        String s = "Hotel line " + getId() + "";
        return s;
    }


    @PrePersist@PreUpdate
    public void pre() {
        if (getRq().isAlreadyConfirmed()) throw new Error("This quotation request has already been related to a File. It can not be modified");
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj != null && obj instanceof QuotationRequestLine && id == ((QuotationRequestLine) obj).getId());
    }

    public Element toXml() {
        DecimalFormat pf = new DecimalFormat("#####0.00");
        DecimalFormat nf = new DecimalFormat("##,###,###,###,##0.00");

        Element el = new Element("line");

        if (active) el.setAttribute("active", "");

        el.setAttribute("hotel", hotel.getName() != null?hotel.getName():"--");
        el.setAttribute("room", room.getName() != null?room.getName():"--");
        el.setAttribute("board", board.getName() != null?board.getName():"--");

        if (start != null) el.setAttribute("start", start.format(DateTimeFormatter.ISO_DATE));
        if (end != null) el.setAttribute("end", end.format(DateTimeFormatter.ISO_DATE));

        el.setAttribute("rooms", "" + numberOfRooms);
        el.setAttribute("adultsPerRoom", "" + adultsPerRoom);
        el.setAttribute("childrenPerRoom", "" + childrenPerRoom);
        if (ages != null) el.setAttribute("ages", Arrays.toString(ages));

        el.setAttribute("total", nf.format(totalSale));

        return el;
    }

}
