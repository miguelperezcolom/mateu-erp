package io.mateu.erp.model.product.hotel.offer;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class EarlyBookingOfferLines {

    private List<EarlyBookingOfferLine> lines = new ArrayList<>();

    public EarlyBookingOfferLines() {}

    public EarlyBookingOfferLines(List<EarlyBookingOfferLine> l) {
        setLines(l);
    }

    @Override
    public String toString() {
        String s = "";
        for (EarlyBookingOfferLine l : lines) {
            if (!"".equals(s)) s += ", ";
            s += l.toString();
        }
        return s;
    }
}
