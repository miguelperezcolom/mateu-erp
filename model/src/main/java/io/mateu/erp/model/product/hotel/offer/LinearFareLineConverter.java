package io.mateu.erp.model.product.hotel.offer;

import com.google.common.base.Strings;
import io.mateu.erp.model.product.hotel.LinearFareLine;
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
public class LinearFareLineConverter implements AttributeConverter<LinearFareLine, String> {

    @Override
    public String convertToDatabaseColumn(LinearFareLine l) {

        if (l == null) return null;
        else {
            return new XMLOutputter().outputString(l.toXml());
        }
        /*


        // Java 8
        return String.join(",", list);
        // Guava
        return Joiner.on(',').join(list);
         */
    }

    @Override
    public LinearFareLine convertToEntityAttribute(String joined) {

        if (Strings.isNullOrEmpty(joined)) return null;
        else {

            Element e = null;
            try {
                e = new SAXBuilder().build(new StringReader(joined)).getRootElement();

                return new LinearFareLine(e);
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