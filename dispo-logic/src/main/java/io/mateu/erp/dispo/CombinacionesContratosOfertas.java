package io.mateu.erp.dispo;

import io.mateu.erp.dispo.interfaces.product.IHotelContract;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class CombinacionesContratosOfertas {

    private List<CombinacionContratosOfertas> combinaciones = new ArrayList<>();

    public CombinacionesContratosOfertas(ContratosYOfertas contratosYOfertas) {

        //todo: completar cuando tengamos las ofertas. De momento solo copiamos la lista de contratos


        for (IHotelContract c : contratosYOfertas.getContratos()) {
            CombinacionContratosOfertas cof;
            combinaciones.add(cof = new CombinacionContratosOfertas());
            cof.getContratos().add(c);
        }

    }
}
