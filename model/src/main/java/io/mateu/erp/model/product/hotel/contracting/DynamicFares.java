package io.mateu.erp.model.product.hotel.contracting;

import lombok.Getter;
import lombok.Setter;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

@Getter@Setter
public class DynamicFares {

    private Map<String, DynamicFareValue> supplementPerRoom = new HashMap<>();

    private Map<String, DynamicFareValue> supplementPerBoard = new HashMap<>();

    public DynamicFares() {

    }

    public DynamicFares(String s) throws JDOMException, IOException {
        fill(new SAXBuilder().build(new StringReader(s)).getRootElement());
    }

    private void fill(Element e) {
        for (Element es : e.getChild("supplementsPerRoom").getChildren("supplement")) {
            getSupplementPerRoom().put(es.getAttributeValue("key"), new DynamicFareValue(Double.parseDouble(es.getAttributeValue("percent")), Double.parseDouble(es.getAttributeValue("percent"))));
        }
        for (Element es : e.getChild("supplementsPerBoard").getChildren("supplement")) {
            getSupplementPerBoard().put(es.getAttributeValue("key"), new DynamicFareValue(Double.parseDouble(es.getAttributeValue("percent")), Double.parseDouble(es.getAttributeValue("percent"))));
        }
    }

    @Override
    public String toString() {
        Element xml = new Element("terms");

        Element espr;
        xml.addContent(espr = new Element("supplementsPerRoom"));
        for (String k : getSupplementPerRoom().keySet()) {
            DynamicFareValue s = getSupplementPerRoom().get(k);
            Element e;
            espr.addContent(e = new Element("supplement"));
            e.setAttribute("key", k);
            e.setAttribute("percent", "" + s.getPercent());
            e.setAttribute("value", "" + s.getValue());
        }

        Element espb;
        xml.addContent(espb = new Element("supplementsPerBoard"));
        for (String k : getSupplementPerBoard().keySet()) {
            DynamicFareValue s = getSupplementPerBoard().get(k);
            Element e;
            espr.addContent(e = new Element("supplement"));
            e.setAttribute("key", k);
            e.setAttribute("percent", "" + s.getPercent());
            e.setAttribute("value", "" + s.getValue());
        }

        return new XMLOutputter().outputString(xml);
    }
}
