package io.mateu.erp.model.booking.hotel;

import io.mateu.erp.model.product.hotel.BoardType;
import io.mateu.erp.model.product.hotel.RoomType;
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

    private LocalDate start;

    @Column(name = "_end")
    private LocalDate end;

    @ManyToOne
    private RoomType roomType;

    @ManyToOne
    private BoardType boardType;

    private int pax;


    private int[] ages;

    private boolean active;


}
