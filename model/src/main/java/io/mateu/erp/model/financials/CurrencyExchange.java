package io.mateu.erp.model.financials;

import lombok.Getter;
import lombok.Setter;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class CurrencyExchange implements Serializable {

    private Map<String, Map<String, Double>> exchange = new HashMap<>();

    public CurrencyExchange() {

    }

    public CurrencyExchange(String s) throws JDOMException, IOException {
        fill(new SAXBuilder().build(new StringReader(s)).getRootElement());
    }

    private void fill(Element rootElement) {
        for (Element e : rootElement.getChildren("exchange")) {
            String de = e.getAttributeValue("origin");
            String a = e.getAttributeValue("destination");
            double factor = Double.parseDouble(e.getAttributeValue("factor"));

            {
                Map<String, Double> m = getExchange().get(de);
                if (m == null) getExchange().put(de, m = new HashMap<>());
                m.put(a, factor);
            }
            {
                Map<String, Double> m = getExchange().get(a);
                if (m == null) getExchange().put(a, m = new HashMap<>());
                m.put(de, factor);
            }
        }
    }

    @Override
    public String toString() {
        Element xml = new Element("exchanges");

        for (String de : getExchange().keySet()) {
            Map<String, Double> m = getExchange().get(de);
            for (String a : m.keySet()) {
                xml.addContent(new Element("exchange")
                        .setAttribute("origin", de)
                        .setAttribute("destination", a)
                        .setAttribute("factor", "" + m.get(a))
                );
            }
        }

        return new XMLOutputter().outputString(xml);
    }
}
