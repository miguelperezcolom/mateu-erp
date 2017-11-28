package io.mateu.erp.dispo;


import io.mateu.erp.model.product.hotel.MinimumStayRule;
import io.mateu.erp.model.product.hotel.ReleaseRule;
import io.mateu.erp.model.product.hotel.Supplement;
import io.mateu.erp.model.product.hotel.WeekDaysRule;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class SuplementosPorDia {

    private List<Supplement> suplementos = new ArrayList<>();
}
