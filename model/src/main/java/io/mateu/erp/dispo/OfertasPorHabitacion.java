package io.mateu.erp.dispo;

import io.mateu.erp.dispo.interfaces.product.IHotelOffer;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class OfertasPorHabitacion {

    private List<IHotelOffer> ofertas = new ArrayList<>();

}
