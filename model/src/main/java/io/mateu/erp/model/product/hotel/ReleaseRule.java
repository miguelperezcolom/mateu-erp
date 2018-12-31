package io.mateu.erp.model.product.hotel;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.UseCheckboxes;
import io.mateu.mdd.core.util.XMLSerializable;
import org.jdom2.Element;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 1/10/16.
 */
public class ReleaseRule implements XMLSerializable {

    @Ignored
    private HotelContractPhoto photo;

    private LocalDate start;
    private LocalDate end;

    private int release;

    @UseCheckboxes(editableInline = true)
    private List<String> rooms = new ArrayList<>();

    public DataProvider getRoomsDataProvider() {
        List<RoomType> l = new ArrayList<>();
        Hotel h = photo.getContract().getHotel();
        for (Room r : h.getRooms()) {
            l.add(r.getType());
        }
        return new ListDataProvider<RoomType>(l);
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

    public int getRelease() {
        return release;
    }

    public void setRelease(int release) {
        this.release = release;
    }

    public List<String> getRooms() {
        return rooms;
    }

    public void setRooms(List<String> rooms) {
        this.rooms = rooms;
    }


    public ReleaseRule(HotelContractPhoto photo) {
        this.photo = photo;
    }

    public ReleaseRule(HotelContractPhoto photo, Element e) {
        this.photo = photo;
        fromXml(e);
    }

    public ReleaseRule() {
    }

    public ReleaseRule(LocalDate start, LocalDate end, int release, List<String> rooms) {
        this.start = start;
        this.end = end;
        this.release = release;
        this.rooms = rooms;
        if (this.rooms == null) this.rooms = new ArrayList<>();
    }

    @Override
    public Element toXml() {
        Element e = new Element("rule");

        if (getStart() != null) e.setAttribute("start", getStart().toString());
        if (getEnd() != null) e.setAttribute("end", getEnd().toString());
        for (String k : getRooms()) e.addContent(new Element("room").setAttribute("id", "" + k));
        e.setAttribute("release", "" + getRelease());

        return e;
    }

    @Override
    public void fromXml(Element e) {
        if (e.getAttribute("start") != null) setStart(LocalDate.parse(e.getAttributeValue("start")));
        if (e.getAttribute("end") != null) setEnd(LocalDate.parse(e.getAttributeValue("end")));
        if (e.getAttribute("release") != null) setRelease(Integer.parseInt(e.getAttributeValue("release")));
        for (Element z : e.getChildren("room")) getRooms().add(z.getAttributeValue("id"));
    }
}
