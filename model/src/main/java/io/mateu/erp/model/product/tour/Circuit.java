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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
public class Circuit extends Tour {

    @OneToMany(mappedBy = "circuit", cascade = CascadeType.ALL)
    @UseLinkToListView
    private List<CircuitCalendar> schedule = new ArrayList<>();

    @Action(order = 4)
    public void generateEvents(EntityManager em) {
        Map<LocalDate, ManagedEvent> byDate = new HashMap<>();
        getEvents().forEach(e -> byDate.put(e.getDate(), e));

        Map<LocalDate, ManagedEvent> byDateDeprecated = new HashMap<>(byDate);

        schedule.forEach(c -> {
            for (LocalDate d = c.getStart(); !d.isAfter(c.getEnd()); d = d.plusDays(1)) if (c.getWeekdays()[d.getDayOfWeek().getValue() - 1]) {
                byDateDeprecated.remove(d);
                if (!byDate.containsKey(d)) {
                    ManagedEvent e;
                    byDate.put(d, e = new ManagedEvent());
                    e.setDate(d);
                    e.setTour(this);
                    getEvents().add(e);
                    e.setOffice(getOffice());
                    e.setMaxUnits(c.getMaxPax());
                    em.persist(e);
                }
            }
        });

        byDateDeprecated.values().stream()
        .filter(e -> e.getBookings().size() == 0 && e.getServices().size() == 0).forEach(e -> em.remove(e));
    }

}
