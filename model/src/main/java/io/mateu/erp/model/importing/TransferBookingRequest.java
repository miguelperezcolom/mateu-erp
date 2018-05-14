package io.mateu.erp.model.importing;

import com.google.common.base.Strings;
import io.mateu.common.model.authentication.Audit;
import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.booking.ServiceConfirmationStatus;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.partners.Actor;
import io.mateu.erp.model.product.transfer.TransferType;
import io.mateu.common.model.util.Constants;
import io.mateu.ui.mdd.server.annotations.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antonia on 26/03/2017.
 */

@Entity
@Getter
@Setter
public class TransferBookingRequest {

    @ListColumn(order = true)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Output
    @ListColumn
    @Column(name = "_when")
    LocalDateTime when = LocalDateTime.now();

    @Output
    @ManyToOne
    private TransferImportTask task;

    @SearchFilter
    @Output
    @ManyToOne
    private Actor customer;

    @SearchFilter
    @Output
    private String agencyReference;

    @Output
    private String created;//ojo, de momento estos campos no se estan aplicando en la reserva
    @Output
    private String modified; //ojo, de momento estos campos no se estan aplicando en la reserva

    private String currency;

    @SameLine
    private double value;

    @Transient
    private double effectiveValue;

    //public enum SERVICETYPE {SHUTTLE, PRIVATE};//Shuttle, Private, se pueden agregar mas...
    @ListColumn
    @Output
    private TransferType serviceType; //Shuttle, Private, Executive
    @ListColumn
    @Output
    private String vehicle; //Si es un privado (taxi, minibus, etc)

    @SearchFilter
    @Output
    private String passengerName;
    @ListColumn
    @Output
    private String phone;
    @ListColumn
    @Output
    private String email;
    @ListColumn
    @Output
    private int adults=0;
    @ListColumn
    @Output
    private int children=0;
    @ListColumn
    @Output
    private int babies=0;
    @ListColumn
    @Output
    private int extras=0;
    @ListColumn
    @Output
    private String comments;

    public enum TRANSFERSERVICES {ARRIVAL, DEPARTURE, BOTH};
    @ListColumn
    @Output
    private TRANSFERSERVICES transferServices;

    public enum STATUS {OK, CANCELLED};


    @Separator("Arrival")
    @ListColumn
    @Output
    private STATUS arrivalStatus;
    @ListColumn
    @Output
    @SameLine
    private String arrivalAirport;
    @ListColumn
    @Output
    private String arrivalResort;
    @ListColumn
    @Output
    @SameLine
    private String arrivalAddress;
    @ListColumn
    @Output
    private boolean arrivalConfirmed=false;

    @Output
    @ListColumn
    @SameLine
    private String arrivalFlightDate;//formato dd/MM/yyyy
    public void setArrivalFlightDate(String day)
    {
        arrivalFlightDate=checkDayFormat(day);
    }

    @ListColumn
    @Output

    @SameLine
    private String arrivalFlightTime;//formato HH:mm
    public void setArrivalFlightTime(String time)
    {
        arrivalFlightTime= checkTimeFormat(time);
    }
    @ListColumn
    @Output

    @SameLine
    private String arrivalFlightNumber;
    @ListColumn
    @Output

    @SameLine
    private String arrivalFlightCompany;
    @ListColumn
    @Output

    @SameLine
    private String arrivalOriginAirport;
    @ListColumn
    @Output
    @SameLine
    private String arrivalComments;
    @ListColumn
    @Output
    @SameLine
    private String arrivalPickupDate;//formato dd/MM/yyyy
    public void setArrivalPickupDate(String day)
    {
        arrivalPickupDate= checkDayFormat(day);
    }
    @ListColumn
    @Output
    @SameLine
    private String arrivalPickupTime;//formato HH:mm
    public void setArrivalPickupTime(String time)
    {
        arrivalPickupTime= checkTimeFormat(time);
    }

    @Separator("Departure")
    @ListColumn
    @Output
    private STATUS departureStatus;
    @ListColumn
    @Output
    @SameLine
    private String departureAirport;
    @ListColumn
    @Output
    private String departureResort;

    public void setDepartureResort(String departureResort) {
        this.departureResort = (departureResort != null)?departureResort.replaceAll("\\n", "_").replaceAll("\\r", "_"):departureResort;
    }

    @ListColumn
    @Output
    @SameLine
    private String departureAddress;

    public void setDepartureAddress(String departureAddress) {
        this.departureAddress = (departureAddress != null)?departureAddress.replaceAll("\\n", "_").replaceAll("\\r", "_"):departureAddress;
    }

    @ListColumn
    @Output
    private boolean departureConfirmed=false;
    @ListColumn
    @Output
    @SameLine
    private String departureFlightDate;//formato dd/MM/yyyy
    public void setDepartureFlightDate(String day)
    {
        departureFlightDate=checkDayFormat(day);
    }
    @ListColumn
    @Output
    @SameLine
    private String departureFlightTime;//formato HH:mm
    public void setDepartureFlightTime(String time)
    {
        departureFlightTime= checkTimeFormat(time);
    }

    @ListColumn
    @Output
    @SameLine
    private String departureFlightNumber;
    @ListColumn
    @Output
    @SameLine
    private String departureFlightCompany;
    @ListColumn
    @Output
    @SameLine
    private String departureDestinationAirport;
    @ListColumn
    @Output
    @SameLine
    private String departureComments;
    @ListColumn
    @Output
    @SameLine
    private String departurePickupDate; //formato dd/MM/yyyy
    public void setDeparturePickupDate(String day)
    {
        departurePickupDate= checkDayFormat(day);
    }
    @ListColumn
    @Output
    @SameLine
    private String departurePickupTime;//formato HH:mm
    public void setDeparturePickupTime(String time)
    {
        departurePickupTime= checkTimeFormat(time);
    }

    @Separator("Others")
    private String source; //xml origen o csv

    @Output
    @ManyToOne
    private Booking booking;

    @Output
    @ListColumn
    private String result;


    //formato dd/MM/yyyy
    private String checkDayFormat(String day) {
        if (Strings.isNullOrEmpty(day)) return null;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate d = LocalDate.parse(day.trim(), df);
        return df.format(d);
        //LocalDateTime.parse(s, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
    //formato HH:mm
    private String checkTimeFormat(String time) {
        if (Strings.isNullOrEmpty(time)) return null;
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
        if (transferServices ==null) err += "Missing transferType\n";

        if (transferServices !=null && (transferServices.equals(TRANSFERSERVICES.ARRIVAL) || transferServices.equals(TRANSFERSERVICES.BOTH)))
        {
            if (arrivalStatus==null ) err += "Missing arrivalStatus\n";
            if (arrivalAirport==null || arrivalAirport.isEmpty()) err += "Missing arrivalAirport\n";
            if (arrivalResort==null || arrivalResort.isEmpty()) err += "Missing arrivalResort\n";
            if (arrivalAddress==null || arrivalAddress.isEmpty()) err += "Missing arrivalAddress\n";
            if (arrivalFlightDate==null || arrivalFlightDate.isEmpty()) err += "Missing arrivalFlightDate\n";
            if (arrivalFlightTime==null || arrivalFlightTime.isEmpty()) err += "Missing arrivalFlightTime\n";
        }
        if (transferServices !=null && (transferServices.equals(TRANSFERSERVICES.DEPARTURE) || transferServices.equals(TRANSFERSERVICES.BOTH)))
        {
            if (departureStatus==null ) err += "Missing departureStatus\n";
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
        String _result="";
        try {

            List<Object> nuevasEntidades = new ArrayList<>();

            //Validamos y si no va bien salimos devolviendo el error
            _result = validate();
            if (_result.length() > 0) {
                this.result = _result;
                return _result;
            }

            _result = "";

            System.out.println("yyyy ref=" + getAgencyReference());

            System.out.println("value=" + value);

            effectiveValue = value;

            System.out.println("effectiveValue=" + effectiveValue);



            //Si ok, actualizamos la reserva...
            Booking b = Booking.getByAgencyRef(em, agencyReference, customer);//buscamos la reserva
            User u = em.find(User.class, Constants.IMPORTING_USER_LOGIN);
            if (b==null)//Crear reserva nueva
            {
                b = new Booking();
                b.setAudit(new Audit(u));
                nuevasEntidades.add(b);

                b.setAgencyReference(agencyReference);
                b.setAgency(customer);
                b.setLeadName(passengerName);
                b.setTelephone(phone);
                b.setEmail(email);
                if (comments!=null) b.setComments(comments);
               //TODO: b.getBookingRequests.add(this);//Agregar este request en el historial de la reserva
                setBooking(b);
                nuevasEntidades.add(this);

                //ojo, si la reserva es nueva no comprobamos fechas ni el estado. La reserva se crea siempre
                if ((TRANSFERSERVICES.ARRIVAL.equals(transferServices) || TRANSFERSERVICES.BOTH.equals(transferServices)))
                {
                    TransferService s;
                    b.getServices().add(s = new TransferService());
                    s.setAudit(new Audit(u));
                    s.setBooking(b);
                    nuevasEntidades.add(s);
                    fillArrival(s, null);
                    this.getTask().increaseAdditions();
                }

                if ((TRANSFERSERVICES.DEPARTURE.equals(transferServices) || TRANSFERSERVICES.BOTH.equals(transferServices))) {
                    TransferService s;
                    b.getServices().add(s = new TransferService());
                    s.setAudit(new Audit(u));
                    s.setBooking(b);
                    nuevasEntidades.add(s);
                    fillDeparture(s, lastRequest);
                    this.getTask().increaseAdditions();
                }
            }
            else //reserva ya existente --> actualizar
            {
                boolean hayCambios=false;

                boolean hayBloqueos = false;

                TransferBookingRequest lastRequest = null;
                for (Service s : b.getServices()) {
                    hayBloqueos |= s.isLocked();
                    if (s instanceof TransferService) {
                        TransferService ts = (TransferService) s;
                        if (ts.getTransferBookingRequest() != null && (lastRequest == null || getWhen().isBefore(lastRequest.getWhen()))) lastRequest = ((TransferService) s).getTransferBookingRequest();
                    }
                }

                if (!hayBloqueos) {

                    if (!passengerName.equals(b.getLeadName()) && (lastRequest == null || !passengerName.equals(lastRequest.getPassengerName()))) {
                        b.setLeadName(passengerName);
                        hayCambios=true;
                    }
                    if (phone!=null && !phone.equals(b.getTelephone()) && (lastRequest == null || !phone.equals(lastRequest.getPhone()))) {
                        b.setTelephone(phone);
                        hayCambios=true;
                    }
                    if (email!=null && !email.equals(b.getEmail()) && (lastRequest == null || !email.equals(lastRequest.getEmail())))
                    {
                        b.setEmail(email);
                        hayCambios=true;
                    }
                    if (comments!=null && !b.getComments().contains(comments) && (lastRequest == null || !comments.equals(lastRequest.getComments())))
                    {
                        b.setComments(b.getComments() + "--" + comments);
                        hayCambios=true;
                    }

                }



                if (TRANSFERSERVICES.ARRIVAL.equals(transferServices) || TRANSFERSERVICES.BOTH.equals(transferServices)) {
                    TransferService s = getArrival(b);
                    if (s==null)
                    {
                        b.getServices().add(s = new TransferService());
                        s.setAudit(new Audit(u));
                        s.setBooking(b);
                        nuevasEntidades.add(s);
                        fillArrival(s, null);
                        hayCambios = true;
                        this.getTask().increaseAdditions();
                    }
                    else if (changesInArrival(s, lastRequest))//si hay cambios
                    {
                        if (s.isLocked())
                        {
                            _result += "Changes in arrival not applied because it is locked";
                            this.getTask().increaseErrors();
                        }
                        else if (getTime(arrivalFlightDate + " " + arrivalFlightTime).isBefore(LocalDateTime.now().plusHours(1))
                                || s.getFlightTime().isBefore(LocalDateTime.now()))
                        {//solo modificamos si la fecha es posterior y si la fecha del servicio no ha pasado
                            _result += "Changes in arrival not applied because the arrival date is in the past";
                            this.getTask().increaseErrors();
                        }
                        else
                        {
                            fillArrival(s, lastRequest);
                            s.getAudit().touch(u);
                            hayCambios=true;
                            if (s.isCancelled()) this.getTask().increaseCancellations();
                            else this.getTask().increaseModifications();
                        }
                    }
                    else {
                        //sin cambios
                        this.getTask().increaseUnmodified();

                        if (effectiveValue != 0) {
                            effectiveValue -= s.getOverridedNetValue();
                        }

                    }

                }

                if (TRANSFERSERVICES.DEPARTURE.equals(transferServices) || TRANSFERSERVICES.BOTH.equals(transferServices)) {
                    TransferService s = getDeparture(b);
                    if (s==null)
                    {
                        b.getServices().add(s = new TransferService());
                        s.setAudit(new Audit(u));
                        s.setBooking(b);
                        nuevasEntidades.add(s);
                        fillDeparture(s, null);
                        hayCambios = true;
                        this.getTask().increaseAdditions();
                    }
                    else if (changesInDeparture(s, lastRequest))
                    {
                        if (s.isLocked())
                        {
                            _result += "Changes in arrival not applied because it is locked";
                            this.getTask().increaseErrors();
                        }//solo modificamos si la fecha es posterior
                        else if (getTime(departureFlightDate + " " + departureFlightTime).isBefore(LocalDateTime.now().plusHours(1))
                                || s.getFlightTime().isBefore(LocalDateTime.now()))
                        {
                            _result += "Changes in departure not applied because the arrival date is in the past";
                            this.getTask().increaseErrors();
                        }
                        else {
                            fillDeparture(s, lastRequest);
                            s.getAudit().touch(u);
                            hayCambios = true;
                            if (s.isCancelled()) this.getTask().increaseCancellations();
                            else this.getTask().increaseModifications();
                        }
                    }
                    else {
                        //sin cambios
                        this.getTask().increaseUnmodified();

                        if (effectiveValue != 0) {
                            effectiveValue -= s.getOverridedNetValue();
                        }

                    }

                }
                if (hayCambios) {
                    //TODO:    b.getBookingRequests.add(this);//Agregar este request en el historial de la reserva
                    b.getAudit().touch(u);
                    setBooking(b);
                    nuevasEntidades.add(this);
                }

            }//fin else

            for (Object o : nuevasEntidades) em.persist(o);

        } catch (Throwable ex) {
            _result += ex.getMessage();

            ConstraintViolationException cve = (ConstraintViolationException) ex.getCause();
            if (ex instanceof ConstraintViolationException) {
                cve = (ConstraintViolationException) ex;
            } else if (ex.getCause() != null && ex.getCause() instanceof ConstraintViolationException) {
                cve = (ConstraintViolationException) ex.getCause();
            }

            if (cve != null) {
                StringBuffer sb = new StringBuffer();
                for (ConstraintViolation v : cve.getConstraintViolations()) {
                    if (sb.length() > 0) sb.append("\n");
                    sb.append(v.toString());
                }
                System.out.println(sb.toString());
                ex = new Exception(sb.toString());
            }

            ex.printStackTrace();
            this.getTask().increaseErrors();
        }

        if (_result.isEmpty())
            this.result = "Ok";
        else
            this.result = _result;

        return _result;
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

    private void fillArrival(TransferService s, TransferBookingRequest lastRequest) {
        if (lastRequest == null || !arrivalStatus.equals(lastRequest.getArrivalStatus())) s.setCancelled(arrivalStatus.equals(STATUS.CANCELLED));

        if (lastRequest == null || !arrivalPickupDate.equals(lastRequest.getArrivalPickupDate()) || !arrivalPickupTime.equals(lastRequest.getArrivalPickupTime())) {
            if (arrivalPickupDate!=null && arrivalPickupTime!=null)
                s.setImportedPickupTime(getTime(arrivalPickupDate + " " + arrivalPickupTime));
            else
                s.setImportedPickupTime(null);
        }

        if (lastRequest == null || arrivalConfirmed != lastRequest.isArrivalConfirmed()) {
            ServiceConfirmationStatus a = ServiceConfirmationStatus.PENDING;
            if (arrivalConfirmed) a = ServiceConfirmationStatus.CONFIRMED;
            s.setAnswer(a);
        }

        if (lastRequest == null || !arrivalResort.equals(lastRequest.getArrivalResort()) || !arrivalAddress.equals(lastRequest.getArrivalAddress())) s.setDropoffText("" + arrivalResort + " (" + arrivalAddress + ")");
        if (lastRequest == null || !arrivalFlightCompany.equals(lastRequest.getArrivalFlightCompany()) || !arrivalFlightNumber.equals(lastRequest.getArrivalFlightNumber())) s.setFlightNumber("" + arrivalFlightCompany + arrivalFlightNumber);
        if (lastRequest == null || !arrivalOriginAirport.equals(lastRequest.getArrivalOriginAirport())) s.setFlightOriginOrDestination("" + arrivalOriginAirport);
        if (lastRequest == null || !arrivalFlightDate.equals(lastRequest.getArrivalFlightDate()) || !arrivalFlightTime.equals(lastRequest.getArrivalFlightTime())) s.setFlightTime(getTime(arrivalFlightDate + " " + arrivalFlightTime));
        if (lastRequest == null || (adults + children + babies) != (lastRequest.getAdults() + lastRequest.getChildren() + lastRequest.getBabies())) s.setPax(adults + children + babies);
       // s.setAdults(adults);
        //s.setChildren(children);


        if (lastRequest == null || !arrivalAirport.equals(lastRequest.getArrivalAirport())) s.setPickupText(arrivalAirport);

        if (lastRequest == null || !serviceType.equals(lastRequest.getServiceType())) s.setTransferType(serviceType);

        s.setOffice(getTask().getOffice());
        s.setPos(getTask().getPointOfSale());

        if (s.getComment()==null) s.setComment("");
        String comm = vehicle + ". ";
        if (getArrivalComments()!=null && !getArrivalComments().isEmpty())
            comm +=  getArrivalComments() + ". ";
        if (children>0) comm += (children + " CHILDREN. ");
        if (babies>0) comm += (babies + " BABIES. ");
        if (extras>0) comm += (extras + " EXTRAS. ");
        String comm0 = "";
        if (lastRequest != null) {
            comm0 = lastRequest.getVehicle() + ". ";
            if (lastRequest.getArrivalComments()!=null && !lastRequest.getArrivalComments().isEmpty())
                comm0 +=  lastRequest.getArrivalComments() + ". ";
            if (lastRequest.getChildren()>0) comm0 += (lastRequest.getChildren() + " CHILDREN. ");
            if (lastRequest.getBabies()>0) comm0 += (lastRequest.getBabies() + " BABIES. ");
            if (lastRequest.getExtras()>0) comm0 += (lastRequest.getExtras() + " EXTRAS. ");
        }
        if (!s.getComment().contains(comm) && (lastRequest == null || !comm.equals(comm0)))
            s.setComment(comm + "\n" + s.getComment());


        if (getValue() != 0 && getValue() != lastRequest.getValue()) {
            s.setOverridedNetValue(effectiveValue);
            s.setValueOverrided(true);
            effectiveValue = 0;
        }

    }

    private boolean changesInArrival(TransferService s, TransferBookingRequest lastRequest) {
       if (s.isCancelled()!= (arrivalStatus.equals(STATUS.CANCELLED))  && (lastRequest == null || !arrivalStatus.equals(lastRequest.getArrivalStatus()))) return true;

        ServiceConfirmationStatus a = ServiceConfirmationStatus.PENDING;
        if (arrivalConfirmed) a = ServiceConfirmationStatus.CONFIRMED;
        if (!a.equals(s.getAnswer())  && (lastRequest == null || arrivalConfirmed != lastRequest.isArrivalConfirmed())) return true;

        String txt = "" + arrivalResort + " (" + arrivalAddress + ")";
        if (!txt.equals(s.getDropoffText())  && (lastRequest == null || !txt.equals("" + lastRequest.getArrivalResort() + " (" + lastRequest.getArrivalAddress() + ")"))) return true;

        txt = "" + arrivalFlightCompany + arrivalFlightNumber;
        if (!s.getFlightNumber().equals(txt) && (lastRequest == null || !txt.equals("" + lastRequest.getArrivalFlightCompany() + lastRequest.getArrivalFlightNumber()))) return true;

        txt = "" + arrivalOriginAirport;
        if (!s.getFlightOriginOrDestination().equals(txt) && (lastRequest == null || !txt.equals("" + lastRequest.getArrivalOriginAirport()))) return true;

        LocalDateTime t = getTime(arrivalFlightDate + " " + arrivalFlightTime);
        if (!s.getFlightTime().equals(t) && (lastRequest == null || !t.equals(getTime(lastRequest.getArrivalFlightDate() + " " + lastRequest.getArrivalFlightTime())))) return true;

        if (arrivalPickupDate!=null && arrivalPickupTime!=null) {
            t = getTime(arrivalPickupDate + " " + arrivalPickupTime);
            if (s.getImportedPickupTime() != null && !s.getImportedPickupTime().equals(t) && (lastRequest == null || !t.equals(getTime(lastRequest.getArrivalPickupDate() + " " + lastRequest.getArrivalPickupTime())))) return true;
        }
        else if (s.getImportedPickupTime()!=null && (lastRequest == null || lastRequest.getArrivalPickupTime() != null)) return true;

        int p = adults + children + babies;
        if (s.getPax()!=p&& (lastRequest == null || p != (lastRequest.getAdults() + lastRequest.getChildren() + lastRequest.getBabies()))) return true;

        //if (!s.getPickupText().equals(arrivalAirport); esto no lo comprobamos porque es la condicion para encontrar la entrada

        if (!s.getTransferType().equals(serviceType) && (lastRequest == null || !serviceType.equals(lastRequest.getServiceType()))) return true;

        if (!s.getOffice().equals(getTask().getOffice())) return true;
        if (!s.getPos().equals(getTask().getPointOfSale())) return true;


        if (s.getComment()==null) s.setComment("");
        String comm = vehicle + ". ";
        if (getArrivalComments()!=null && !getArrivalComments().isEmpty())
            comm +=  getArrivalComments() + ". ";
        if (children>0) comm += (children + " CHILDREN. ");
        if (babies>0) comm += (babies + " BABIES. ");
        if (extras>0) comm += (extras + " EXTRAS. ");
        String comm0 = "";
        if (lastRequest != null) {
            comm0 = lastRequest.getVehicle() + ". ";
            if (lastRequest.getArrivalComments()!=null && !lastRequest.getArrivalComments().isEmpty())
                comm0 +=  lastRequest.getArrivalComments() + ". ";
            if (lastRequest.getChildren()>0) comm0 += (lastRequest.getChildren() + " CHILDREN. ");
            if (lastRequest.getBabies()>0) comm0 += (lastRequest.getBabies() + " BABIES. ");
            if (lastRequest.getExtras() >0) comm0 += (lastRequest.getExtras() + " EXTRAS. ");
        }
        if (!s.getComment().contains(comm) && (lastRequest == null || !comm.equals(comm0))) return true;

        if (getValue() != 0 && effectiveValue != s.getOverridedNetValue() && (lastRequest == null || effectiveValue != lastRequest.getEffectiveValue())) return true;

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

    private boolean changesInDeparture(TransferService s, TransferBookingRequest lastRequest) {
        if (s.isCancelled()!= (departureStatus.equals(STATUS.CANCELLED)) && (lastRequest == null || !departureStatus.equals(lastRequest.getDepartureStatus()))) return true;

        ServiceConfirmationStatus a = ServiceConfirmationStatus.PENDING;
        if (departureConfirmed) a = ServiceConfirmationStatus.CONFIRMED;
        if (!a.equals(s.getAnswer()) && (lastRequest == null || departureConfirmed != lastRequest.isDepartureConfirmed())) return true;

        String txt = "" + departureResort + " (" + departureAddress + ")";
        if (!txt.equals(s.getPickupText()) && (lastRequest == null || !txt.equals("" + lastRequest.getDepartureResort() + " (" + lastRequest.getDepartureAddress() + ")"))) return true;
        txt = "" + departureFlightCompany + departureFlightNumber;
        if (!txt.equals(s.getFlightNumber()) && (lastRequest == null || !txt.equals("" + lastRequest.getDepartureFlightCompany() + lastRequest.getDepartureFlightNumber()))) return true;
        txt = "" + departureDestinationAirport;
        if (!txt.equals(s.getFlightOriginOrDestination()) && (lastRequest == null || !txt.equals("" + lastRequest.getDepartureDestinationAirport()))) return true;

        LocalDateTime t = getTime(departureFlightDate + " " + departureFlightTime);
        if (!t.equals(s.getFlightTime()) && (lastRequest == null || !t.equals(getTime(lastRequest.getDepartureFlightDate() + " " + lastRequest.getDepartureFlightTime())))) return true;

         // && (lastRequest == null || !arrivalStatus.equals(lastRequest.getArrivalStatus()))

        if (departurePickupDate!=null && departurePickupTime!=null) {
            t = getTime(departurePickupDate + " " + departurePickupTime);
            if (s.getImportedPickupTime() != null && !s.getImportedPickupTime().equals(t) && (lastRequest == null || !t.equals(getTime(lastRequest.getDeparturePickupDate() + " " + lastRequest.getDeparturePickupTime())))) return true;
        }
        else if (s.getImportedPickupTime()!=null && (lastRequest == null || lastRequest.getDeparturePickupTime() != null)) return true;

        int p = adults + children + babies;
        if (p!=s.getPax() && (lastRequest == null || p != lastRequest.getAdults() + lastRequest.getChildren() + lastRequest.getBabies())) return true;

       // s.setDropoffText(departureAirport);

        if (!serviceType.equals(s.getTransferType()) && (lastRequest == null || !serviceType.equals(lastRequest.getServiceType())))return true;

        if (!s.getOffice().equals(getTask().getOffice())) return true;
        if (!s.getPos().equals(getTask().getPointOfSale())) return true;

        if (s.getComment()==null) s.setComment("");
        String comm = vehicle + ". ";
        if (getDepartureComments()!=null && !getDepartureComments().isEmpty())
            comm +=  getDepartureComments() + ". ";
        if (children>0) comm += (children + " CHILDREN. ");
        if (babies>0) comm += (babies + " BABIES. ");
        if (extras>0) comm += (extras + " EXTRAS. ");
        String comm0 = "";
        if (lastRequest != null) {
            comm0 = lastRequest.getVehicle() + ". ";
            if (lastRequest.getDepartureComments()!=null && !lastRequest.getDepartureComments().isEmpty())
                comm0 +=  lastRequest.getDepartureComments() + ". ";
            if (lastRequest.getChildren()>0) comm0 += (lastRequest.getChildren() + " CHILDREN. ");
            if (lastRequest.getBabies()>0) comm0 += (lastRequest.getBabies() + " BABIES. ");
            if (lastRequest.getExtras()>0) comm0 += (lastRequest.getExtras() + " EXTRAS. ");
        }
        if (!s.getComment().contains(comm)  && (lastRequest == null || !comm.equals(comm0))) return true;

        if (getValue() != 0 && effectiveValue != s.getOverridedNetValue() && (lastRequest == null || effectiveValue != lastRequest.getEffectiveValue())) return true;

        return false;
    }

    private void fillDeparture(TransferService s, TransferBookingRequest lastRequest) {
        if (lastRequest == null || !departureStatus.equals(lastRequest.getDepartureStatus())) s.setCancelled(departureStatus.equals(STATUS.CANCELLED));

        if (lastRequest == null || !departurePickupDate.equals(lastRequest.getDeparturePickupDate()) || !departurePickupTime.equals(lastRequest.getDeparturePickupTime())) {
            if (departurePickupDate!=null && departurePickupTime!=null)
                s.setImportedPickupTime(getTime(departurePickupDate + " " + departurePickupTime));
            else
                s.setImportedPickupTime(null);
        }

        if (lastRequest == null || departureConfirmed != lastRequest.isDepartureConfirmed()) {
            ServiceConfirmationStatus a = ServiceConfirmationStatus.PENDING;
            if (departureConfirmed) a = ServiceConfirmationStatus.CONFIRMED;
            s.setAnswer(a);
        }

        if (lastRequest == null || !departureResort.equals(lastRequest.getDepartureResort()) || !departureAddress.equals(lastRequest.getDepartureAddress())) s.setPickupText("" + departureResort + " (" + departureAddress + ")");
        if (lastRequest == null || !departureFlightCompany.equals(lastRequest.getDepartureFlightCompany()) || !departureFlightNumber.equals(lastRequest.getDepartureFlightNumber())) s.setFlightNumber("" + departureFlightCompany + departureFlightNumber);
        if (lastRequest == null || !departureDestinationAirport.equals(lastRequest.getDepartureDestinationAirport())) s.setFlightOriginOrDestination("" + departureDestinationAirport);
        if (lastRequest == null || !departureFlightDate.equals(lastRequest.getDepartureFlightDate()) || !departureFlightTime.equals(lastRequest.getDepartureFlightTime())) s.setFlightTime(getTime(departureFlightDate + " " + departureFlightTime));
        if (lastRequest == null || (adults + children + babies) != (lastRequest.getAdults() + lastRequest.getChildren() + lastRequest.getBabies())) s.setPax(adults + children + babies);
       // s.setAdults(adults);
        //s.setChildren(children);

        if (lastRequest == null || !departureAirport.equals(lastRequest.getDepartureAirport())) s.setDropoffText(departureAirport);

        if (lastRequest == null || !serviceType.equals(lastRequest.getServiceType())) s.setTransferType(serviceType);

        s.setOffice(getTask().getOffice());
        s.setPos(getTask().getPointOfSale());

        if (s.getComment()==null) s.setComment("");
        String comm = vehicle + ". ";
        if (getDepartureComments()!=null && !getDepartureComments().isEmpty())
            comm +=  getDepartureComments() + ". ";
        if (children>0) comm += (children + " CHILDREN. ");
        if (babies>0) comm += (babies + " BABIES. ");
        if (extras>0) comm += (babies + " EXTRAS. ");
        String comm0 = "";
        if (lastRequest != null) {
            comm0 = lastRequest.getVehicle() + ". ";
            if (lastRequest.getDepartureComments()!=null && !lastRequest.getDepartureComments().isEmpty())
                comm0 +=  lastRequest.getDepartureComments() + ". ";
            if (lastRequest.getChildren()>0) comm0 += (lastRequest.getChildren() + " CHILDREN. ");
            if (lastRequest.getBabies()>0) comm0 += (lastRequest.getBabies() + " BABIES. ");
            if (lastRequest.getExtras()>0) comm0 += (lastRequest.getExtras() + " EXTRAS. ");
        }
        if (!s.getComment().contains(comm) && (lastRequest == null || !comm.equals(comm0)))
            s.setComment(comm + "\n" + s.getComment());

        if (getValue() != 0 && (lastRequest == null || getValue() != lastRequest.getValue())) {
            s.setOverridedNetValue(effectiveValue);
            s.setValueOverrided(true);
            effectiveValue = 0;
        }

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
