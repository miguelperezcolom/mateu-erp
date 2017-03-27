package io.mateu.erp.model.booking;

import io.mateu.erp.model.authentication.Audit;
import io.mateu.erp.model.booking.generic.PriceDetail;
import io.mateu.erp.model.product.generic.Product;
import io.mateu.ui.mdd.server.annotations.*;
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
    @Output
    private Audit audit;

    @ManyToOne
    @Required
    private Booking booking;

    @StartsLine
    private boolean cancelled;

    private boolean noShow;


    @TextArea
    private String comment;


    @Ignored
    private LocalDate start;

    @Ignored
    private LocalDate finish;

    @Ignored
    private int units;

    @Ignored
    private int adults;

    @Ignored
    private int children;

    @Ignored
    private int[] ages;

    @Ignored
    private double total;

    @Ignored
    @OneToMany
    private List<PriceDetail> priceBreakdown = new ArrayList<>();
}
