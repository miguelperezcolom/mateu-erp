package io.mateu.erp.model.booking.transfer;

import io.mateu.erp.model.booking.Service;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * Created by miguel on 25/2/17.
 */
@Entity
@Getter
@Setter
public class TransferService extends Service {

    @OneToOne
    private Pickup pickup;

    @OneToOne
    private Dropoff dropoff;

    @Embedded
    private FlightInfo flight;

    private int pax;

    /*
    private int bikes;
    private int golfBags;
    */

    private String comment;
}
