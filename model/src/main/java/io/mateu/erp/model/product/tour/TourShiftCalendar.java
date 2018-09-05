package io.mateu.erp.model.product.tour;

import io.mateu.erp.model.partners.Partner;
import io.mateu.mdd.core.annotations.WeekDays;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class TourShiftCalendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private TourShift shift;


    @Column(name = "_start")
    private LocalDate start;

    @Column(name = "_end")
    private LocalDate end;

    @WeekDays
    private boolean[] weekdays = {true, true, true, true, true, true, true};

    @ManyToOne
    private Partner agency;

    private int allotment;

}
