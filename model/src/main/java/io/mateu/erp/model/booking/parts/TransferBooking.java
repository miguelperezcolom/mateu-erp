package io.mateu.erp.model.booking.parts;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.erp.model.product.transfer.TransferType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter@Setter
public class TransferBooking extends Booking {

    @ManyToOne@NotNull
    private TransferPoint origin;

    @ManyToOne@NotNull
    private TransferPoint destination;

    @NotNull
    private TransferType transferType;

    private boolean roundTrip;


    private String arrivalFlightNumber;

    private LocalDateTime arrivalFlightTime;

    private String arrivalFlightOrigin;



    private String departureFlightNumber;

    private LocalDateTime departureFlightTime;

    private String departureFlightDestination;




}
