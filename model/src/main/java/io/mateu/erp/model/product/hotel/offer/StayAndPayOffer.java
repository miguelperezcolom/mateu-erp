package io.mateu.erp.model.product.hotel.offer;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter@Setter
public class StayAndPayOffer extends AbstractHotelOffer {

    private int stayNights;

    private int payNights;

    private WhichNights whichNights;

}
