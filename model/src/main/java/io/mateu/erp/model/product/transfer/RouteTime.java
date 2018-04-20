package io.mateu.erp.model.product.transfer;

import io.mateu.erp.model.world.City;
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
    private City origin;

    @ManyToOne
    @NotNull
    private City destination;


    private int minutes;

}
