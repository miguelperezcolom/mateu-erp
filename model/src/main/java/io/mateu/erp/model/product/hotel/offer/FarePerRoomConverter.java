package io.mateu.erp.model.product.hotel.offer;

import io.mateu.erp.model.product.hotel.RoomFare;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

@Converter
public class FarePerRoomConverter implements AttributeConverter<FarePerRoom, String> {

    @Override
    public String convertToDatabaseColumn(FarePerRoom list) {

        if (list == null) return null;
        else {
            Element e = new Element("fares");
            for (String k : list.getFares().keySet()) {
                e.addContent(list.getFares().get(k).toXml().setAttribute("room", "" + k));
            }
            return new XMLOutputter().outputString(e);
        }
        /*


        // Java 8
        return String.join(",", list);
        // Guava
        return Joiner.on(',').join(list);
         */
    }

    @Override
    public FarePerRoom convertToEntityAttribute(String joined) {

        if (joined == null) return null;
        else {

            Element e = null;
            try {

                Map<String, RoomFare> l = new HashMap<>();

                e = new SAXBuilder().build(new StringReader(joined)).getRootElement();
                for (Element z : e.getChildren("roomFare")) l.put(z.getAttributeValue("room"), new RoomFare(z));


                return new FarePerRoom(l);
            } catch (JDOMException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        }

        //return new ArrayList<>(Arrays.asList(joined.split(",")));
    }

}