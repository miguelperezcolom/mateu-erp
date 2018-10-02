package io.mateu.erp.model.booking.parts;

import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.generic.GenericService;
import io.mateu.erp.model.booking.generic.GenericServiceExtra;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.Position;
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
