package io.mateu.erp.model.product.hotel;


import org.jdom2.JDOMException;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;

@Converter
public class MaxCapacitiesConverter  implements AttributeConverter<MaxCapacities, String> {

    @Override
    public String convertToDatabaseColumn(MaxCapacities maxCapacities) {
        if (maxCapacities == null) return null;
        else return maxCapacities.toString();
    }

    @Override
    public MaxCapacities convertToEntityAttribute(String s) {
        if (s == null || "".equals(s)) return null;
        try {
            return new MaxCapacities(s);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
