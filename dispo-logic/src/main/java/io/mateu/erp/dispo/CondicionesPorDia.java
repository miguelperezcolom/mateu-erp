package io.mateu.erp.dispo;

import io.mateu.erp.model.product.hotel.RoomFare;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter@Setter
public class CondicionesPorDia {

    private Map<String, RoomFare> farePerRoom = new HashMap<>();
}
