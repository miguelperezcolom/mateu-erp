package io.mateu.erp.model.product.hotel.offer;

import io.mateu.erp.model.product.hotel.Room;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter@Setter
public class RoomUpgradeOffer extends AbstractHotelOffer {

    private Room get;

    private Room pay;

}
