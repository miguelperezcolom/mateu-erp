package io.mateu.erp.model.booking.parts;

import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.generic.GenericService;
import io.mateu.erp.model.booking.generic.GenericServiceExtra;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter@Setter
public class ArrivalBooking extends Booking {

    @ManyToOne
    @NotNull
    private TransferPoint airport;

    private String arrivalFlightNumber;

    private LocalDateTime arrivalFlightTime;

    private String arrivalFlightOrigin;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "booking")
    List<ArrivalBookingLine> lines = new ArrayList<>();

    public ArrivalBooking() {
        setIcons(FontAwesome.PLANE.getHtml());
    }


    @Override
    public void validate() throws Exception {

    }

    @Override
    protected void generateServices(EntityManager em) {

        for (ArrivalBookingLine l : getLines()) {

            // traslado llegada



            // estancia


            // traslado salida


        }

    }

    @Override
    public void priceServices() {

    }

}
