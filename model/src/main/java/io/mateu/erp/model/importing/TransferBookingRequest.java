package io.mateu.erp.model.importing;

import com.google.common.base.Strings;
import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.ServiceConfirmationStatus;
import io.mateu.erp.model.booking.parts.TransferBooking;
import io.mateu.erp.model.booking.transfer.TransferPointMapping;
import io.mateu.erp.model.partners.Agency;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.erp.model.product.transfer.TransferType;
import io.mateu.mdd.core.annotations.*;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.util.Constants;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.workflow.WorkflowEngine;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by Antonia on 26/03/2017.
 */

@Entity
@Getter
@Setter
@NewNotAllowed
public class TransferBookingRequest {

    @ListColumn
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
    @ListColumn
    private Agency customer;

    @SearchFilter
    @Output
    @ListColumn
    private String agencyReference;

    @Output
    private String created;//ojo, de momento estos campos no se estan aplicando en la reserva
    @Output
    private String modified; //ojo, de momento estos campos no se estan aplicando en la reserva

    @Output
    private String currency;

    @Output
    private double value;

    @Transient
    @Output
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
    @ListColumn
    private String passengerName;
    @Output
    private String phone;
    @Output
    private String email;
    @Output
    private int adults=0;
    @Output
    private int children=0;
    @Output
    private int babies=0;
    @Output
    private int extras=0;
    @Output
    private String comments;

    public enum TRANSFERSERVICES {ARRIVAL, DEPARTURE, BOTH};
    @ListColumn
    @Output
    private TRANSFERSERVICES transferServices;

    public enum STATUS {OK, CANCELLED};


    @Section("ArrivalBooking")
    @Output
    private STATUS arrivalStatus;
    @Output
    private String arrivalAirport;
    @Output
    private String arrivalResort;
    @Output
    private String arrivalAddress;
    @Output
    private boolean arrivalConfirmed=false;

    @Output
    private String arrivalFlightDate;//formato dd/MM/yyyy
    public void setArrivalFlightDate(String day)
    {
        arrivalFlightDate=checkDayFormat(day);
    }

    @Output
    private String arrivalFlightTime;//formato HH:mm
    public void setArrivalFlightTime(String time)
    {
        arrivalFlightTime= checkTimeFormat(time);
    }
    @Output
    private String arrivalFlightNumber;
    @Output
    private String arrivalFlightCompany;
    @Output
    private String arrivalOriginAirport;
    @Output
    private String arrivalComments;
    @Output
    private String arrivalPickupDate;//formato dd/MM/yyyy
    public void setArrivalPickupDate(String day)
    {
        arrivalPickupDate= checkDayFormat(day);
    }
    @Output
    private String arrivalPickupTime;//formato HH:mm
    public void setArrivalPickupTime(String time)
    {
        arrivalPickupTime= checkTimeFormat(time);
    }

    @Section("Departure")
    @Output
    private STATUS departureStatus;
    @Output
    private String departureAirport;
    @Output
    private String departureResort;

    public void setDepartureResort(String departureResort) {
        this.departureResort = (departureResort != null)?departureResort.replaceAll("\\n", "_").replaceAll("\\r", "_"):departureResort;
    }

    @Output
    private String departureAddress;

    public void setDepartureAddress(String departureAddress) {
        this.departureAddress = (departureAddress != null)?departureAddress.replaceAll("\\n", "_").replaceAll("\\r", "_"):departureAddress;
    }

    @Output
    private boolean departureConfirmed=false;
    @Output
    private String departureFlightDate;//formato dd/MM/yyyy
    public void setDepartureFlightDate(String day)
    {
        departureFlightDate=checkDayFormat(day);
    }
    @Output
    private String departureFlightTime;//formato HH:mm
    public void setDepartureFlightTime(String time)
    {
        departureFlightTime= checkTimeFormat(time);
    }

    @Output
    private String departureFlightNumber;
    @Output
    @SameLine
    private String departureFlightCompany;
    @Output
    private String departureDestinationAirport;
    @Output
    @SameLine
    private String departureComments;
    @Output
    private String departurePickupDate; //formato dd/MM/yyyy
    public void setDeparturePickupDate(String day)
    {
        departurePickupDate= checkDayFormat(day);
    }
    @Output
    private String departurePickupTime;//formato HH:mm
    public void setDeparturePickupTime(String time)
    {
        departurePickupTime= checkTimeFormat(time);
    }

    @Section("Others")

    @ManyToOne
    @Output
    private TransferPoint airport;


    @ManyToOne
    @Output
    private TransferPoint destination;

    @Output
    private String source; //xml origen o csv

    @Output
    @ManyToOne(cascade = CascadeType.PERSIST)
    @ListColumn
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

    public String getSignature() {
        String s = "";

        s += agencyReference;

        s += "|";

        s += value;

        s += "|";

        s += serviceType.name(); //Shuttle, Private, Executive

        s += "|";

        s += vehicle; //Si es un privado (taxi, minibus, etc)

        s += "|";

        s += passengerName;

        s += "|";

        s += phone;

        s += "|";

        s += email;

        s += "|";

        s += adults;

        s += "|";

        s += children;

        s += "|";

        s += babies;

        s += "|";

        s += extras;

        s += "|";

        s += comments;

        s += "|";

        s += transferServices.name();

        s += "|";

        s += arrivalStatus != null?arrivalStatus.name():"-";

        s += "|";

        s += arrivalAirport;

        s += "|";

        s += arrivalResort;

        s += "|";

        s += arrivalAddress;

        s += "|";

        s += arrivalConfirmed;

        s += "|";

        s += arrivalFlightDate;//formato dd/MM/yyyy

        s += "|";

        s += arrivalFlightTime;//formato HH:mm

        s += "|";

        s += arrivalFlightNumber;

        s += "|";

        s += arrivalFlightCompany;

        s += "|";

        s += arrivalOriginAirport;

        s += "|";

        s += arrivalComments;

        s += "|";

        s += arrivalPickupDate;//formato dd/MM/yyyy

        s += "|";

        s += arrivalPickupTime;//formato HH:mm

        s += "|";

        s += departureStatus != null?departureStatus.name():"-";

        s += "|";

        s += departureAirport;

        s += "|";

        s += departureResort;

        s += "|";

        s += departureAddress;

        s += "|";

        s += departureConfirmed;

        s += "|";

        s += departureFlightDate;//formato dd/MM/yyyy

        s += "|";

        s += departureFlightTime;//formato HH:mm

        s += "|";

        s += departureFlightNumber;

        s += "|";

        s += departureFlightCompany;

        s += "|";

        s += departureDestinationAirport;

        s += "|";

        s += departureComments;

        s += "|";

        s += departurePickupDate; //formato dd/MM/yyyy

        s += "|";

        s += departurePickupTime;//formato HH:mm


        return s;
    }


    @Action
    public String updateBooking(EntityManager em)
    {
        String _result="";
        try {

            //Validamos y si no va bien salimos devolviendo el error
            _result = validate();
            if (_result.length() > 0) {
                this.result = _result;
                return _result;
            }

            _result = "";

            effectiveValue = value;

            if ((TRANSFERSERVICES.ARRIVAL.equals(transferServices) || TRANSFERSERVICES.BOTH.equals(transferServices))) {
                setAirport(TransferPointMapping.getTransferPoint(em, "" + arrivalAirport, this));
                setDestination(TransferPointMapping.getTransferPoint(em, "" + arrivalResort + " (" + arrivalAddress + ")", this));
            } else {
                setAirport(TransferPointMapping.getTransferPoint(em, "" + departureAirport, this));
                setDestination(TransferPointMapping.getTransferPoint(em, "" + departureResort + " (" + departureAddress + ")", this));
            }


            if (airport == null || destination == null) {

                _result = "Unmapped";

            } else {

                boolean hayCambios=false;

                //Si ok, actualizamos la reserva...
                TransferBooking b = (TransferBooking) Booking.getByAgencyRef(em, agencyReference, customer);//buscamos la reserva
                User u = em.find(User.class, Constants.IMPORTING_USER_LOGIN);
                if (b==null)//Crear reserva nueva
                {
                    b = new TransferBooking();
                    b.setAudit(new Audit(u));

                    b.setPos(getTask().getPointOfSale());
                    b.setAgencyReference(agencyReference);
                    b.setAgency(customer);
                    b.setLeadName(passengerName);
                    b.setTelephone(phone);
                    b.setEmail(email);
                    if (comments!=null) b.setSpecialRequests(comments);
                    b.setConfirmed(true);
                    this.getTask().increaseAdditions();

                    hayCambios = true;

                } else {
                    hayCambios = b.getTransferBookingRequest() == null || !b.getTransferBookingRequest().getSignature().equals(getSignature());
                }
                //else //reserva ya existente --> actualizar
                if (hayCambios) {

                    boolean hayBloqueos = b.isLocked();

                    TransferBookingRequest lastRequest = b.getTransferBookingRequest();

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
                        if (comments!=null && !b.getSpecialRequests().contains(comments) && (lastRequest == null || !comments.equals(lastRequest.getComments())))
                        {
                            b.setSpecialRequests(b.getSpecialRequests() + "--" + comments);
                            hayCambios=true;
                        }

                    }



                    if (TRANSFERSERVICES.ARRIVAL.equals(transferServices) || TRANSFERSERVICES.BOTH.equals(transferServices)) {

                        fillArrival(b);

                    }

                    if (TRANSFERSERVICES.DEPARTURE.equals(transferServices) || TRANSFERSERVICES.BOTH.equals(transferServices)) {

                        fillDeparture(b);

                    }

                    if (getValue() != 0 && (lastRequest == null || getValue() != lastRequest.getValue())) {
                        b.setOverridedValue(Helper.roundEuros(effectiveValue));
                        b.setCurrency(b.getAgency().getCurrency());
                        b.setOverridedBillingConcept(task.getBillingConcept());
                        b.setValueOverrided(true);
                        effectiveValue = 0;
                    }

                    b.setTransferBookingRequest(this);

                    if (hayCambios) {
                        b.getAudit().touch(u);
                        this.setBooking(b);
                        b.setTransferBookingRequest(this);
                        this.getTask().increaseAdditions();
                    }

                } else {
                    //sin cambios
                    this.getTask().increaseUnmodified();
                }

            }


        } catch (Throwable ex) {
            _result += ex.getMessage();

            ConstraintViolationException cve = null;
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


    private void fillArrival(TransferBooking s) {

        TransferBookingRequest lastRequest = s.getTransferBookingRequest();

        if (lastRequest == null || !arrivalStatus.equals(lastRequest.getArrivalStatus())) s.setActive(!arrivalStatus.equals(STATUS.CANCELLED));

        /*
        if (lastRequest == null || !arrivalPickupDate.equals(lastRequest.getArrivalPickupDate()) || !arrivalPickupTime.equals(lastRequest.getArrivalPickupTime())) {
            if (arrivalPickupDate!=null && arrivalPickupTime!=null)
                s.setImportedPickupTime(getTime(arrivalPickupDate + " " + arrivalPickupTime));
            else
                s.setImportedPickupTime(null);
        }
        */

        //todo: se ha movido a la reserva
        /*
        if (lastRequest == null || arrivalConfirmed != lastRequest.isArrivalConfirmed()) {
            ServiceConfirmationStatus a = ServiceConfirmationStatus.PENDING;
            if (arrivalConfirmed) a = ServiceConfirmationStatus.CONFIRMED;
            s.setAnswer(a);
        }
        */

        if (lastRequest == null || !destination.equals(lastRequest.getDestination())) s.setDestination(destination);
        if (lastRequest == null || !arrivalFlightCompany.equals(lastRequest.getArrivalFlightCompany()) || !arrivalFlightNumber.equals(lastRequest.getArrivalFlightNumber())) s.setArrivalFlightNumber("" + arrivalFlightCompany + arrivalFlightNumber);
        if (lastRequest == null || !arrivalOriginAirport.equals(lastRequest.getArrivalOriginAirport())) s.setArrivalFlightOrigin("" + arrivalOriginAirport);
        if (lastRequest == null || !arrivalFlightDate.equals(lastRequest.getArrivalFlightDate()) || !arrivalFlightTime.equals(lastRequest.getArrivalFlightTime())) s.setArrivalFlightTime(getTime(arrivalFlightDate + " " + arrivalFlightTime));
        if (lastRequest == null || adults != lastRequest.getAdults()) s.setAdults(adults);
        if (lastRequest == null || children != lastRequest.getChildren()) s.setChildren(children);

        if (lastRequest == null || !airport.equals(lastRequest.getAirport())) s.setOrigin(airport);

        if (lastRequest == null || !serviceType.equals(lastRequest.getServiceType())) s.setTransferType(serviceType);

        //s.setOffice(getTask().getOffice());
        //s.setPos(getTask().getPointOfSale());


        //todo: se ha movido a la reserva

        if (s.getSpecialRequests()==null) s.setSpecialRequests("");
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
        if (!s.getSpecialRequests().contains(comm) && (lastRequest == null || !comm.equals(comm0)))
            s.setSpecialRequests(comm + "\n" + s.getSpecialRequests());

    }


    private void fillDeparture(TransferBooking s) {
        TransferBookingRequest lastRequest = s.getTransferBookingRequest();

        if (lastRequest == null || !departureStatus.equals(lastRequest.getDepartureStatus())) s.setActive(!departureStatus.equals(STATUS.CANCELLED));

        /*
        if (lastRequest == null || !departurePickupDate.equals(lastRequest.getDeparturePickupDate()) || !departurePickupTime.equals(lastRequest.getDeparturePickupTime())) {
            if (departurePickupDate!=null && departurePickupTime!=null)
                s.setImportedPickupTime(getTime(departurePickupDate + " " + departurePickupTime));
            else
                s.setImportedPickupTime(null);
        }
        */

        if (lastRequest == null || departureConfirmed != lastRequest.isDepartureConfirmed()) {
            ServiceConfirmationStatus a = ServiceConfirmationStatus.PENDING;
            if (departureConfirmed) a = ServiceConfirmationStatus.CONFIRMED;
            //todo: se ha movido a la reserva
            //s.setAnswer(a);
        }

        if (lastRequest == null || !destination.equals(lastRequest.getDestination())) s.setDestination(destination);
        if (lastRequest == null || !departureFlightCompany.equals(lastRequest.getDepartureFlightCompany()) || !departureFlightNumber.equals(lastRequest.getDepartureFlightNumber())) s.setDepartureFlightNumber("" + departureFlightCompany + departureFlightNumber);
        if (lastRequest == null || !departureDestinationAirport.equals(lastRequest.getDepartureDestinationAirport())) s.setDepartureFlightDestination("" + departureDestinationAirport);
        if (lastRequest == null || !departureFlightDate.equals(lastRequest.getDepartureFlightDate()) || !departureFlightTime.equals(lastRequest.getDepartureFlightTime())) s.setDepartureFlightTime(getTime(departureFlightDate + " " + departureFlightTime));
        if (lastRequest == null || adults != lastRequest.getAdults()) s.setAdults(adults);
        if (lastRequest == null || children != lastRequest.getChildren()) s.setChildren(children);

        if (lastRequest == null || !airport.equals(lastRequest.getAirport())) s.setOrigin(airport);

        if (lastRequest == null || !serviceType.equals(lastRequest.getServiceType())) s.setTransferType(serviceType);


        if (s.getSpecialRequests()==null) s.setSpecialRequests("");
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
        if (!s.getSpecialRequests().contains(comm) && (lastRequest == null || !comm.equals(comm0)))
            s.setSpecialRequests(comm + "\n" + s.getSpecialRequests());

    }



    private LocalDateTime getTime(String s)  {
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


    public static TransferBookingRequest getByAgencyRef(EntityManager em, String agencyRef, Agency agency)
    {
        try {
            String jpql = "select x from " + TransferBookingRequest.class.getName() + " x" +
                    " where x.agencyReference='" + agencyRef + "' and x.customer.id= " + agency.getId() + " order by x.when desc";
            Query q = em.createQuery(jpql).setFlushMode(FlushModeType.COMMIT);
            List<TransferBookingRequest> l = q.getResultList();
            TransferBookingRequest b = (l.size() > 0)?l.get(0):null;
            return b;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        System.setProperty("appconf", "/home/miguel/work/quotravel.properties");

        try {
            Helper.transact(em -> {

                em.find(TransferBookingRequest.class, 601l).setAgencyReference("" + LocalDateTime.now());

            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        WorkflowEngine.exit(0);

    }

    @PostPersist@PostUpdate
    public void post() {
        WorkflowEngine.add(() -> {
            try {
                Helper.transact(em -> {
                    TransferBookingRequest r = em.merge(this);
                    r.updateBooking(em);
                });
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }


    public static void run() {
        System.out.println("TransferBookingRequest.run()");
        try {
            Helper.transact(em -> {

                ((List<TransferBookingRequest>)em.createQuery("select x from " + TransferBookingRequest.class.getName() + " x where x.result = 'Unmapped'").getResultList()).forEach(t -> {
                    t.updateBooking(em);
                });

            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "rq " + getId() + " " + getAgencyReference() + " " + (getTask() != null && getTask().getCustomer() != null?getTask().getCustomer().getName():"--");
    }
}
