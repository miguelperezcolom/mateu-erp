package io.mateu.erp.model.product.hotel;

import io.mateu.mdd.core.annotations.OwnedList;
import io.mateu.mdd.core.util.XMLSerializable;
import org.jdom2.Element;

import java.util.HashMap;
import java.util.Map;

public class RoomFare implements XMLSerializable {

    @OwnedList
    private Map<String, BoardFare> farePerBoard = new HashMap<>();

    public Map<String, BoardFare> getFarePerBoard() {
        return farePerBoard;
    }

    public void setFarePerBoard(Map<String, BoardFare> farePerBoard) {
        this.farePerBoard = farePerBoard;
    }

    public RoomFare(Element e) {
        fromXml(e);
    }

    public RoomFare() {
    }

    @Override
    public Element toXml() {
        Element e = new Element("roomFare");
        for (String k : getFarePerBoard().keySet()) {
            e.addContent(getFarePerBoard().get(k).toXml().setAttribute("board", "" + k));
        }
        return e;
    }

    @Override
    public void fromXml(Element e) {
        for (Element z : e.getChildren("boardFare")) getFarePerBoard().put(z.getAttributeValue("board"), new BoardFare(z));
    }

    public RoomFare combineWith(RoomFare rf) {
        return this;
    }
}
