package io.mateu.erp.model.product.tour;

import io.mateu.erp.model.booking.ManagedEvent;
import io.mateu.erp.model.partners.Agency;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.Tab;
import io.mateu.mdd.core.annotations.UseCheckboxes;
import io.mateu.mdd.core.annotations.WeekDays;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class ExcursionShift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Tab("Info")
    private String name;

    @ManyToOne
    @NotNull
    private Excursion excursion;

    @Column(name = "_start")
    private LocalDate start;

    @Column(name = "_end")
    private LocalDate end;

    @WeekDays
    private boolean[] weekdays = {true, true, true, true, true, true, true};

    /**
     * en días
     */
    private int release;

    /**
     * hora de inicio
     */
    private LocalTime startTime;


    @ManyToOne
    private Agency agency;


    private int maxPax;

    @OneToMany
    @UseCheckboxes
    @NotEmpty
    private Set<ExcursionLanguage> languages = new HashSet<>();


    @Tab("Pickup times")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "shift")
    private List<TourPickupTime> pickupTimes = new ArrayList<>();



    @OneToMany
    @Ignored
    private List<ManagedEvent> events = new ArrayList<>();


    @Override
    public String toString() {
        return name;
    }
}
