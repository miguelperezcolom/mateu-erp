package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.multilanguage.Literal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by miguel on 1/10/16.
 */
@Entity
@Getter
@Setter
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Hotel hotel;

    @ManyToOne
    private RoomType type;

    @ManyToOne
    private Literal description;

    private String maxCapacity;

    private int minPax;

    private int minAdultsForChildDiscount;

    private boolean infantsAllowed;

    private boolean childrenAllowed;

    private boolean infantsInBed;
}
