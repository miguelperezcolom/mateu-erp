package io.mateu.erp.model.product.transfer;

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
    private TransferPoint origin;

    @ManyToOne
    @NotNull
    private TransferPoint destination;


    private int minutes;

}
