package io.mateu.erp.model.booking.parts;

import io.mateu.erp.model.product.tour.Excursion;
import io.mateu.erp.model.product.tour.TourShift;
import io.mateu.erp.model.product.tour.TourVariant;
import io.mateu.mdd.core.annotations.Position;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
@Getter@Setter
public class ExcursionBooking extends TourBooking {

    @ManyToOne
    @NotNull
    @Position(13)
    private Excursion excursion;


    @ManyToOne
    @NotNull
    @Position(14)
    private TourVariant variant;

    @ManyToOne
    @NotNull
    @Position(15)
    private TourShift shift;


    public boolean isEndOutput() { return true; }


    public ExcursionBooking() {
        setIcons("<i class=\"v-icon fas fa-hiking\"></i>");
    }


    @Override
    public void validate() throws Exception {

    }

    @Override
    public void generateServices(EntityManager em) {

    }

    @Override
    public void priceServices() {

    }
}
