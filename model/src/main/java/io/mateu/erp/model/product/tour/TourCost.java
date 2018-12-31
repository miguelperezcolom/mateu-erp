package io.mateu.erp.model.product.tour;

import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.product.AbstractProduct;
import io.mateu.erp.model.product.ProductType;
import io.mateu.erp.model.product.generic.Extra;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter@Setter
public class TourCost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private Tour tour;

    @ManyToOne
    @NotNull
    private TourVariant variant;

    @ManyToOne
    private Extra extra;

    @NotNull
    @ManyToOne
    private ProductType type;

    @ManyToOne
    private AbstractProduct product;

    @ManyToOne
    private Partner supplier;

    @Column(name = "_order")
    private int order;

    private int day;

    private int nights;

}
