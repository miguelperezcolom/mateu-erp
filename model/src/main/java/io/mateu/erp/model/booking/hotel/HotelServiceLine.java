package io.mateu.erp.model.booking.hotel;

import io.mateu.erp.model.product.hotel.BoardType;
import io.mateu.erp.model.product.hotel.RoomType;
import io.mateu.ui.mdd.server.annotations.Required;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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

    @Required
    private LocalDate start;

    @Column(name = "_end")
    @Required
    private LocalDate end;

    @ManyToOne
    @Required
    private RoomType roomType;

    @ManyToOne
    @Required
    private BoardType boardType;

    @Required
    private int numberOfRooms;

    @Required
    private int paxPerRoom;


    private int[] ages;

    private boolean active;


}
