package io.mateu.erp.model.booking.hotel;

import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.world.State;
import io.mateu.ui.mdd.server.AbstractServerSideWizardPage;
import io.mateu.ui.mdd.server.annotations.Required;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class Pagina1 extends AbstractServerSideWizardPage {

    @Required
    private Actor agency;

    @Required
    private State state;

    @Required
    private LocalDate checkin = LocalDate.of(2018, 1, 15);

    @Required
    private LocalDate checkout = LocalDate.of(2018, 1, 23);

    @Required
    private List<Occupation> occupations = Arrays.asList(new Occupation(1, 2, null));

    @Override
    public String getTitle() {
        return "Search filters";
    }
}
