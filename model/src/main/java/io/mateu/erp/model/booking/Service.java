package io.mateu.erp.model.booking;

import io.mateu.erp.model.authentication.Audit;
import io.mateu.erp.model.product.generic.Product;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 31/1/17.
 */
@Entity
@Getter
@Setter
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Embedded
    private Audit audit;

    @ManyToOne
    private Booking booking;

    @ManyToOne
    private Product product;

    private boolean cancelled;

    private int start;

    private int end;

    private int units;

    private int adults;

    private int children;

    private double total;

    @OneToMany
    private List<PriceDetail> priceBreakdown = new ArrayList<>();
}
