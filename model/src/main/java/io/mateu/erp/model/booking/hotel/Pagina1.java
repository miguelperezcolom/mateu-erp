package io.mateu.erp.model.booking.hotel;

import io.mateu.erp.model.partners.Agency;
import io.mateu.erp.model.world.Destination;
import io.mateu.mdd.core.annotations.SameLine;
import io.mateu.mdd.core.interfaces.WizardPage;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class Pagina1 implements WizardPage {

    @NotNull
    private Agency agency;

    @NotNull
    private Destination state;

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
    public String toString() {
        return "Search filters";
    }

    @Override
    public WizardPage getPrevious() {
        return null;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public WizardPage getNext() {
        return new Pagina2(this);
    }
}
