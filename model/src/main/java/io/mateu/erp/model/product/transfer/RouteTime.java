package io.mateu.erp.model.product.transfer;

import io.mateu.erp.model.world.Resort;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class RouteTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private Resort origin;

    @ManyToOne
    @NotNull
    private Resort destination;


    private int minutes;

}
