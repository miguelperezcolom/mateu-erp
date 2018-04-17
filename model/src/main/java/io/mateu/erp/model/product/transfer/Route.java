package io.mateu.erp.model.product.transfer;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @ManyToOne
    @NotNull
    private TransferPoint airport;


    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "route")
    @OrderColumn(name = "orderInRoute")
    private List<RouteStop> stops = new ArrayList<>();


}
