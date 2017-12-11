package io.mateu.erp.model.product.hotel.contracting;

import org.jdom2.JDOMException;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;

@Converter
public class DynamicFaresConverter implements AttributeConverter<DynamicFares, String> {

    @Override
    public String convertToDatabaseColumn(DynamicFares dynamicFares) {
        if (dynamicFares == null) return null;
        else return dynamicFares.toString();
    }

    @Override
    public DynamicFares convertToEntityAttribute(String s) {
        if (s == null || "".equals(s)) return null;
        try {
            return new DynamicFares(s);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
