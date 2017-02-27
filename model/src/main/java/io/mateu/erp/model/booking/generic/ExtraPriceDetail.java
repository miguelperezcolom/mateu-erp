package io.mateu.erp.model.booking.generic;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by miguel on 1/2/17.
 */
@Entity
@Getter
@Setter
public class ExtraPriceDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private PriceDetail priceDetail;

    private double basePrice;
    private double pricePerNight;

}
