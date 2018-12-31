package io.mateu.erp.model.booking.parts;


import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.product.hotel.Board;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.erp.model.product.hotel.Room;
import io.mateu.mdd.core.util.StringListConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class ArrivalBookingLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private Booking booking;

    @ManyToOne
    private Hotel hotel;

    @ManyToOne
    private Room room;

    @ManyToOne
    private Board board;

    private int rooms;
    private int adultsPerRoon;
    private int childrenPerRoom;
    private int[] ages;

    @Convert(converter = StringListConverter.class)
    private List<String> paxNames = new ArrayList<>();


    private boolean roundTrip;

    private String departureFlightNumber;

    private LocalDateTime departureFlightTime;

    private String departureFlightDestination;




    private boolean cancelled;

}
