package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.multilanguage.Literal;

/**
 * Created by miguel on 1/10/16.
 */
public class Room {

    private RoomType type;

    private Literal description;

    private String maxCapacity;

    private int minPax;

    private int minAdultsForChildDiscount;

    private boolean infantsAllowed;

    private boolean childrenAllowed;

    private boolean infantsInBed;
}
