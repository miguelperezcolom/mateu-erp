package io.mateu.erp.model.booking.parts;

import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.financials.Amount;
import io.mateu.erp.model.importing.TransferBookingRequest;
import io.mateu.erp.model.invoicing.BookingCharge;
import io.mateu.erp.model.invoicing.ChargeType;
import io.mateu.erp.model.performance.Accessor;
import io.mateu.erp.model.product.transfer.*;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.Output;
import io.mateu.mdd.core.annotations.Position;
import io.mateu.mdd.core.annotations.SameLine;
import io.mateu.mdd.core.annotations.TextArea;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;
import org.easytravelapi.common.BestDeal;
import org.easytravelapi.transfer.AvailableTransfer;
import org.javamoney.moneta.FastMoney;

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

    @SameLine
    @Position(16)
    private int wheelChairs;

    @ManyToOne@NotNull
    @Position(17)
    private TransferPoint origin;

    @TextArea
    @Position(18)
    private String originAddress;

    @ManyToOne@NotNull
    @Position(19)
    private TransferPoint destination;

    @TextArea
    @Position(20)
    private String destinationAddress;


    @NotNull
    @Position(21)
    private TransferType transferType;


    @Position(22)
    private LocalDateTime arrivalFlightTime;

    public void setArrivalFlightTime(LocalDateTime arrivalFlightTime) {
        this.arrivalFlightTime = arrivalFlightTime;
        if (arrivalFlightTime != null) {
            setStart(arrivalFlightTime.toLocalDate());
            if (getEnd() == null) setEnd(arrivalFlightTime.toLocalDate());
        }
    }

    @Position(23)
    private String arrivalFlightNumber;


    @Position(24)
    private String arrivalFlightOrigin;



    @Position(25)
    private LocalDateTime departureFlightTime;

    public void setDepartureFlightTime(LocalDateTime departureFlightTime) {
        this.departureFlightTime = departureFlightTime;
        if (departureFlightTime != null) {
            setEnd(departureFlightTime.toLocalDate());
            if (getStart() == null) setStart(departureFlightTime.toLocalDate());
        }
    }

    @Position(26)
    private String departureFlightNumber;

    @Position(27)
    private String departureFlightDestination;


    @ManyToOne
    private Contract contract;

    @ManyToOne@Output
    private Vehicle priceForVehicle;

    @ManyToOne@Output
    private Price priceFromPriceLine;

    @ManyToOne
    private TransferBookingRequest transferBookingRequest;

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
                s.setAudit(new Audit(MDD.getCurrentUser()));
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
                s.setAudit(new Audit(MDD.getCurrentUser()));
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
    public void priceServices(EntityManager em) {

        setTotalValue(0);

        for (Contract c : Accessor.get(em).getTransferContracts()) {

            boolean contratoOk = true;

            contratoOk = contratoOk && !c.getValidFrom().isAfter(getStart());
            contratoOk = contratoOk && !c.getValidTo().isBefore(getEnd());

            //todo: comprobar file window y demás condiciones

            if (contratoOk) {

                for (Price p : c.getPrices()) {

                    boolean precioOk = p.getOrigin().getPoints().contains(getOrigin()) || p.getOrigin().getCities().contains(getOrigin().getZone());

                    precioOk = precioOk && (p.getDestination().getPoints().contains(getDestination()) || p.getDestination().getCities().contains(getDestination().getZone()));

                    precioOk = precioOk && p.getTransferType().equals(getTransferType());

                    precioOk = precioOk && p.getVehicle().getMinPax() <= getAdults() + getChildren() && p.getVehicle().getMaxPax() >= getAdults() + getChildren();

                    if (precioOk) {

                        setContract(c);
                        setPriceForVehicle(p.getVehicle());
                        setPriceFromPriceLine(p);

                        double valor = p.getPrice();
                        if (PricePer.PAX.equals(p.getPricePer())) valor = valor * (getAdults() + getChildren());

                        if (getArrivalFlightTime() != null && getDepartureFlightTime() != null) valor *= 2;
                        else if (getArrivalFlightTime() == null && getDepartureFlightTime() == null) valor = 0;

                        setTotalValue(Helper.roundEuros(valor));
                    }

                }

            }

        }

    }

    @Override
    public void createCharges(EntityManager em) throws Throwable {
        getServiceCharges().clear();

        if (getContract() != null) {
            BookingCharge c;
            getServiceCharges().add(c = new BookingCharge());
            c.setAudit(new Audit(MDD.getCurrentUser()));
            c.setTotal(new Amount(FastMoney.of(getTotalValue(), "EUR")));

            c.setText(((getArrivalFlightTime() != null && getDepartureFlightTime() != null)?"RW":"OW") + " " + getTransferType().name() + " transfer from " + getOrigin().getName() + " to " + getDestination().getName() + " in " + getPriceForVehicle().getName() + " for " + (getAdults() + getChildren()) + " pax");

            c.setPartner(getAgency());

            c.setType(ChargeType.SALE);
            c.setBooking(this);

            c.setInvoice(null);

            c.setBillingConcept(getContract().getBillingConcept());
        }
    }





    public void setBigLuggages(int bigLuggages) {
        if (this.bigLuggages != bigLuggages) setUpdatePending(true);
        this.bigLuggages = bigLuggages;
    }

    public void setGolf(int golf) {
        if (this.golf != golf) setUpdatePending(true);
        this.golf = golf;
    }

    public void setBikes(int bikes) {
        if (this.bikes != bikes) setUpdatePending(true);
        this.bikes = bikes;
    }

    public void setWheelChairs(int wheelChairs) {
        if (this.wheelChairs != wheelChairs) setUpdatePending(true);
        this.wheelChairs = wheelChairs;
    }

    public void setOrigin(TransferPoint origin) {
        if (!Helper.equals(this.origin, origin)) setUpdatePending(true);
        this.origin = origin;
    }

    public void setOriginAddress(String originAddress) {
        if (!Helper.equals(this.originAddress, originAddress)) setUpdatePending(true);
        this.originAddress = originAddress;
    }

    public void setDestination(TransferPoint destination) {
        if (!Helper.equals(this.destination, destination)) setUpdatePending(true);
        this.destination = destination;
    }

    public void setDestinationAddress(String destinationAddress) {
        if (!Helper.equals(this.destinationAddress, destinationAddress)) setUpdatePending(true);
        this.destinationAddress = destinationAddress;
    }

    public void setTransferType(TransferType transferType) {
        if (!Helper.equals(this.transferType, transferType)) setUpdatePending(true);
        this.transferType = transferType;
    }

    public void setArrivalFlightNumber(String arrivalFlightNumber) {
        if (!Helper.equals(this.arrivalFlightNumber, arrivalFlightNumber)) setUpdatePending(true);
        this.arrivalFlightNumber = arrivalFlightNumber;
    }

    public void setArrivalFlightOrigin(String arrivalFlightOrigin) {
        if (!Helper.equals(this.arrivalFlightOrigin, arrivalFlightOrigin)) setUpdatePending(true);
        this.arrivalFlightOrigin = arrivalFlightOrigin;
    }

    public void setDepartureFlightNumber(String departureFlightNumber) {
        if (!Helper.equals(this.departureFlightNumber, departureFlightNumber)) setUpdatePending(true);
        this.departureFlightNumber = departureFlightNumber;
    }

    public void setDepartureFlightDestination(String departureFlightDestination) {
        if (!Helper.equals(this.departureFlightDestination, departureFlightDestination)) setUpdatePending(true);
        this.departureFlightDestination = departureFlightDestination;
    }




}
