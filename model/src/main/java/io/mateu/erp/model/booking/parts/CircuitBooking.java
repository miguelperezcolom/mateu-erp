package io.mateu.erp.model.booking.parts;

import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import io.mateu.erp.model.product.Variant;
import io.mateu.erp.model.product.tour.Circuit;
import io.mateu.mdd.core.annotations.Position;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.Map;

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
    private Variant variant;

    public boolean isEndOutput() { return true; }


    public CircuitBooking() {
        setIcons(FontAwesome.GLOBE.getHtml());
    }


    @Override
    protected void completeChangeSignatureData(Map<String, String> data) {

    }

    @Override
    public void validate() throws Exception {

    }

    @Override
    public void generateServices(EntityManager em) {

    }

    @Override
    public void priceServices(EntityManager em) {

    }

    @Override
    protected void completeSignature(Map<String, Object> m) {

    }
}
