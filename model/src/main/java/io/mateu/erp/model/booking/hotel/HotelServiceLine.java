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

    private LocalDate start;

    @Column(name = "_end")
    private LocalDate end;

    private RoomType roomType;

    private BoardType boardType;

    private int pax;

    private int[] ages;

    private boolean active;


}
