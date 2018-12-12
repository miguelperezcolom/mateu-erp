package io.mateu.erp.model.product.hotel;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import io.mateu.mdd.core.annotations.ColumnWidth;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.OptionsClass;
import io.mateu.mdd.core.annotations.ValueClass;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.XMLSerializable;
import org.jdom2.Element;

import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 1/10/16.
 */
public class Allotment implements XMLSerializable {

    @Ignored
    @NotNull
    private HotelContractPhoto photo;

    @NotEmpty
    @ManyToOne
    @ColumnWidth(400)
    private RoomType room;

    public DataProvider getRoomDataProvider() {
        List<RoomType> l = new ArrayList<>();
        for (Room r : photo.getContract().getHotel().getRooms()) {
            l.add(r.getType());
        }
        return new ListDataProvider<RoomType>(l);
    }

    private LocalDate start;

    private LocalDate end;

    private int quantity;


    public RoomType getRoom() {
        return room;
    }

    public void setRoom(RoomType room) {
        this.room = room;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    private RoomType getRoom(String roomTypeCode) {
        RoomType c = null;
        if (photo != null
                && photo.getContract() != null
                && photo.getContract().getHotel() != null) {
            for (Room r : photo.getContract().getHotel().getRooms()) {
                if (r.getType().getCode().equals(roomTypeCode)) {
                    c = r.getType();
                    return c;
                }
            }
        } else {
            try {
                c = Helper.find(RoomType.class, roomTypeCode);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        return c;
    }

    private BoardType getBoard(String boardTypeCode) {
        BoardType c = null;
        if (photo != null
                && photo.getContract() != null
                && photo.getContract().getHotel() != null) {
            for (Board r : photo.getContract().getHotel().getBoards()) {
                if (r.getType().getCode().equals(boardTypeCode)) {
                    c = r.getType();
                    return c;
                }
            }
        } else {
            try {
                c = Helper.find(BoardType.class, boardTypeCode);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        return c;
    }


    @Override
    public Element toXml() {
        Element e = new Element("allotment");

        if (getStart() != null) e.setAttribute("start", getStart().toString());
        if (getEnd() != null) e.setAttribute("end", getEnd().toString());

        if (getRoom() != null) e.setAttribute("room", getRoom().getCode());
        e.setAttribute("quantity", "" + getQuantity());

        return e;
    }

    @Override
    public void fromXml(Element e) {
        if (e.getAttribute("start") != null) setStart(LocalDate.parse(e.getAttributeValue("start")));
        if (e.getAttribute("end") != null) setEnd(LocalDate.parse(e.getAttributeValue("end")));
        if (e.getAttribute("room") != null) setRoom(getRoom(e.getAttributeValue("room")));
        if (e.getAttribute("quantity") != null) setQuantity(Integer.parseInt(e.getAttributeValue("quantity")));
    }


    public Allotment(HotelContractPhoto photo) {
        this.photo = photo;
    }

    public Allotment(HotelContractPhoto photo, Element e) {
        this.photo = photo;
        fromXml(e);
    }

    public Allotment(HotelContractPhoto photo, String room, LocalDate start, LocalDate end, int quantity) {
        this.photo = photo;
        this.room = getRoom(room);
        this.start = start;
        this.end = end;
        this.quantity = quantity;
    }

    public Allotment cloneAsConverted() {
        return new Allotment(photo, toXml());
    }
}
