package io.mateu.erp.model.booking.parts;

import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.product.transfer.TransferType;
import io.mateu.mdd.core.annotations.Position;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter@Setter
public class HotelAndTransferBooking extends HotelBooking {


    @NotNull
    @Position(5)
    private TransferType transferType;

    @Position(6)
    private boolean roundTrip;


    @Position(7)
    private String arrivalFlightNumber;

    @Position(8)
    private LocalDateTime arrivalFlightTime;

    @Position(9)
    private String arrivalFlightOrigin;



    @Position(10)
    private String departureFlightNumber;

    @Position(11)
    private LocalDateTime departureFlightTime;

    @Position(12)
    private String departureFlightDestination;


    public HotelAndTransferBooking() {
        setIcons(FontAwesome.HOTEL.getHtml() + " " + FontAwesome.BUS.getHtml());
    }
}
