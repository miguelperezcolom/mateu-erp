package io.mateu.erp.model.product.tour;

import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.erp.model.world.Resort;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter@Setter
public class TourPriceZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @OneToMany
    private List<Resort> zones = new ArrayList<>();

    @ManyToOne
    private TransferPoint meetingPoint;

}
