package io.mateu.erp.model.product.hotel.offer;

import io.mateu.erp.model.product.hotel.BoardType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter@Setter
public class BoardUpgradeOffer extends AbstractHotelOffer {

    @ManyToOne
    private BoardType get;

    @ManyToOne
    private BoardType pay;

}
