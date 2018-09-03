package io.mateu.erp.model.product.hotel;

import io.mateu.mdd.core.util.XMLSerializable;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Element;

import java.time.LocalDate;

@Getter@Setter
public class DoublePerDateRange implements XMLSerializable {

    private LocalDate start;
    private LocalDate end;

    private double value;

    public DoublePerDateRange() {
    }

    public DoublePerDateRange(LocalDate start, LocalDate end, double value) {
        this.start = start;
        this.end = end;
        this.value = value;
    }

    public DoublePerDateRange(Element e) {
        fromXml(e);
    }

    @Override
    public Element toXml() {
        Element e = new Element("value");
        if (getStart() != null) e.setAttribute("start", getStart().toString());
        if (getEnd() != null) e.setAttribute("end", getEnd().toString());
        e.setAttribute("value", "" + value);
        return e;
    }

    @Override
    public void fromXml(Element e) {
        if (e.getAttribute("start") != null) setStart(LocalDate.parse(e.getAttributeValue("start")));
        if (e.getAttribute("end") != null) setEnd(LocalDate.parse(e.getAttributeValue("end")));
        if (e.getAttribute("value") != null) setValue(Double.parseDouble(e.getAttributeValue("value")));
    }
}
