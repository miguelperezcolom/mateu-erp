package io.mateu.erp.model.product.hotel;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.util.XMLSerializable;
import org.jdom2.Element;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 1/10/16.
 */
public class MinimumStayRule implements XMLSerializable {

    @Ignored
    private HotelContractPhoto photo;

    private LocalDate start;
    private LocalDate end;

    @ColumnWidth(80)
    private int nights;

    @ColumnWidth(80)
    @Caption("OR")
    private boolean onRequest;

    @ColumnWidth(80)
    @Caption("+%")
    private double supplementPercent;

    @ColumnWidth(80)
    @Caption("+Value")
    private double supplementValue;

    @NotNull
    @ColumnWidth(100)
    private SupplementPer per = SupplementPer.PAX;

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

    @UseCheckboxes(editableInline = true)
    private List<String> boards = new ArrayList<>();

    public DataProvider getBoardsDataProvider() {
        List<BoardType> l = new ArrayList<>();
        Hotel h = photo.getContract().getHotel();
        for (Board r : h.getBoards()) {
            l.add(r.getType());
        }
        return new ListDataProvider<BoardType>(l);
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

    public int getNights() {
        return nights;
    }

    public void setNights(int nights) {
        this.nights = nights;
    }

    public boolean isOnRequest() {
        return onRequest;
    }

    public void setOnRequest(boolean onRequest) {
        this.onRequest = onRequest;
    }

    public double getSupplementPercent() {
        return supplementPercent;
    }

    public void setSupplementPercent(double supplementPercent) {
        this.supplementPercent = supplementPercent;
    }

    public double getSupplementValue() {
        return supplementValue;
    }

    public void setSupplementValue(double supplementValue) {
        this.supplementValue = supplementValue;
    }

    public SupplementPer getPer() {
        return per;
    }

    public void setPer(SupplementPer per) {
        this.per = per;
    }

    public List<String> getRooms() {
        return rooms;
    }

    public void setRooms(List<String> rooms) {
        this.rooms = rooms;
    }

    public List<String> getBoards() {
        return boards;
    }

    public void setBoards(List<String> boards) {
        this.boards = boards;
    }

    public MinimumStayRule(HotelContractPhoto photo) {
        this.photo = photo;
    }

    public MinimumStayRule(HotelContractPhoto photo, Element e) {
        this.photo = photo;
        fromXml(e);
    }

    public MinimumStayRule() {
    }

    public MinimumStayRule(LocalDate start, LocalDate end, int nights, boolean onRequest, double supplementPercent, double supplementValue, SupplementPer per, List<String> rooms, List<String> boards) {
        this.start = start;
        this.end = end;
        this.nights = nights;
        this.onRequest = onRequest;
        this.supplementPercent = supplementPercent;
        this.supplementValue = supplementValue;
        this.per = per;
        this.rooms = rooms;
        this.boards = boards;
        if (this.rooms == null) this.rooms = new ArrayList<>();
        if (this.boards == null) this.boards = new ArrayList<>();
    }

    @Override
    public Element toXml() {
        Element e = new Element("rule");

        if (getStart() != null) e.setAttribute("start", getStart().toString());
        if (getEnd() != null) e.setAttribute("end", getEnd().toString());
        e.setAttribute("nights", "" + getNights());
        if (isOnRequest()) e.setAttribute("onRequest", "");
        e.setAttribute("supplementPercent", "" + getSupplementPercent());
        e.setAttribute("supplementValue", "" + getSupplementValue());
        if (getPer() != null) e.setAttribute("per", "" + getPer());
        for (String k : getRooms()) e.addContent(new Element("room").setAttribute("id", "" + k));
        for (String k : getBoards()) e.addContent(new Element("board").setAttribute("id", "" + k));

        StringBuffer sb = new StringBuffer("");
        if (getSupplementPercent() != 0) sb.append("" + getSupplementPercent() + " %");
        if (getSupplementValue() != 0) {
            sb.append("" + getSupplementValue() + "");
            sb.append(" per " + getPer());
        }
        if (getSupplementPercent() != 0 || getSupplementValue() != 0) sb.append(" supplement will be applied");
        if (isOnRequest()) {
            if (sb.length() > 0) sb.append(" and ");
            sb.append("the booking will be on request");
        }
        if (getRooms().size() > 0) {
            if (sb.length() > 0) sb.append(". ");
            sb.append("Appliable on rooms ");
            int aux = 0;
            for (String rcode : getRooms()) {
                if (aux++ > 0) sb.append(", ");
                sb.append(rcode);
            }
        }
        if (getBoards().size() > 0) {
            sb.append((getRooms().size() > 0)?" and on boards ":((sb.length() > 0)?". ":""));
            int aux = 0;
            for (String rcode : getBoards()) {
                if (aux++ > 0) sb.append(", ");
                sb.append(rcode);
            }
        }

        if (sb.length() > 0) e.setAttribute("descriptionforpdf", "" + sb.toString());

        return e;
    }

    @Override
    public void fromXml(Element e) {
        if (e.getAttribute("start") != null) setStart(LocalDate.parse(e.getAttributeValue("start")));
        if (e.getAttribute("end") != null) setEnd(LocalDate.parse(e.getAttributeValue("end")));
        if (e.getAttribute("nights") != null) setNights(Integer.parseInt(e.getAttributeValue("nights")));
        if (e.getAttribute("onRequest") != null) setOnRequest(true);
        if (e.getAttribute("supplementPercent") != null) setSupplementPercent(Double.parseDouble(e.getAttributeValue("supplementPercent")));
        if (e.getAttribute("supplementValue") != null) setSupplementValue(Double.parseDouble(e.getAttributeValue("supplementValue")));
        if (e.getAttribute("per") != null) setPer(SupplementPer.valueOf(e.getAttributeValue("per")));
        for (Element z : e.getChildren("room")) getRooms().add(z.getAttributeValue("id"));
        for (Element z : e.getChildren("board")) getBoards().add(z.getAttributeValue("id"));
    }
}
