package io.mateu.erp.model.importing;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.financials.Actor;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by Antonia on 26/03/2017.
 */

@Entity
@Getter
@Setter
public class TransferBookingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private TransferImportTask task;
    private String agencyReference;

    @ManyToOne
    private Actor customer;

    private String created;
    private String modified;
    private String serviceType;//Shuttle, Private, etc
    private String vehicle; //Si es un privado (taxi, minibus, etc)

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

                    if (b==null)//Crear reserva nueva
                    {

                    }
                    else //reserva ya existente --> actualizar
                    {

                    }


        } catch (Exception ex) {
            result = ex.getMessage();
            ex.printStackTrace();
        }
        return result;

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
