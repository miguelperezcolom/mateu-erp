package io.mateu.erp.model.booking.hotel;

import io.mateu.erp.model.booking.parts.HotelBookingLine;
import io.mateu.erp.model.product.hotel.Board;
import io.mateu.erp.model.product.hotel.BoardType;
import io.mateu.erp.model.product.hotel.Room;
import io.mateu.erp.model.product.hotel.RoomType;
import io.mateu.mdd.core.model.util.IntArrayAttributeConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class HotelServiceLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private HotelService service;

    @NotNull
    private LocalDate start;

    @Column(name = "_end")
    @NotNull
    private LocalDate end;

    @ManyToOne
    @NotNull
    private Room room;

    @ManyToOne
    @NotNull
    private Board board;

    @NotNull
    private int numberOfRooms;

    @NotNull
    private int adultsPerRoom;

    @NotNull
    private int childrenPerRoom;

    @Convert(converter = IntArrayAttributeConverter.class)
    @Column(name = "_ages")
    private int[] ages;

    private boolean active;


    public HotelServiceLine() {

    }

    public HotelServiceLine(HotelService service, HotelBookingLine l) {
        this.service = service;
        start = l.getStart();
        end = l.getEnd();
        room = l.getRoom();
        board = l.getBoard();
        numberOfRooms = l.getRooms();
        adultsPerRoom = l.getAdultsPerRoon();
        childrenPerRoom = l.getChildrenPerRoom();
        ages = l.getAges();
        active = l.isActive();
    }
}
