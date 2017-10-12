package io.mateu.erp.model.product.hotel.offer;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Convert;
import javax.persistence.Entity;

@Entity
@Getter@Setter
public class PriceOffer extends AbstractHotelOffer {

    @Convert(converter = FarePerRoomConverter.class)
    private FarePerRoom farePerRoom = new FarePerRoom();


}
