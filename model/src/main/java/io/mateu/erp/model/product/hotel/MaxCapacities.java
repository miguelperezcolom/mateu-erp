package io.mateu.erp.model.product.hotel;

import lombok.Getter;
import lombok.Setter;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class MaxCapacities implements Serializable {

    private List<MaxCapacity> capacities = new ArrayList<>();

    public MaxCapacities() {

    }

    public MaxCapacities(String xml) throws JDOMException, IOException {
        fill(new SAXBuilder().build(new StringReader(xml)).getRootElement());
    }

    private void fill(Element xml) {
        for (Element e : xml.getChildren("capacity")) getCapacities().add(new MaxCapacity(e));
    }

    public Element toXml() {
        Element xml = new Element("maxcapacities");
        for (MaxCapacity c : getCapacities()) xml.addContent(c.toXml());
        return xml;
    }

    public String toXmlString() {
        return new XMLOutputter(Format.getCompactFormat()).outputString(toXml());
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        for (MaxCapacity c : getCapacities()) {
            if (first) first = false;
            else sb.append(",");
            sb.append(c.toString());
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && toString().equals(obj.toString());
    }

}
