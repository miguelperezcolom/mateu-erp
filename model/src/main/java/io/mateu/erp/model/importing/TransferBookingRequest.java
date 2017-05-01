package io.mateu.erp.model.importing;

import io.mateu.erp.model.authentication.Audit;
import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.product.transfer.TransferType;
import io.mateu.erp.model.util.Constants;
import io.mateu.ui.mdd.server.annotations.ListColumn;
import io.mateu.ui.mdd.server.annotations.SearchFilter;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Antonia on 26/03/2017.
 */

@Entity
@Table(indexes = {
        @Index(name = "i_tbr_customer_and_reference", columnList = "customer,agencyReference")
})
@Getter
@Setter
public class TransferBookingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @SearchFilter(value="Task", field = "id")
    @ListColumn(value="Task", field = "id")
    private TransferImportTask task;
    @SearchFilter
    @ListColumn
    private String agencyReference;

    @ManyToOne
    @SearchFilter
    @ListColumn
    private Actor customer;

    @ListColumn
    private String created;
    @ListColumn
    private String modified;
    private String serviceType;//Shuttle, Private, etc
    private String vehicle; //Si es un privado (taxi, minibus, etc)

    @SearchFilter
    @ListColumn
    private String passengerName;
    private String phone;
    private String email;
    private int adults=0;
    private int children=0;
    private int babies=0;
    private int extras=0;
    private String comments;

    public enum TRANSFERTYPE {ARRIVAL, DEPARTURE, BOTH};
    private TRANSFERTYPE transferType;

    @ListColumn
    private String status;
    private String typeAtSource;

    private String arrivalAirport;
    private String arrivalResort;
    private String arrivalAddress;
    private String arrivalFlightDate;
    private String arrivalFlightTime;
    private String arrivalFlightNumber;
    private String arrivalFlightCompany;
    private String arrivalOriginAirport;
    private String arrivalComments;
    private String arrivalPickupDate;
    private String arrivalPickupTime;

    private String departureAirport;
    private String departureResort;
    private String departureAddress;
    private String departureFlightDate;
    private String departureFlightTime;
    private String departureFlightNumber;
    private String departureFlightCompany;
    private String departureDestinationAirport;
    private String departureComments;
    private String departurePickupDate;
    private String departurePickupTime;

    @ManyToOne
    @SearchFilter
    private Booking booking;

    @ListColumn
    private String source;

    public String validate()
    {
        String err="";
        if (agencyReference==null || agencyReference.isEmpty()) err += "Missing agencyReference\n";
        if (customer==null ) err += "Missing customer\n";
        if (serviceType==null || serviceType.isEmpty()) err += "Missing serviceType\n";
        if (vehicle==null || vehicle.isEmpty()) err += "Missing vehicle\n";
        if (passengerName==null || passengerName.isEmpty()) err += "Missing passengerName\n";
        if (adults+children<=0) err += "Missing number of paxes\n";
        if (transferType==null) err += "Missing transferType\n";

        if (transferType!=null && (transferType.equals(TRANSFERTYPE.ARRIVAL) || transferType.equals(TRANSFERTYPE.BOTH)))
        {
            if (arrivalAirport==null || arrivalAirport.isEmpty()) err += "Missing arrivalAirport\n";
            if (arrivalResort==null || arrivalResort.isEmpty()) err += "Missing arrivalResort\n";
            if (arrivalAddress==null || arrivalAddress.isEmpty()) err += "Missing arrivalAddress\n";
            if (arrivalFlightDate==null || arrivalFlightDate.isEmpty()) err += "Missing arrivalFlightDate\n";
            if (arrivalFlightTime==null || arrivalFlightTime.isEmpty()) err += "Missing arrivalFlightTime\n";
        }
        if (transferType!=null && (transferType.equals(TRANSFERTYPE.DEPARTURE) || transferType.equals(TRANSFERTYPE.BOTH)))
        {
            if (departureAirport==null || departureAirport.isEmpty()) err += "Missing departureAirport\n";
            if (departureResort==null || departureResort.isEmpty()) err += "Missing departureResort\n";
            if (departureAddress==null ||departureAddress.isEmpty()) err += "Missing departureAddress\n";
            if (departureFlightDate==null || departureFlightDate.isEmpty()) err += "Missing departureFlightDate\n";
            if (departureFlightTime==null || departureFlightTime.isEmpty()) err += "Missing departureFlightTime\n";
        }

        return err;
    }

    public String updateBooking(EntityManager em)
    {
        String result="";
        try {
            //Validamos y si no va bien salimos devolviendo el error
            result = validate();
            if (result.length() > 0) return result;

            //Si ok, actualizamos la reserva...
                    //Buscamos la reserva
                    Booking b = Booking.getByAgencyRef(em, agencyReference, customer);

            //TODO: comprobar que elegimos bien el servicio de llegada y el de salida, para el caso de que hubiésemos creado la reserva a mano

                    if (b==null)//Crear reserva nueva
                    {

                        b = new Booking();
                        b.setAudit(new Audit(em.find(User.class, Constants.IMPORTING_USER_LOGIN)));
                        em.persist(b);


                        if (TRANSFERTYPE.ARRIVAL.equals(transferType) || TRANSFERTYPE.BOTH.equals(transferType)) {
                            TransferService s;
                            b.getServices().add(s = new TransferService());
                            s.setAudit(new Audit(em.find(User.class, Constants.IMPORTING_USER_LOGIN)));
                            s.setBooking(b);
                            em.persist(s);
                        }


                        if (TRANSFERTYPE.DEPARTURE.equals(transferType) || TRANSFERTYPE.BOTH.equals(transferType)) {
                            TransferService s;
                            b.getServices().add(s = new TransferService());
                            s.setAudit(new Audit(em.find(User.class, Constants.IMPORTING_USER_LOGIN)));
                            s.setBooking(b);
                            em.persist(s);
                        }
                    }
                    else //reserva ya existente --> actualizar
                    {

                    }


            setBooking(b);

            b.setAgencyReference(agencyReference);
            b.setAgency(customer);
            b.setEmail(email);
            b.setLeadName(passengerName);
            b.setTelephone(phone);

            int posServei = 0;
            if (TRANSFERTYPE.ARRIVAL.equals(transferType) || TRANSFERTYPE.BOTH.equals(transferType)) {
                TransferService s = (TransferService) b.getServices().get(posServei++);
                s.setDropoffText("" + arrivalResort + " (" + arrivalAddress + ")");
                s.setFlightNumber("" + arrivalFlightCompany + arrivalFlightNumber);
                s.setFlightOriginOrDestination("" + arrivalOriginAirport);
                s.setFlightTime(getTime(arrivalFlightDate + " " + arrivalFlightTime));
                s.setPax(adults + children + babies);
                s.setPickupText(arrivalAirport);
                TransferType tt = TransferType.SHUTTLE;
                switch (serviceType.toLowerCase()) {
                    case "private":
                        tt = TransferType.PRIVATE;
                        break;
                }
                if ("executive".equalsIgnoreCase(vehicle)) tt = TransferType.EXECUTIVE;
                s.setTransferType(tt);
                s.setOffice(getTask().getOffice());
                s.setPos(getTask().getPointOfSale());
                s.setComment("" + vehicle + ", " + serviceType);

                s.setCancelled("Cancellation".equalsIgnoreCase(getStatus()));

                try {
                    s.afterSet(em, false);
                    s.price(em);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }


            if (TRANSFERTYPE.DEPARTURE.equals(transferType) || TRANSFERTYPE.BOTH.equals(transferType)) {
                TransferService s = (TransferService) b.getServices().get(posServei++);
                s.setPickupText("" + departureResort + " (" + departureAddress + ")");
                s.setFlightNumber("" + departureFlightCompany + departureFlightNumber);
                s.setFlightOriginOrDestination("" + departureDestinationAirport);
                s.setFlightTime(getTime(departureFlightDate + " " + departureFlightTime));
                s.setPax(adults + children + babies);
                s.setDropoffText(departureAirport);
                TransferType tt = TransferType.SHUTTLE;
                switch (serviceType.toLowerCase()) {
                    case "private":
                        tt = TransferType.PRIVATE;
                        break;
                }
                if ("executive".equalsIgnoreCase(vehicle)) tt = TransferType.EXECUTIVE;
                s.setTransferType(tt);
                s.setOffice(getTask().getOffice());
                s.setPos(getTask().getPointOfSale());
                s.setComment("" + vehicle + ", " + serviceType);

                s.setCancelled("Cancellation".equalsIgnoreCase(getStatus()));

                try {
                    s.afterSet(em, false);
                    s.price(em);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }




        } catch (Exception ex) {
            result = ex.getMessage();
            ex.printStackTrace();
        }
        return result;

    }

    private LocalDateTime getTime(String s) throws  Exception {
        try {
            return LocalDateTime.parse(s, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        } catch (Exception e1) {
            try {
                return LocalDateTime.parse(s, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            } catch (Exception e2) {
                return LocalDateTime.parse(s, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm a"));
            }
        }
    }

    public String toXml()
    {
        return "";
    }

    public static TransferBookingRequest fromXml(String xml)
    {
        return new TransferBookingRequest();
    }
}
