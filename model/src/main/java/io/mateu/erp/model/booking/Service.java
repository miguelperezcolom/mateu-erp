package io.mateu.erp.model.booking;

import io.mateu.erp.model.authentication.Audit;
import io.mateu.erp.model.booking.generic.PriceDetail;
import io.mateu.erp.model.product.generic.Product;
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

    private LocalDate start;

    private LocalDate finish;

    private int units;

    private int adults;

    private int children;

    private int[] ages;

    private double total;

    @OneToMany
    private List<PriceDetail> priceBreakdown = new ArrayList<>();
}
