package io.mateu.erp.model.booking.parts;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.product.hotel.Board;
import io.mateu.erp.model.product.hotel.Room;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Getter@Setter
public class HotelBookingLine {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private Booking booking;

    @NotNull
    private LocalDate start;
    @NotNull@Column(name = "_end")
    private LocalDate end;

    @ManyToOne
    private Room room;

    @ManyToOne
    private Board board;

    private int rooms;
    private int adultsPerRoon;
    private int childrenPerRoom;
    private int[] ages;

    private boolean cancelled;

}
