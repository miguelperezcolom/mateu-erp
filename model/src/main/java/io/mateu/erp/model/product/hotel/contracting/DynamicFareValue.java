package io.mateu.erp.model.product.hotel.contracting;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class DynamicFareValue {

    private double percent;

    private double value;

    public DynamicFareValue() {

    }

    public DynamicFareValue(double percent, double value) {
        this.percent = percent;
        this.value = value;
    }
}
