package io.mateu.erp.model.product.hotel;

import io.mateu.mdd.core.annotations.ValueClass;
import io.mateu.mdd.core.util.XMLSerializable;
import org.jdom2.Element;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CancellationRule implements XMLSerializable, ICancellationRule {

    private LocalDate start;
    private LocalDate end;
    private int release;
    private double amount;
    private double percent;
    private int firstNights;
    @ValueClass(RoomType.class)
    private List<String> rooms = new ArrayList<>();


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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public int getFirstNights() {
        return firstNights;
    }

    public void setFirstNights(int firstNights) {
        this.firstNights = firstNights;
    }

    public List<String> getRooms() {
        return rooms;
    }

    public void setRooms(List<String> rooms) {
        this.rooms = rooms;
    }


    public CancellationRule(Element e) {
        fromXml(e);
    }

    public CancellationRule() {
    }

    public CancellationRule(LocalDate start, LocalDate end, int release, double amount, double percent, int firstNights, List<String> rooms) {
        this.start = start;
        this.end = end;
        this.release = release;
        this.amount = amount;
        this.percent = percent;
        this.firstNights = firstNights;
        this.rooms = rooms;
        if (this.rooms == null) this.rooms = new ArrayList<>();
    }

    @Override
    public Element toXml() {
        Element e = new Element("rule");

        if (getStart() != null) e.setAttribute("start", getStart().toString());
        if (getEnd() != null) e.setAttribute("end", getEnd().toString());
        e.setAttribute("release", "" + getRelease());
        e.setAttribute("amount", "" + getAmount());
        e.setAttribute("percent", "" + getPercent());
        e.setAttribute("firstNights", "" + getFirstNights());
        for (String k : getRooms()) e.addContent(new Element("room").setAttribute("id", "" + k));

        return e;
    }

    @Override
    public void fromXml(Element e) {
        if (e.getAttribute("start") != null) setStart(LocalDate.parse(e.getAttributeValue("start")));
        if (e.getAttribute("end") != null) setEnd(LocalDate.parse(e.getAttributeValue("end")));
        if (e.getAttribute("release") != null) setRelease(Integer.parseInt(e.getAttributeValue("release")));
        if (e.getAttribute("amount") != null) setAmount(Double.parseDouble(e.getAttributeValue("amount")));
        if (e.getAttribute("percent") != null) setPercent(Double.parseDouble(e.getAttributeValue("percent")));
        if (e.getAttribute("firstNights") != null) setFirstNights(Integer.parseInt(e.getAttributeValue("firstNights")));
        for (Element z : e.getChildren("room")) getRooms().add(z.getAttributeValue("id"));
    }
}
