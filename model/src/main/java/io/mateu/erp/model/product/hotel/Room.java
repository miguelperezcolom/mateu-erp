package io.mateu.erp.model.product.hotel;

import io.mateu.erp.dispo.interfaces.product.IRoom;
import io.mateu.erp.model.multilanguage.Literal;
import io.mateu.ui.mdd.server.annotations.SearchFilter;
import lombok.Getter;
import lombok.Setter;
import org.easytravelapi.hotel.Occupancy;

import javax.persistence.*;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Getter
@Setter
public class Room implements IRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @SearchFilter
    @ManyToOne
    private Hotel hotel;

    @SearchFilter
    @ManyToOne
    private RoomType type;

    @ManyToOne(cascade = CascadeType.ALL)
    private Literal description;

    private String maxCapacity;

    private int minPax;

    private int minAdultsForChildDiscount;

    private boolean infantsAllowed;

    private boolean childrenAllowed;

    private boolean infantsInBed;

    @Override
    public boolean fits(Occupancy o) {
        boolean ok = o.getPaxPerRoom() >= getMinPax();
        //todo: completar!!!
        return ok;
    }

    @Override
    public String getCode() {
        return getType().getCode();
    }

    @Override
    public String getName() {
        return getType().getName().getEs();
    }
}
