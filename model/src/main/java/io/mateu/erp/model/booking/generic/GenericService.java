package io.mateu.erp.model.booking.generic;

import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.financials.Actor;
import io.mateu.ui.mdd.server.annotations.OwnedList;
import io.mateu.ui.mdd.server.annotations.Required;
import io.mateu.ui.mdd.server.annotations.Subtitle;
import io.mateu.ui.mdd.server.interfaces.WithTriggers;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Created by miguel on 12/4/17.
 */
@Entity
@Getter
@Setter
public class GenericService extends Service implements WithTriggers {

    @Required
    private String description;

    @OneToMany(mappedBy = "service")
    @OwnedList
    private List<PriceLine> priceLines = new ArrayList<>();

    @Override
    public void beforeSet(EntityManager em, boolean isNew) throws Exception {

    }

    @Override
    public void afterSet(EntityManager em, boolean isNew) throws Exception {

    }

    @Override
    public void beforeDelete(EntityManager em) throws Exception {

    }

    @Override
    public void afterDelete(EntityManager em) throws Exception {

    }

    @Override
    protected double rate(EntityManager em) throws Throwable {
        if (getPriceLines().size() == 0) throw new Throwable("No price lines");
        double value = 0;
        for (PriceLine l : getPriceLines()) {
            LocalDate start = getStart();
            LocalDate finish = getFinish();
            if (l.getStart() != null) start = l.getStart();
            if (l.getStart() != null) finish = l.getStart();
            long nights = DAYS.between(start, finish) - 1;
            value += l.getUnits() * l.getPricePerUnit() + l.getUnits() * nights * l.getPricePerUnitAndNight();
        }
        return value;
    }

    @Subtitle
    public String getSubitle() {
        return super.toString();
    }
}
