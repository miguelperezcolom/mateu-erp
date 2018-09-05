package io.mateu.erp.model.product.hotel.contracting;

import io.mateu.erp.model.product.hotel.HotelContractPhoto;
import org.jdom2.JDOMException;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;

@Converter
public class HotelContractPhotoConverter implements AttributeConverter<HotelContractPhoto, String> {


    @Override
    public String convertToDatabaseColumn(HotelContractPhoto hotelContractPhoto) {
        if (hotelContractPhoto == null) return null;
        else return hotelContractPhoto.serialize();
    }

    @Override
    public HotelContractPhoto convertToEntityAttribute(String s) {
        if (s == null || "".equals(s)) return null;
        try {
            return new HotelContractPhoto(s);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
