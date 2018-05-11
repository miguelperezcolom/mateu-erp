package io.mateu.erp.model.product.hotel;

import io.mateu.common.model.util.XMLSerializable;
import io.mateu.ui.mdd.server.annotations.KeyClass;
import io.mateu.ui.mdd.server.annotations.OwnedList;
import org.jdom2.Element;

import java.util.HashMap;
import java.util.Map;

public class RoomFare implements XMLSerializable {

    @KeyClass(BoardType.class)
    @OwnedList
    private Map<String, BoardFare> farePerBoard = new HashMap<>();

    public Map<String, BoardFare> getFarePerBoard() {
        return farePerBoard;
    }

    public void setFarePerBoard(Map<String, BoardFare> farePerBoard) {
        this.farePerBoard = farePerBoard;
    }

    public RoomFare(Element e) {
        for (Element z : e.getChildren("boardFare")) getFarePerBoard().put(z.getAttributeValue("board"), new BoardFare(z));
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

    public RoomFare combineWith(RoomFare rf) {
        return this;
    }
}
