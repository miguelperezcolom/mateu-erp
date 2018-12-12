package io.mateu.erp.model.product.hotel.offer;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.List;

@Converter
public class EarlyBookingOfferLineConverter implements AttributeConverter<EarlyBookingOfferLines, String> {

    @Override
    public String convertToDatabaseColumn(EarlyBookingOfferLines list) {

        if (list == null) return null;
        else {
            StringBuffer sb = new StringBuffer();
            for (EarlyBookingOfferLine r : list.getLines()) {
                if (sb.length() > 0) sb.append(",");
                sb.append(r.getRelease());
                sb.append("|");
                sb.append(r.getDiscountPercent());
            }
            return sb.toString();
        }
        /*


        // Java 8
        return String.join(",", list);
        // Guava
        return Joiner.on(',').join(list);
         */
    }

    @Override
    public EarlyBookingOfferLines convertToEntityAttribute(String joined) {

        if (joined == null) return null;
        else {
            List<EarlyBookingOfferLine> l = new ArrayList<>();

            for (String z : joined.split(",")) if (!"".equalsIgnoreCase(z)) {
                EarlyBookingOfferLine r;
                l.add(r = new EarlyBookingOfferLine());
                String[] x = z.split("\\|");
                r.setRelease(Integer.parseInt(x[0]));
                r.setDiscountPercent(Double.parseDouble(x[1]));
            }

            return new EarlyBookingOfferLines(l);
        }

        //return new ArrayList<>(Arrays.asList(joined.split(",")));
    }

}
