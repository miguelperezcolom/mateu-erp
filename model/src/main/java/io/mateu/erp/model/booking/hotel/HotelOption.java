package io.mateu.erp.model.booking.hotel;

import io.mateu.mdd.core.annotations.CellStyleGenerator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotelOption {

    private String id;

    private String city;

    private String hotelName;

    private String category;

    @CellStyleGenerator(BestDealCellStyleGenerator.class)
    private String bestDeal;

}
