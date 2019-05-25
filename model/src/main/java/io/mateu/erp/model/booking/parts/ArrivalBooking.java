package io.mateu.erp.model.booking.parts;

import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.PriceBreakdownItem;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.erp.model.revenue.ProductLine;
import io.mateu.mdd.core.annotations.Position;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Getter@Setter
public class ArrivalBooking extends Booking {

    @ManyToOne
    @NotNull
    @Position(2)
    private TransferPoint airport;

    @Position(3)
    private String arrivalFlightNumber;

    @Position(4)
    private LocalDateTime arrivalFlightTime;

    @Position(5)
    private String arrivalFlightOrigin;


    @Position(6)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "booking")
    List<ArrivalBookingLine> lines = new ArrayList<>();


    public boolean isStartVisible() { return false; }
    public boolean isEndVisible() { return false; }
    public boolean isAdultsVisible() { return false; }
    public boolean isChildrenVisible() { return false; }
    public boolean isAgesVisible() { return false; }


    public ArrivalBooking() {
        setIcons(FontAwesome.PLANE.getHtml());
    }


    @Override
    protected void completeChangeSignatureData(Map<String, String> data) {

    }

    @Override
    public void validate() throws Exception {

    }

    @Override
    public void generateServices(EntityManager em) {

        for (ArrivalBookingLine l : getLines()) {

            // traslado llegada



            // estancia


            // traslado salida


        }

    }

    @Override
    protected ProductLine getEffectiveProductLine() {
        return null;
    }

    @Override
    public void priceServices(EntityManager em, List<PriceBreakdownItem> breakdown) {

    }

    @Override
    protected BillingConcept getDefaultBillingConcept(EntityManager em) {
        return AppConfig.get(em).getBillingConceptForTransfer();
    }

    @Override
    protected void completeSignature(Map<String, Object> m) {

    }
}
