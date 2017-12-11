package io.mateu.erp.dispo;


import io.mateu.erp.model.product.hotel.Supplement;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class SuplementosPorDia {

    private List<Supplement> suplementos = new ArrayList<>();
}
