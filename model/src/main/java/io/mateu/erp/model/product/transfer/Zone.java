package io.mateu.erp.model.product.transfer;

import io.mateu.mdd.core.annotations.QLForCombo;
import io.mateu.mdd.core.annotations.SearchFilter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 25/2/17.
 */
@Entity(name = "TransferContractZone")
@Getter
@Setter
@QLForCombo(ql = "select x.id, concat(coalesce(x.group, '*'), ' - ', x.name) from TransferContractZone x order by x.group, x.name")
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "_group")
    @SearchFilter
    private String group;

    @NotNull
    @SearchFilter
    private String name;

    @OneToMany
    private List<TransferPoint> points = new ArrayList<>();

    @OneToMany
    private List<io.mateu.erp.model.world.Zone> cities = new ArrayList<>();
}
