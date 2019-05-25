package io.mateu.erp.model.product.tour;


import io.mateu.erp.model.product.transfer.TransferPoint;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Entity
@Getter
@Setter
public class TourPickupTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private ExcursionShift shift;

    @ManyToOne
    @NotNull
    private TransferPoint point;

    /**
     * no se puede vender una excursión si no hay hora de recogida
     */
    @NotNull
    private LocalTime time;

    @Column(name = "_order")
    private int order;
}
