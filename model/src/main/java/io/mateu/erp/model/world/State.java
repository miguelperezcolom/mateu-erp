package io.mateu.erp.model.world;

import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.ui.mdd.server.annotations.Ignored;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * holder for states
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Getter@Setter
public class State {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull
    private Country country;

    @NotNull
    private String name;

    @OneToMany(mappedBy = "state")
    @Ignored
    private List<City> cities = new ArrayList<>();

    @OneToMany(mappedBy = "gatewayOf")
    @Ignored
    private List<TransferPoint> gateways = new ArrayList<>();

}
