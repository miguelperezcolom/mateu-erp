package io.mateu.erp.model.booking.parts;

import io.mateu.erp.model.booking.Booking;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class ThirdPartyHotelBookingLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private Booking booking;

    @NotNull
    private LocalDate start;
    @NotNull@Column(name = "_end")
    private LocalDate end;

    private String roomCode;
    private String roomName;

    private String boardCode;
    private String boardName;

    private int rooms;
    private int adultsPerRoon;
    private int childrenPerRoom;
    private int[] ages;

    private boolean cancelled;

}
