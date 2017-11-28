package io.mateu.erp.dispo;

import io.mateu.erp.dispo.interfaces.product.IHotelContract;
import io.mateu.erp.dispo.interfaces.product.IHotelOffer;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class CombinacionesContratosOfertas {

    private List<CombinacionContratosOfertas> combinaciones = new ArrayList<>();

    public CombinacionesContratosOfertas(ContratosYOfertas contratosYOfertas) {


        for (IHotelContract c : contratosYOfertas.getContratos()) {

            List<IHotelOffer> ofertasValidasParaEsteContrato = new ArrayList<>();
            for (IHotelOffer o : contratosYOfertas.getOfertas()) {
                if (o.getContracts().contains(c)) ofertasValidasParaEsteContrato.add(o);
            }

            List<List<IHotelOffer>> combinacionesDeOfertasValidasParaEsteContrato = new ArrayList<>();
            for (int pos = 0; pos < ofertasValidasParaEsteContrato.size(); pos++) {
                List<IHotelOffer> combinacion;
                combinacionesDeOfertasValidasParaEsteContrato.add(combinacion = new ArrayList<>());
                IHotelOffer o;
                combinacion.add(o = ofertasValidasParaEsteContrato.get(pos));
                for (int posx = pos + 1; posx < ofertasValidasParaEsteContrato.size(); posx++) {
                    IHotelOffer ox = ofertasValidasParaEsteContrato.get(posx);
                    if (o.getCumulativeTo().contains(ox)) combinacion.add(ox);
                }
            }


            if (combinacionesDeOfertasValidasParaEsteContrato.size() == 0) {
                CombinacionContratosOfertas cof;
                combinaciones.add(cof = new CombinacionContratosOfertas());
                cof.getContratos().add(c);
            } else for (List<IHotelOffer> combinacionOfertas : combinacionesDeOfertasValidasParaEsteContrato) {
                CombinacionContratosOfertas cof;
                combinaciones.add(cof = new CombinacionContratosOfertas());
                cof.getContratos().add(c);
                cof.setOfertas(combinacionOfertas);
            }

        }

    }
}
