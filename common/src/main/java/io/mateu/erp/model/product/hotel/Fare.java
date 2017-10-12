package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.util.XMLSerializable;
import io.mateu.ui.mdd.server.annotations.KeyClass;
import io.mateu.ui.mdd.server.annotations.OwnedList;
import org.jdom2.Element;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by miguel on 1/10/16.
 */
public class Fare implements XMLSerializable {

    private String name;

    private List<DatesRange> dates = new ArrayList<>();

    @KeyClass(RoomType.class)
    @OwnedList
    private Map<String, RoomFare> farePerRoom = new HashMap<>();


    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, RoomFare> getFarePerRoom() {
        return farePerRoom;
    }

    public void setFarePerRoom(Map<String, RoomFare> farePerRoom) {
        this.farePerRoom = farePerRoom;
    }

    public List<DatesRange> getDates() {
        return dates;
    }

    public void setDates(List<DatesRange> dates) {
        this.dates = dates;
    }

    public Fare(Element e) {

        if (e.getAttribute("name") != null) setName(e.getAttributeValue("name"));

        if (e.getChild("dates") != null) for (Element z: e.getChild("dates").getChildren()) getDates().add(new DatesRange(z));

        for (Element z : e.getChildren("roomFare")) getFarePerRoom().put(z.getAttributeValue("room"), new RoomFare(z));

    }

    public Fare() {
    }

    public Fare(String name, List<DatesRange> dates, Map<String, RoomFare> farePerRoom) {
        this.name = name;
        this.dates = dates;
        this.farePerRoom = farePerRoom;
    }

    @Override
    public Element toXml() {
        Element e = new Element("fare");
        if (getName() != null) e.setAttribute("name", getName());
        if (getDates() != null) {
            Element x;
            e.addContent(x = new Element("dates"));
            for (DatesRange r : getDates()) x.addContent(r.toXml());
        }
        for (String k : getFarePerRoom().keySet()) {
            e.addContent(getFarePerRoom().get(k).toXml().setAttribute("room", "" + k));
        }
        return e;
    }
}
