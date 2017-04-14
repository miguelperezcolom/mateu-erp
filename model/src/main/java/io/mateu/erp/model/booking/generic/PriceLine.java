package io.mateu.erp.model.booking.generic;

import io.mateu.erp.model.product.generic.Contract;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Created by miguel on 13/4/17.
 */
@Entity
@Getter
@Setter
public class PriceLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private GenericService service;

    private double units;
    private LocalDate start;
    private LocalDate finish;
    private String description;

    private double pricePerUnit;
    private double pricePerUnitAndNight;
}
