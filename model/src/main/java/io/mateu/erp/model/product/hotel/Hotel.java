package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.product.AbstractItem;
import io.mateu.erp.model.product.AbstractPlaceable;
import io.mateu.erp.model.world.City;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Getter
@Setter
public class Hotel extends AbstractPlaceable {

    @ManyToOne
    private HotelCategory category;

    @OneToMany(mappedBy = "hotel")
    private List<Room> rooms = new ArrayList<>();

    @OneToMany(mappedBy = "hotel")
    private List<Board> boards = new ArrayList<>();

    private String stopsales;

}
