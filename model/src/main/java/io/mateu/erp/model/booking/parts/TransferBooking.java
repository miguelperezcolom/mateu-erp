package io.mateu.erp.model.booking.parts;

import com.google.common.base.Strings;
import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.booking.transfer.TransferDirection;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.importing.TransferBookingRequest;
import io.mateu.erp.model.invoicing.BookingCharge;
import io.mateu.erp.model.invoicing.ChargeType;
import io.mateu.erp.model.performance.Accessor;
import io.mateu.erp.model.product.ContractType;
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

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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


    @Position(28)
    private LocalDateTime overridePickupTime;


    @ManyToOne@Output
    private Vehicle priceForVehicle;

    @ManyToOne@Output
    private Price priceFromPriceLine;

    @ManyToOne
    @Output
    private TransferBookingRequest transferBookingRequest;

    public TransferBooking() {
        setIcons(FontAwesome.BUS.getHtml());
    }


    @Override
    protected void completeChangeSignatureData(Map<String, String> data) {

        data.put("Big luggages", "" + bigLuggages);
        data.put("Golfs", "" + golf);
        data.put("Bikes", "" + bikes);
        data.put("Wheel chairs", "" + wheelChairs);
        if (origin != null) data.put("Origin", origin.getName());
        if (originAddress != null) data.put("Origin address", originAddress);
        if (destination != null) data.put("Destination", destination.getName());
        if (destinationAddress != null) data.put("Destination address", destinationAddress);
        if (transferType != null) data.put("Transfer type", transferType.name());

        if (arrivalFlightTime != null) data.put("Arrival flight time", "" + arrivalFlightTime);
        if (arrivalFlightNumber != null) data.put("Arrival flight number", arrivalFlightNumber);
        if (arrivalFlightOrigin != null) data.put("Arrival flight origin", arrivalFlightOrigin);


        if (departureFlightTime != null) data.put("Departure flight time", "" + departureFlightTime);
        if (departureFlightNumber != null) data.put("Departure flight number", departureFlightNumber);
        if (departureFlightDestination != null) data.put("Departure flight destination", departureFlightDestination);

    }

    @Override
    public void validate() throws Exception {

    }

    @Override
    public void generateServices(EntityManager em) {

        List<Service> usedServices = new ArrayList<>();
        List<Service> availableServices = new ArrayList<>(getServices());

        //el primer servicio siempre
        if (arrivalFlightTime != null) {
            TransferService s = null;
            if (availableServices.size() > 0) s = (TransferService) availableServices.remove(0);
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

            s.setActive(isActive());

            usedServices.add(s);
        }

        // el segundo servicio quizás
        if (departureFlightTime != null) {
            TransferService s = null;
            if (availableServices.size() > 0) s = (TransferService) availableServices.remove(0);
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

            if (overridePickupTime != null) s.setPickupTime(overridePickupTime);

            s.setActive(isActive());

            usedServices.add(s);
        }

        getServices().forEach(s -> {
            if (!usedServices.contains(s)) s.cancel(em, getAudit().getModifiedBy());
        });

    }

    @Override
    public void priceServices(EntityManager em) {

        setTotalValue(0);

        boolean sale = true;

        // seleccionamos los contratos válidos
        List<Contract> contracts = new ArrayList<>();
        for (Contract c : Accessor.get(em).getTransferContracts()) {
            boolean ok = true;
            ok &= (sale && ContractType.SALE.equals(c.getType())) || (!sale     && ContractType.PURCHASE.equals(c.getType()));
            ok &= c.getAgencies().size() == 0 || c.getAgencies().contains(getAgency());
            ok &= getProvider() == null || getProvider().equals(c.getSupplier());
            ok &= c.getValidFrom().isBefore(getStart()) || c.getValidFrom().equals(getStart());
            ok &= c.getValidTo().isAfter(getEnd()) || c.getValidTo().equals(getEnd());
            LocalDate created = (getAudit() != null && getAudit().getCreated() != null)?getAudit().getCreated().toLocalDate():LocalDate.now();
            ok &= c.getBookingWindowFrom() == null || c.getBookingWindowFrom().isBefore(created) || c.getBookingWindowFrom().equals(created);
            ok &= c.getBookingWindowTo() == null || c.getBookingWindowTo().isAfter(created) || c.getBookingWindowTo().equals(created);
            if (ok) contracts.add(c);
        }

        List<Contract> propietaryContracts = contracts.stream().filter((c) -> c.getAgencies().size() > 0).collect(Collectors.toList());

        if (propietaryContracts.size() > 0) contracts = propietaryContracts;

        for (Contract c : contracts) {

            boolean contratoOk = true;

            contratoOk = contratoOk && !c.getValidFrom().isAfter(getStart());
            contratoOk = contratoOk && !c.getValidTo().isBefore(getEnd());

            //todo: comprobar file window y demás condiciones

            if (contratoOk) {

                for (Price p : c.getPrices()) {

                    boolean precioOk = p.getOrigin().getPoints().contains(getOrigin()) || p.getOrigin().getResorts().contains(getOrigin().getResort());

                    precioOk = precioOk && (p.getDestination().getPoints().contains(getDestination()) || p.getDestination().getResorts().contains(getDestination().getResort()));

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

            BookingCharge c;
            getServiceCharges().add(c = new BookingCharge());
            c.setAudit(new Audit(MDD.getCurrentUser()));
            c.setTotal(getTotalValue());
            c.setCurrency(getCurrency());

            c.setText(((getArrivalFlightTime() != null && getDepartureFlightTime() != null)?"RW":"OW") + " " + getTransferType().name() + " transfer from " + getOrigin().getName() + " to " + getDestination().getName() + (getPriceForVehicle() != null?" in " + getPriceForVehicle().getName():"") + " for " + (getAdults() + getChildren()) + " pax");

            c.setAgency(getAgency());

            c.setType(ChargeType.SALE);
            c.setBooking(this);

            c.setInvoice(null);

        c.setBillingConcept(getContract() != null?getContract().getBillingConcept():AppConfig.get(em).getBillingConceptForTransfer());
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


    @Override
    protected void completeSignature(Map<String, Object> m) {
        if (getTransferType() != null) m.put("transferType", getTransferType().name());
        m.put("pickup", getOrigin().getName());
        m.put("pickupResort", getOrigin().getResort().getName());
        if (getOverridePickupTime() != null) m.put("pickupTime", getOverridePickupTime().format(DateTimeFormatter.ISO_DATE_TIME));
        m.put("dropoff", getDestination().getName());
        m.put("dropoffResort", getDestination().getResort().getName());
        if (getDepartureFlightTime() != null) {
            m.put("departureFlight", getDepartureFlightNumber());
            m.put("departureFlightDate", getDepartureFlightTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            m.put("departureFlightTime", getDepartureFlightTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            m.put("departureFlightOriginOrDestination", getDepartureFlightDestination());
        }
        if (getArrivalFlightTime() != null) {
            m.put("arrivalFlight", getArrivalFlightNumber());
            m.put("arrivalFlightDate", getArrivalFlightTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            m.put("arrivalFlightTime", getArrivalFlightTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            m.put("arrivalFlightOriginOrDestination", getArrivalFlightOrigin());
        }
        m.put("agencyReference", getAgencyReference());
        m.put("comments", getSpecialRequests());
        m.put("direction", getArrivalFlightTime() != null?"INBOUND":"OUTBOUND");
    }


    @Override
    public Map<String, Object> getData(EntityManager em) throws Throwable {
        Map<String, Object> d = super.getData(em);

       completeSignature(d);

        return d;
    }

    @Override
    public String getServiceDataHtml() {
        String h = "<pre>";

        h += "" + getTransferType().name() + " TRANSFER BOOKING \n";

        h += "Origin: " + getOrigin().getName() + "  - " + getOrigin().getResort().getName() + " \n";
        h += "Destination: " + getDestination().getName() + " - " + getDestination().getName() + " \n";
        if (getArrivalFlightTime() != null) h += "Arrival: " + getArrivalFlightTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "  - " + getArrivalFlightNumber() + " - " + getArrivalFlightOrigin() + " \n";
        if (getDepartureFlightTime() != null) h += "Departure: " + getDepartureFlightTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "  - " + getDepartureFlightNumber() + " - " + getDepartureFlightDestination() + " \n";

        h += "Adults: " + getAdults() + " \n";
        h += "Children: " + getChildren() + " \n";
        h += "Bikes: " + getBikes() + " \n";
        h += "Golf: " + getGolf() + " \n";
        h += "Wheel chairs: " + getWheelChairs() + " \n";
        h += "Big luggages: " + getBigLuggages() + " \n";

        if (!Strings.isNullOrEmpty(getSpecialRequests())) h += "Special requests: " + getSpecialRequests() + "\n";

        if (getArrivalFlightTime() != null) h += "Arrival instructions:\n" + (Strings.isNullOrEmpty(getOrigin().getInstructions())?"--":getOrigin().getInstructions()) + "\n";
        if (getDepartureFlightTime() != null) h += "Departure instructions:\n" + (Strings.isNullOrEmpty(getDestination().getInstructions())?"--":getDestination().getInstructions()) + "\n";


        h += "</pre>";
        return h;
    }
}
