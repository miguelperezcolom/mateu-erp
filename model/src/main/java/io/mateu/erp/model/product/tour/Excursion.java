package io.mateu.erp.model.product.tour;


import io.mateu.erp.model.booking.ManagedEvent;
import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.UseLinkToListView;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
public class Excursion extends Tour {


    @NotNull
    private TourDuration duration;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tour")
    @UseLinkToListView
    private List<ExcursionShift> shifts = new ArrayList<>();


    @Action(order = 4)
    public void generateEvents(EntityManager em) {
        Map<LocalDate, Map<ExcursionShift, ManagedEvent>> byDate = new HashMap<>();
        Map<LocalDate, Map<ExcursionShift, ManagedEvent>> byDateDeprecated = new HashMap<>();
        fill(byDate);
        fill(byDateDeprecated);


        shifts.forEach(c -> {
            for (LocalDate d = c.getStart(); !d.isAfter(c.getEnd()); d = d.plusDays(1)) if (c.getWeekdays()[d.getDayOfWeek().getValue() - 1]) {

                Map<ExcursionShift, ManagedEvent> byShift = byDateDeprecated.get(d);
                if (byShift != null) byShift.remove(c);


                byShift = byDate.get(d);
                if (byShift == null) byDate.put(d, byShift = new HashMap<>());
                if (!byShift.containsKey(c)) {
                    ManagedEvent e;
                    byShift.put(c, e = new ManagedEvent());
                    e.setDate(d);

                    e.setTour(this);
                    getEvents().add(e);

                    e.setShift(c);
                    c.getEvents().add(e);

                    e.setOffice(getOffice());
                    e.setMaxUnits(c.getMaxPax());
                    em.persist(e);
                }

            }
        });

        byDateDeprecated.values().stream().forEach(byShift -> byShift.values().stream().filter(e -> e.getBookings().size() == 0 && e.getServices().size() == 0).forEach(e -> em.remove(e)));
    }

    private void fill(Map<LocalDate,Map<ExcursionShift,ManagedEvent>> byDate) {
        getEvents().forEach(e -> {
            Map<ExcursionShift, ManagedEvent> byShift = byDate.get(e.getShift());
            if (byShift == null) byDate.put(e.getDate(), byShift = new HashMap<>());
            byShift.put(e.getShift(), e);
        });
    }

}
