package io.mateu.erp.model.product;

import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.product.hotel.offer.DatesRangeListConverter;
import io.mateu.mdd.core.annotations.ListColumn;
import io.mateu.mdd.core.annotations.SearchFilter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
@Getter
@Setter
public class AbstractOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @SearchFilter
    @ListColumn
    private String name;

    @ListColumn
    private boolean includedInContractPdf;

    @ListColumn
    private boolean prepayment;

    @ListColumn
    private boolean active;

    @ListColumn
    private LocalDate bookingWindowFrom;
    @ListColumn
    private LocalDate bookingWindowTo;

    @Convert(converter = DatesRangeListConverter.class)
    private DatesRanges checkinDates = new DatesRanges();

    @Convert(converter = DatesRangeListConverter.class)
    private DatesRanges stayDates = new DatesRanges();


    @SearchFilter
    @ManyToOne
    @NotNull
    private AbstractProduct product;

    @SearchFilter
    @ManyToMany
    private List<AbstractContract> contracts = new ArrayList<>();

    @SearchFilter
    @OneToMany
    private List<Partner> targets = new ArrayList<>();

    @SearchFilter
    @ManyToMany
    private List<AbstractOffer> cumulativeTo = new ArrayList<>();

}
