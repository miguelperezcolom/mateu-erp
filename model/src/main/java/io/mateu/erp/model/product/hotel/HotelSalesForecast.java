package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.product.hotel.contracting.HotelContract;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Getter@Setter
public class HotelSalesForecast {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private HotelContract hotelContract;

    @NotNull
    private LocalDate start;

    @NotNull
    @Column(name = "_end")
    private LocalDate end;

    private int overnights;

}
