package io.mateu.erp.model.booking.parts;

import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.product.transfer.TransferType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter@Setter
public class HotelAndTransferBooking extends HotelBooking {


    @NotNull
    private TransferType transferType;

    private boolean roundTrip;


    private String arrivalFlightNumber;

    private LocalDateTime arrivalFlightTime;

    private String arrivalFlightOrigin;



    private String departureFlightNumber;

    private LocalDateTime departureFlightTime;

    private String departureFlightDestination;


    public HotelAndTransferBooking() {
        setIcons(FontAwesome.HOTEL.getHtml() + " " + FontAwesome.BUS.getHtml());
    }
}
