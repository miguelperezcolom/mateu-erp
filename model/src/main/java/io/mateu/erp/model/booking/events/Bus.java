package io.mateu.erp.model.booking.events;

import io.mateu.erp.model.booking.ManagedEvent;
import io.mateu.erp.model.product.transfer.Route;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity@Getter@Setter
public class Bus extends ManagedEvent {

    private String nameplate;

    private String name;

    @ManyToOne
    private Route route;
}
