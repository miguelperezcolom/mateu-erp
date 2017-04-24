package io.mateu.erp.model.importing;

import io.mateu.erp.model.authentication.Audit;
import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.product.transfer.TransferType;
import io.mateu.erp.model.util.Constants;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    @ManyToOne
    private Actor customer;

    private String agencyReference;

    private String created;//ojo, de momento estos campos no se estan aplicando en la reserva
    private String modified; //ojo, de momento estos campos no se estan aplicando en la reserva

    public enum SERVICETYPE {SHUTTLE, PRIVATE};//Shuttle, Private, se pueden agregar mas...
    private SERVICETYPE serviceType;
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

    //formato dd/MM/yyyy
    private String arrivalFlightDate;
    public void setArrivalFlightDate(String day)
    {
        arrivalFlightDate=checkDayFormat(day);
    }
    //formato HH:mm
    private String arrivalFlightTime;
    public void setArrivalFlightTime(String time)
    {
        arrivalFlightTime= checkTimeFormat(time);
    }
    private String arrivalFlightNumber;
    private String arrivalFlightCompany;
    private String arrivalOriginAirport;
    private String arrivalComments;
    private String arrivalPickupDate;//formato dd/MM/yyyy
    public void setArrivalPickupDate(String day)
    {
        arrivalPickupDate= checkDayFormat(day);
    }
    private String arrivalPickupTime;//formato HH:mm
    public void setArrivalPickupTime(String time)
    {
        arrivalPickupTime= checkTimeFormat(time);
    }

    private String departureAirport;
    private String departureResort;
    private String departureAddress;
    private String departureFlightDate;//formato dd/MM/yyyy
    public void setDepartureFlightDate(String day)
    {
        departureFlightDate=checkDayFormat(day);
    }
    private String departureFlightTime;//formato HH:mm
    public void setDepartureFlightTime(String time)
    {
        departureFlightTime= checkTimeFormat(time);
    }

    private String departureFlightNumber;
    private String departureFlightCompany;
    private String departureDestinationAirport;
    private String departureComments;
    private String departurePickupDate; //formato dd/MM/yyyy
    public void setDeparturePickupDate(String day)
    {
        departurePickupDate= checkDayFormat(day);
    }
    private String departurePickupTime;//formato HH:mm
    public void setDeparturePickupTime(String time)
    {
        departurePickupTime= checkTimeFormat(time);
    }

    @ManyToOne
    private Booking booking;


    //formato dd/MM/yyyy
    private String checkDayFormat(String day) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate d = LocalDate.parse(day.trim(), df);
        return df.format(d);
        //LocalDateTime.parse(s, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
    //formato HH:mm
    private String checkTimeFormat(String time) {
        DateTimeFormatter dh = DateTimeFormatter.ofPattern("HH:mm");
         try {
            return dh.format(LocalDateTime.parse("01/01/2015 " +time, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        } catch (Exception e1) {
            try {
                return dh.format(LocalDateTime.parse("01/01/2015 " + time, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            } catch (Exception e2) {
                return dh.format(LocalDateTime.parse("01/01/2015 " + time, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm a")));
            }
        }
    }

    private String validate()
    {
        String err="";
        if (agencyReference==null || agencyReference.isEmpty()) err += "Missing agencyReference\n";
        if (customer==null ) err += "Missing customer\n";
        if (serviceType==null) err += "Missing serviceType\n";
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

            result = "";

            //Si ok, actualizamos la reserva...
            Booking b = Booking.getByAgencyRef(em, agencyReference, customer);//buscamos la reserva
            User u = em.find(User.class, Constants.IMPORTING_USER_LOGIN);
            if (b==null)//Crear reserva nueva
            {
                b = new Booking();
                b.setAudit(new Audit(u));
                em.persist(b);

                b.setAgencyReference(agencyReference);
                b.setAgency(customer);
                b.setLeadName(passengerName);
                b.setTelephone(phone);
                b.setEmail(email);
                b.setComments(comments);
               //TODO: b.getBookingRequests.add(this);//Agregar este request en el historial de la reserva
                setBooking(b);

                //ojo, si la reserva es nueva no comprobamos fechas. La reserva se crea siempre
                if ((TRANSFERTYPE.ARRIVAL.equals(transferType) || TRANSFERTYPE.BOTH.equals(transferType)))
                {
                    TransferService s;
                    b.getServices().add(s = new TransferService());
                    s.setAudit(new Audit(u));
                    s.setBooking(b);
                    em.persist(s);
                    fillArrival(s);
                }

                if ((TRANSFERTYPE.DEPARTURE.equals(transferType) || TRANSFERTYPE.BOTH.equals(transferType))) {
                    TransferService s;
                    b.getServices().add(s = new TransferService());
                    s.setAudit(new Audit(u));
                    s.setBooking(b);
                    em.persist(s);
                    fillDeparture(s);
                }
            }
            else //reserva ya existente --> actualizar
            {
                boolean hayCambios=false;

                if (!passengerName.equals(b.getLeadName())) {
                    b.setLeadName(passengerName);
                    hayCambios=true;
                }
                if (phone!=null && !phone.equals(b.getTelephone())) {
                    b.setTelephone(phone);
                    hayCambios=true;
                }
                if (email!=null && !email.equals(b.getEmail()))
                {
                    b.setEmail(email);
                    hayCambios=true;
                }
                if (!b.getComments().contains(comments))
                {
                    b.setComments(b.getComments() + "--" + comments);
                    hayCambios=true;
                }


                if (TRANSFERTYPE.ARRIVAL.equals(transferType) || TRANSFERTYPE.BOTH.equals(transferType)) {
                    TransferService s = getArrival(b);
                    if (s==null) throw new Exception("The booking doesn't have any arrival transfer service");
                    if (changesInArrival(s))  //si hay cambios
                    {
                        //solo modificamos si la fecha es posterior
                        if (getTime(arrivalFlightDate + " " + arrivalFlightTime).isAfter(LocalDateTime.now()))
                        {
                            fillArrival(s);
                            s.getAudit().touch(u);
                            hayCambios=true;
                        }
                        else
                        {
                            result += "Changes in arrival not applied because the arrival date is in the past";
                        }

                    }
                }

                if (TRANSFERTYPE.DEPARTURE.equals(transferType) || TRANSFERTYPE.BOTH.equals(transferType)) {
                    TransferService s = getDeparture(b);
                    if (changesInDeparture(s))
                    {
                        //solo modificamos si la fecha es posterior
                        if (getTime(departureFlightDate + " " + departureFlightTime).isAfter(LocalDateTime.now())) {
                            fillDeparture(s);
                            s.getAudit().touch(u);
                            hayCambios = true;
                        }
                        else {
                            result += "Changes in departure not applied because the arrival date is in the past";
                        }
                    }
                }
                if (hayCambios) {
                    //TODO:    b.getBookingRequests.add(this);//Agregar este request en el historial de la reserva
                    b.getAudit().touch(u);
                    setBooking(b);
                }

            }//fin else


        } catch (Exception ex) {
            result += ex.getMessage();
            ex.printStackTrace();
        }

        return result;
    }


    private TransferService getArrival(Booking b)  {
        if (b.getServices().size()==0) return null;
        for (Service s: b.getServices())
        {
            if (s instanceof TransferService )
            {
                TransferService ts = (TransferService)s;
                if ( arrivalAirport.equals(ts.getPickupText())) return ts;

            }
        }
        return null;
    }

    private void fillArrival(TransferService s) {
        s.setDropoffText("" + arrivalResort + " (" + arrivalAddress + ")");
        s.setFlightNumber("" + arrivalFlightCompany + arrivalFlightNumber);
        s.setFlightOriginOrDestination("" + arrivalOriginAirport);
        s.setFlightTime(getTime(arrivalFlightDate + " " + arrivalFlightTime));
        s.setPax(adults + children + babies);
        s.setAdults(adults);
        s.setChildren(children);
        if (babies>0) s.setComment(babies + " BABIES. ");
        if (extras>0) s.setComment(babies + " EXTRAS. ");

        s.setPickupText(arrivalAirport);
        TransferType tt = TransferType.SHUTTLE;
        if (serviceType.equals(SERVICETYPE.PRIVATE)) {
            tt = TransferType.PRIVATE;
        }
        s.setTransferType(tt);
        s.setOffice(getTask().getOffice());
        s.setPos(getTask().getPointOfSale());
        s.setComment(s.getComment() + "" + vehicle + ", " + serviceType);
        if (getArrivalComments()!=null && !getArrivalComments().isEmpty()) s.setComment(s.getComment() + ". " +getArrivalComments());
    }

    private boolean changesInArrival(TransferService s) {
        String txt = "" + arrivalResort + " (" + arrivalAddress + ")";
        if (!s.getDropoffText().equals(txt)) return true;

        txt = "" + arrivalFlightCompany + arrivalFlightNumber;
        if (!s.getFlightNumber().equals(txt)) return true;

        txt = "" + arrivalOriginAirport;
        if (!s.getFlightOriginOrDestination().equals(txt)) return true;

        LocalDateTime t = getTime(arrivalFlightDate + " " + arrivalFlightTime);
        if (!s.getFlightTime().equals(t)) return true;

        int p = adults + children + babies;
        if (s.getPax()!=p) return true;

        //if (!s.getPickupText().equals(arrivalAirport); esto no lo comprobamos porque es la condicion para encontrar la entrada

        TransferType tt = TransferType.SHUTTLE;
        if (serviceType.equals(SERVICETYPE.PRIVATE)) {
            tt = TransferType.PRIVATE;
        }
        if (!s.getTransferType().equals(tt)) return true;

        if (!s.getOffice().equals(getTask().getOffice())) return true;
        if (!s.getPos().equals(getTask().getPointOfSale())) return true;

        txt = "";
        if (babies>0) txt += (babies + " BABIES. ");
        if (extras>0) txt += (babies + " EXTRAS. ");
        txt += "" + vehicle + ", " + serviceType;
        if (getArrivalComments()!=null && !getArrivalComments().isEmpty())
            txt += ". " +getArrivalComments();
        if (!s.getComment().contains(txt)) return true;


        return false;
    }

    private TransferService getDeparture(Booking b)  {
        if (b.getServices().size()==0) return null;
        for (Service s: b.getServices())
        {
            if (s instanceof TransferService )
            {
                TransferService ts = (TransferService)s;
                if ( departureAirport.equals(ts.getDropoffText())) return ts;

            }
        }
        return null;
    }

    private boolean changesInDeparture(TransferService s) {
        String txt = "" + departureResort + " (" + departureAddress + ")";
        if (!txt.equals(s.getPickupText())) return true;
        txt = "" + departureFlightCompany + departureFlightNumber;
        if (!txt.equals(s.getFlightNumber())) return true;
        txt = "" + departureDestinationAirport;
        if (!txt.equals(s.getFlightOriginOrDestination())) return true;

        LocalDateTime t = getTime(departureFlightDate + " " + departureFlightTime);
        if (!t.equals(s.getFlightTime())) return true;

        int p = adults + children + babies;
        if (p!=s.getPax()) return true;

       // s.setDropoffText(departureAirport);
        TransferType tt = TransferType.SHUTTLE;
        if (serviceType.equals(SERVICETYPE.PRIVATE)) {
            tt = TransferType.PRIVATE;
        }
        if (!tt.equals(s.getTransferType()))return true;

        if (!s.getOffice().equals(getTask().getOffice())) return true;
        if (!s.getPos().equals(getTask().getPointOfSale())) return true;

        txt = "";
        if (babies>0) txt += (babies + " BABIES. ");
        if (extras>0) txt += (babies + " EXTRAS. ");
        txt += "" + vehicle + ", " + serviceType;
        if (getDepartureComments()!=null && !getDepartureComments().isEmpty())
            txt += ". " +getDepartureComments();
        if (!s.getComment().contains(txt)) return true;


        return false;
    }

    private void fillDeparture(TransferService s) {
        s.setPickupText("" + departureResort + " (" + departureAddress + ")");
        s.setFlightNumber("" + departureFlightCompany + departureFlightNumber);
        s.setFlightOriginOrDestination("" + departureDestinationAirport);
        s.setFlightTime(getTime(departureFlightDate + " " + departureFlightTime));
        s.setPax(adults + children + babies);
        s.setAdults(adults);
        s.setChildren(children);
        if (babies>0) s.setComment(babies + " BABIES. ");
        if (extras>0) s.setComment(babies + " EXTRAS. ");

        s.setDropoffText(departureAirport);
        TransferType tt = TransferType.SHUTTLE;
        if (serviceType.equals(SERVICETYPE.PRIVATE)) {
            tt = TransferType.PRIVATE;
        }
        s.setTransferType(tt);
        s.setOffice(getTask().getOffice());
        s.setPos(getTask().getPointOfSale());
        s.setComment(s.getComment() + "" + vehicle + ", " + serviceType);
        if (getDepartureComments()!=null && !getDepartureComments().isEmpty())
            s.setComment(s.getComment() + ". " +getDepartureComments());
    }



    private LocalDateTime getTime(String s)  {
        return LocalDateTime.parse(s, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
       /* try {
            return LocalDateTime.parse(s, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        } catch (Exception e1) {
            try {
                return LocalDateTime.parse(s, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            } catch (Exception e2) {
                return LocalDateTime.parse(s, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm a"));
            }
        }*/
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
