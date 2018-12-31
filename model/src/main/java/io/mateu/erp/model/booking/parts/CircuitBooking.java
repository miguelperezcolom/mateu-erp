package io.mateu.erp.model.booking.parts;

import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import io.mateu.erp.model.product.tour.Circuit;
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
public class CircuitBooking extends TourBooking {

    @ManyToOne
    @NotNull
    @Position(13)
    private Circuit circuit;


    @ManyToOne
    @NotNull
    @Position(14)
    private TourVariant variant;

    public boolean isEndOutput() { return true; }


    public CircuitBooking() {
        setIcons(FontAwesome.GLOBE.getHtml());
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
