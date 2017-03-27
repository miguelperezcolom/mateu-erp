package io.mateu.erp.model.product.transfer;

import io.mateu.erp.model.world.City;
import io.mateu.ui.mdd.server.annotations.Required;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 25/2/17.
 */
@Entity(name = "TransferContractZone")
@Getter
@Setter
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Required
    private String name;

    @OneToMany
    private List<TransferPoint> points = new ArrayList<>();

    @OneToMany
    private List<City> cities = new ArrayList<>();
}
