package io.mateu.erp.model.product.hotel.offer;

import io.mateu.erp.model.product.hotel.DatesRanges;
import io.mateu.ui.mdd.server.util.DatesRange;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Converter(autoApply = true)
public class DatesRangeListConverter implements AttributeConverter<DatesRanges, String> {

    @Override
    public String convertToDatabaseColumn(DatesRanges list) {

        if (list == null) return null;
        else {
            StringBuffer sb = new StringBuffer();
            for (DatesRange r : list.getRanges()) {
                if (sb.length() > 0) sb.append(",");
                if (r.getStart() != null) sb.append(r.getStart().toString());
                else sb.append("_");
                sb.append("|");
                if (r.getEnd() != null) sb.append(r.getEnd().toString());
                else sb.append("_");
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
    public DatesRanges convertToEntityAttribute(String joined) {

        if (joined == null) return null;
        else {
            List<DatesRange> l = new ArrayList<>();

            for (String z : joined.split(",")) if (!"".equals(z)) {
                DatesRange r;
                l.add(r = new DatesRange());
                String[] x = z.split("|");
                if (!"_".equals(x[0])) r.setStart(LocalDate.parse(x[0]));
                if (!"_".equals(x[1])) r.setEnd(LocalDate.parse(x[1]));
            }

            return new DatesRanges(l);
        }

        //return new ArrayList<>(Arrays.asList(joined.split(",")));
    }

}