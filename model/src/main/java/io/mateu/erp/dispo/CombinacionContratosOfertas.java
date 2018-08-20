package io.mateu.erp.dispo;

import io.mateu.erp.dispo.interfaces.product.IHotelContract;
import io.mateu.erp.dispo.interfaces.product.IHotelOffer;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class CombinacionContratosOfertas {

    private List<IHotelContract> contratos = new ArrayList<>();

    private List<IHotelOffer> ofertas = new ArrayList<>();

}
