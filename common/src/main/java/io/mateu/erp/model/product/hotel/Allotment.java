package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.util.XMLSerializable;
import io.mateu.ui.mdd.server.annotations.OptionsClass;
import org.jdom2.Element;

import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 1/10/16.
 */
public class Allotment implements XMLSerializable {

    @OptionsClass(RoomType.class)
    private String room;

    private LocalDate start;

    private LocalDate end;

    private int quantity;


    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
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

    @Override
    public Element toXml() {
        Element e = new Element("allotment");

        if (getStart() != null) e.setAttribute("start", getStart().toString());
        if (getEnd() != null) e.setAttribute("end", getEnd().toString());

        e.setAttribute("room", "" + getRoom());
        e.setAttribute("quantity", "" + getQuantity());

        return e;
    }

    public Allotment(Element e) {
        if (e.getAttribute("start") != null) setStart(LocalDate.parse(e.getAttributeValue("start")));
        if (e.getAttribute("end") != null) setEnd(LocalDate.parse(e.getAttributeValue("end")));
        if (e.getAttribute("room") != null) setRoom(e.getAttributeValue("room"));
        if (e.getAttribute("quantity") != null) setQuantity(Integer.parseInt(e.getAttributeValue("quantity")));
    }

    public Allotment() {
    }

    public Allotment(String room, LocalDate start, LocalDate end, int quantity) {
        this.room = room;
        this.start = start;
        this.end = end;
        this.quantity = quantity;
    }
}
