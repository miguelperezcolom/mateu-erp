package io.mateu.erp.model.booking.parts;

import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.generic.GenericService;
import io.mateu.erp.model.booking.generic.GenericServiceExtra;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.erp.model.product.transfer.TransferType;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.Position;
import io.mateu.mdd.core.annotations.SameLine;
import io.mateu.mdd.core.annotations.TextArea;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter@Setter
public class TransferBooking extends Booking {

    public boolean isStartVisible() { return false; }
    public boolean isEndVisible() { return false; }

    @Position(13)
    private int bigLuggages;

    @SameLine
    @Position(14)
    private int golf;

    @SameLine
    @Position(15)
    private int bikes;


    @ManyToOne@NotNull
    @Position(16)
    private TransferPoint origin;

    @TextArea
    @Position(17)
    private String originAddress;

    @ManyToOne@NotNull
    @Position(18)
    private TransferPoint destination;

    @TextArea
    @Position(19)
    private String destinationAddress;


    @NotNull
    @Position(20)
    private TransferType transferType;


    @Position(21)
    private LocalDateTime arrivalFlightTime;

    @Position(22)
    private String arrivalFlightNumber;


    @Position(23)
    private String arrivalFlightOrigin;



    @Position(24)
    private LocalDateTime departureFlightTime;

    @Position(25)
    private String departureFlightNumber;

    @Position(26)
    private String departureFlightDestination;


    public TransferBooking() {
        setIcons(FontAwesome.BUS.getHtml());
    }


    @Override
    public void validate() throws Exception {

    }

    @Override
    public void generateServices(EntityManager em) {
        //el primer servicio siempre
        if (arrivalFlightTime != null) {
            TransferService s = null;
            if (getServices().size() > 0) {
                s = (TransferService) getServices().get(0);
            }
            if (s == null) {
                getServices().add(s = new TransferService());
                s.setBooking(this);
                s.setAudit(new Audit(em.find(User.class, MDD.getUserData().getLogin())));
            }
            s.setOffice(origin.getOffice());
            s.setFinish(getStart());
            s.setStart(getStart());
            s.setTransferType(transferType);

            s.setPax(getAdults() + getChildren());

            //todo: falta maletas, palos de golf, etc. OJO GRATUIDADES
            //todo: faltan extras?

            s.setPickup(origin);
            s.setDropoff(destination);
            s.setFlightNumber(arrivalFlightNumber);
            s.setFlightOriginOrDestination(arrivalFlightOrigin);
            s.setFlightTime(arrivalFlightTime);


            em.merge(s);
        }

        // el segundo servicio quizás
        if (departureFlightTime != null) {
            TransferService s = null;
            if (getServices().size() > 0) {
                s = (TransferService) getServices().get(0);
            }
            if (s == null) {
                getServices().add(s = new TransferService());
                s.setBooking(this);
                s.setAudit(new Audit(em.find(User.class, MDD.getUserData().getLogin())));
            }
            s.setOffice(destination.getOffice());
            s.setFinish(getEnd());
            s.setStart(getEnd());
            s.setTransferType(transferType);

            s.setPax(getAdults() + getChildren());

            //todo: falta maletas, palos de golf, etc. OJO GRATUIDADES
            //todo: faltan extras?

            s.setPickup(destination);
            s.setDropoff(origin);
            s.setFlightNumber(departureFlightNumber);
            s.setFlightOriginOrDestination(departureFlightDestination);
            s.setFlightTime(departureFlightTime);


            em.merge(s);
        } else {
            if (getServices().size() > 1) {
                getServices().get(1).cancel(em, getAudit().getModifiedBy());
            }
        }
    }

    @Override
    public void priceServices() {

    }
}
