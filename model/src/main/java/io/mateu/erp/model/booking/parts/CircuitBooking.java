package io.mateu.erp.model.booking.parts;

import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.product.tour.Circuit;
import io.mateu.mdd.core.annotations.Action;
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
    private Circuit circuit;


    public CircuitBooking() {
        setIcons(FontAwesome.BUS.getHtml());
    }


    @Override
    public void validate() throws Exception {

    }

    @Override
    protected void generateServices(EntityManager em) {

    }

    @Override
    public void priceServices() {

    }
}
