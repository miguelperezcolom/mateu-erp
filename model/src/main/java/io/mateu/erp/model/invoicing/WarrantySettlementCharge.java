package io.mateu.erp.model.invoicing;


import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.File;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import io.mateu.mdd.core.annotations.Output;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter@Setter
public class WarrantySettlementCharge extends Charge {


    @ManyToOne
    @Output
    private HotelContract hotelContract;

    public boolean isHotelContractVisible() {
        return hotelContract != null;
    }


}
