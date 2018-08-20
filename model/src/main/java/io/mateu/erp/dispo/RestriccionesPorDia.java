package io.mateu.erp.dispo;


import io.mateu.erp.model.product.hotel.MinimumStayRule;
import io.mateu.erp.model.product.hotel.ReleaseRule;
import io.mateu.erp.model.product.hotel.WeekDaysRule;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class RestriccionesPorDia {

    private List<ReleaseRule> releases = new ArrayList<>();

    private List<MinimumStayRule> minimumStays = new ArrayList<>();

    private List<WeekDaysRule> weekDays = new ArrayList<>();
}
