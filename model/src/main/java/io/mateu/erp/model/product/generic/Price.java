package io.mateu.erp.model.product.generic;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 31/1/17.
 */
@Entity
@Getter
@Setter
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Contract contract;

    @ManyToOne
    private Product product;

    private LocalDate start;

    private LocalDate end;

    private double pricePerUnit;
    private double pricePerAdult;
    private double pricePerChild;
    private double pricePerUnitAndDay;
    private double pricePerAdultAndDay;
    private double pricePerChildAndDay;

    @OneToMany(mappedBy = "price")
    private List<ExtraPrice> pricePerExtra = new ArrayList<>();



}
