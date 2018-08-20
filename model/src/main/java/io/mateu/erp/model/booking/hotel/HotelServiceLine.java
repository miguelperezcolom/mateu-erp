package io.mateu.erp.model.booking.hotel;

import io.mateu.erp.model.product.hotel.BoardType;
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
    private RoomType roomType;

    @ManyToOne
    @NotNull
    private BoardType boardType;

    @NotNull
    private int numberOfRooms;

    @NotNull
    private int paxPerRoom;

    @Convert(converter = IntArrayAttributeConverter.class)
    @Column(name = "_ages")
    private int[] ages;

    private boolean active;


}
