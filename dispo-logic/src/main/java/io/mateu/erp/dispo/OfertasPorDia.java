package io.mateu.erp.dispo;


import io.mateu.erp.dispo.interfaces.product.IHotelOffer;
import io.mateu.erp.model.product.hotel.Supplement;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class OfertasPorDia {

    private List<IHotelOffer> ofertas = new ArrayList<>();
}
