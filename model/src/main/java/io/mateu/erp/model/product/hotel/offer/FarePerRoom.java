package io.mateu.erp.model.product.hotel.offer;

import io.mateu.erp.model.product.hotel.RoomFare;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter@Setter
public class FarePerRoom {

    private Map<String, RoomFare> fares = new HashMap<>();

    public FarePerRoom() {}

    public FarePerRoom(Map<String, RoomFare> l) {
        setFares(l);
    }
}
