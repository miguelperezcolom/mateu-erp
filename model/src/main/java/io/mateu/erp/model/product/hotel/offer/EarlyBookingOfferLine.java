package io.mateu.erp.model.product.hotel.offer;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class EarlyBookingOfferLine {

    private int release;

    private double discountPercent;

    public EarlyBookingOfferLine() {

    }

    public EarlyBookingOfferLine(int release, double discountPercent) {
        this.release = release;
        this.discountPercent = discountPercent;
    }
}
