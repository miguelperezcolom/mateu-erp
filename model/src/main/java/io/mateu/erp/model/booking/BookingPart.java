package io.mateu.erp.model.booking;

import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.Order;
import io.mateu.mdd.core.annotations.SearchFilter;
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

    private boolean directSale;

    @OneToMany(mappedBy = "bookingPart")
    @Ignored
    private List<Service> services = new ArrayList<>();

    private boolean cancelled;

}
