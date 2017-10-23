package io.mateu.erp.model.revenue;

import io.mateu.ui.mdd.server.annotations.Required;
import io.mateu.ui.mdd.server.annotations.SearchFilter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class MarkupLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @Required
    @SearchFilter
    private Markup markup;

    @ManyToOne
    @Required
    @SearchFilter
    private Product product;

    private boolean active;

    private double minimumRevenuePerBooking;

    private double maximumRevenuePerBooking;

    private double percent;

}
