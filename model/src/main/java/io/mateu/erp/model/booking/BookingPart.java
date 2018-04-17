package io.mateu.erp.model.booking;

import io.mateu.ui.mdd.server.annotations.Order;
import io.mateu.ui.mdd.server.annotations.SearchFilter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter@Setter
public class BookingPart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SearchFilter
    @Order(desc = true, priority = 10)
    private long id;

    @ManyToOne
    private Booking booking;

    @OneToMany(mappedBy = "bookingPart")
    private List<Service> services = new ArrayList<>();

    private boolean cancelled;

}
