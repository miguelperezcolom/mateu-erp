package io.mateu.erp.model.booking.hotel;

import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.world.State;
import io.mateu.ui.mdd.server.AbstractServerSideWizardPage;
import io.mateu.ui.mdd.server.annotations.SameLine;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class Pagina1 extends AbstractServerSideWizardPage {

    @NotNull
    private Actor agency;

    @NotNull
    private State state;

    @NotNull
    private LocalDate checkin = LocalDate.of(2018, 1, 15);

    @SameLine
    @NotNull
    private LocalDate checkout = LocalDate.of(2018, 1, 23);

    @SameLine
    @NotNull
    private LocalDate formalizationDate = LocalDate.now();

    @NotNull
    private List<Occupation> occupations = Arrays.asList(new Occupation(1, 2, null));

    @Override
    public String getTitle() {
        return "Search filters";
    }
}
