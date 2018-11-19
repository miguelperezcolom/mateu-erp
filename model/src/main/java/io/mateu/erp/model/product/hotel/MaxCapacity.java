package io.mateu.erp.model.product.hotel;

import lombok.Getter;
import lombok.Setter;
import org.jdom2.Element;

import java.io.Serializable;


@Getter
@Setter
public class MaxCapacity implements Serializable {

    private int adults;
    private int children;
    private int infants;

    public MaxCapacity() {

    }

    public MaxCapacity(int adults, int children, int infants) {
        this.adults = adults;
        this.children = children;
        this.infants = infants;
    }

    public MaxCapacity(Element e) {
        if (e.getAttribute("adults") != null) setAdults(Integer.parseInt(e.getAttributeValue("adults")));
        if (e.getAttribute("children") != null) setChildren(Integer.parseInt(e.getAttributeValue("children")));
        if (e.getAttribute("infants") != null) setInfants(Integer.parseInt(e.getAttributeValue("infants")));
    }

    public Element toXml() {
        Element xml = new Element("capacity");
        xml.setAttribute("adults", "" + getAdults());
        xml.setAttribute("children", "" + getChildren());
        xml.setAttribute("infants", "" + getInfants());
        return xml;
    }

    @Override
    public String toString() {
        return "" + adults + "+" + children + "+" + infants;
    }
}
