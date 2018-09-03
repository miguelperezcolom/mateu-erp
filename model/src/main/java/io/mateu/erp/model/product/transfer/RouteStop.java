package io.mateu.erp.model.product.transfer;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class RouteStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private Route route;

    @NotNull
    @ManyToOne
    private TransferPoint point;

    private int orderInRoute;

    @Override
    public String toString() {
        return "" + (getRoute() != null?getRoute().getName():"No route") + " - " + (getPoint() != null?getPoint().getName():"No point");
    }
}
