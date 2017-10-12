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
}
