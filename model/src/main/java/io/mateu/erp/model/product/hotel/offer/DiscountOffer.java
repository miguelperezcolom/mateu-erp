package io.mateu.erp.model.product.hotel.offer;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter@Setter
public class DiscountOffer extends AbstractHotelOffer {

    private Per per;

    private Scope scope;

    private boolean percent;

    private double value;
}
