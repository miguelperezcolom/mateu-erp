package io.mateu.erp.model.booking;

import io.mateu.erp.model.product.generic.Contract;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 1/2/17.
 */
@Entity
@Getter
@Setter
public class PriceDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Contract contract;

    private double pricePerUnit;
    private double pricePerAdult;
    private double pricePerChild;
    private double pricePerUnitAndDay;
    private double pricePerAdultAndDay;
    private double pricePerChildAndDay;

    @OneToMany(mappedBy = "priceDetail")
    private List<ExtraPriceDetail> pricesForExtras = new ArrayList<>();

}
