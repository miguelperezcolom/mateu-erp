package io.mateu.erp.model.product.hotel;

import io.mateu.erp.model.product.AbstractPlaceableItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 1/10/16.
 */
public class Hotel extends AbstractPlaceableItem {

    HotelCategory category;

    List<Room> rooms = new ArrayList<>();

    List<Board> boards = new ArrayList<>();

    private String stopsales;

}
