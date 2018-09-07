package io.mateu.erp.model.booking;

import io.mateu.erp.model.booking.parts.TourBooking;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.product.tour.Excursion;
import io.mateu.erp.model.product.tour.Tour;
import io.mateu.erp.model.product.tour.TourShift;
import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.Order;
import io.mateu.mdd.core.annotations.Output;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter@Setter
public class ManagedEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private Office office;


    @ManyToOne
    @NotNull
    private Tour tour;


    private LocalDate date;


    @ManyToOne
    private TourShift shift;

    public boolean isShiftVisible() {
        return tour != null && tour instanceof Excursion;
    }

    private boolean active = true;


    private int maxUnits;

    @Output
    private int unitsBooked;

    @Output
    private int unitsLeft;



    @OneToMany(mappedBy = "managedEvent")
    private List<TourBooking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "managedEvent")
    private List<Service> services = new ArrayList<>();


    @PrePersist@PreUpdate
    public void pre() {
        int bkd = 0;
        for (TourBooking b : bookings) bkd += b.getAdults() + b.getChildren();
        unitsBooked = bkd;
        unitsLeft = maxUnits - unitsBooked;
    }



    @Action(order = 1)
    public URL schedule() {
        return null;
    }


    @Action(order = 2)
    public URL manifest() {
        return null;
    }


    @Action(order = 3)
    public URL pickups() {
        return null;
    }


    @Action(order = 4)
    public URL delivers() {
        return null;
    }

    @Action(order = 5)
    public URL report() {
        return null;
    }


    @Action
    public static void close(EntityManager em, Set<ManagedEvent> sel) {
        sel.forEach(e -> {
            e.setActive(false);
            em.merge(e);
        });
    }

    @Action
    public static void open(EntityManager em, Set<ManagedEvent> sel) {
        sel.forEach(e -> {
            e.setActive(true);
            em.merge(e);
        });
    }

}
