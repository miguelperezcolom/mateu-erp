package io.mateu.erp.client.operations;

import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.product.ProductType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter@Setter
public class GenericServiceCalendarRow extends ServiceCalendarRow {

    private ProductType productType;

    public GenericServiceCalendarRow(LocalDate date, Office office, ProductType productType, long bookings, int minProcessingStatus) {
        super(date, office, bookings, minProcessingStatus);
        this.productType = productType;
    }
}
