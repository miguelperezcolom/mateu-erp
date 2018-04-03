package io.mateu.erp.model.importing;

import com.google.common.base.Strings;
import io.mateu.erp.model.authentication.Audit;
import io.mateu.erp.model.authentication.User;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.product.transfer.TransferType;
import io.mateu.erp.model.util.Constants;
import io.mateu.erp.model.util.Helper;
import io.mateu.erp.model.util.JPATransaction;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Entity
@Getter
@Setter
public class TravelRepublicImportTask extends TransferImportTask {

    public TravelRepublicImportTask() {}

    public TravelRepublicImportTask(String name, User user, Actor customer, String html, Office office, PointOfSale pos)
    {
        this.setCustomer(customer);

        this.setName(name);

        this.setAudit(new Audit(user));

        this.setPriority(0);

        this.setStatus(STATUS.PENDING);

        this.setHtml(html);

        setOffice(office);

        setPointOfSale(pos);

    }

    public TravelRepublicImportTask( User user, Actor customer, String xml, Office office, PointOfSale pos)
    {
        this("TravelRepublic", user, customer,xml, office, pos);//guardamos el xml en el campo del html
    }


    @Override
    public void execute(EntityManager em) {
        String result = "";
        this.setAdditions(0);
        this.setCancellations(0);
        this.setModifications(0);
        this.setUnmodified(0);
        this.setErrors(0);
        this.setTotal(0);

        try {

            String[][] lines = Helper.parsearCSV(getHtml());


            //recorre cada transfer del fichero
            String res = "";
            String aux = "";
            boolean pasadaCabecera = false;
            String[] cabecera = null;
            for (String[] l : lines) {
                if (!pasadaCabecera && l.length > 0 && "TransferBookingId".equalsIgnoreCase(l[0])) {
                    cabecera = l;
                    pasadaCabecera = true;
                }
                else if (pasadaCabecera && l.length > 0){

                    aux = "";
                    this.increaseTotal();
                    try {
                        aux = "\nRef. " + l[0] + ": ";
                        //por cada uno rellena un "transferBookingRequest" y llama a updatebooking()
                        TransferBookingRequest rq = rellenarTransferBookingRequest(cabecera, l);
                        res = rq.updateBooking(em);
                        //vamos guardando el resultado junto con la refAge para crear el informe final
                        if (res.length() > 0)//hay errores
                            result += aux + res;
                    /*else {
                        result += "Ok ";
                    }*/
                    } catch (Exception e) {
                        e.printStackTrace();
                        result += aux + "Error => " + e.getClass() + " - " + e.getMessage();
                    }

                }
            }

            this.setStatus(STATUS.OK);//fichero procesado

        } catch (Exception ex) {
            ex.printStackTrace();
            result += "General exception: " + ex.getClass() + " - " + ex.getMessage();
            this.setStatus(STATUS.ERROR);//fichero no procesado
        }

        this.getAudit().touch(em.find(User.class, Constants.IMPORTING_USER_LOGIN));
        this.setReport(result.replaceFirst("\n",""));

    }

    private TransferBookingRequest rellenarTransferBookingRequest(String[] cabecera, String[] l) {
        TransferBookingRequest rq = new TransferBookingRequest();
        rq.setTask(this);
        rq.setSource("" + l);
        rq.setCustomer(this.getCustomer());


        Map<String, String> m = new HashMap<>();
        for (int pos = 0; pos < cabecera.length; pos++) if (pos < l.length) {
            m.put(cabecera[pos], l[pos]);
        }

        rq.setAgencyReference(m.get("TransferBookingId"));
        rq.setCreated("" + new Date());
        rq.setModified("" + new Date());

        String type = m.get("TransferTypeDescription");
        if (type.toUpperCase().contains("SHUTTLE"))
            rq.setServiceType(TransferType.SHUTTLE);
        else if (type.toUpperCase().contains("EXECUTIVE"))
            rq.setServiceType(TransferType.EXECUTIVE);
        else
            rq.setServiceType(TransferType.PRIVATE);
        rq.setVehicle(type);

        rq.setPassengerName(m.get("LeadName"));
        rq.setPhone("");
        rq.setAdults(Integer.parseInt(m.get("Adults")));
        rq.setChildren(Integer.parseInt(m.get("Children")));
        rq.setBabies(Integer.parseInt(m.get("Infants")));



        String extras = "";


        //ARRIVAL o DEPARTURE? En shuttleDirect cada reserva es 1 trayecto (no tenemos Both)
        boolean isArrival=false;
        rq.setTransferServices(TransferBookingRequest.TRANSFERSERVICES.DEPARTURE);//departure=el aerpuerto esta en el destino
        String departureAirport = m.get("InboundFlightDepartureAirport");
        String arrivalAirport = m.get("OutboundFlightArrivalAirport");

        String nc = m.get("NetCost");
        if (!Strings.isNullOrEmpty(nc)) {
            //EUR19.18
            try {
                rq.setCurrency(nc.substring(0, 3));
                rq.setValue(Helper.toDouble(nc.substring(3)));
                if (rq.getValue() != 0 && TransferType.SHUTTLE.equals(rq.getServiceType())) rq.setValue(rq.getValue() * 2);
                if (rq.getValue() != 0) rq.setValue(rq.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!Strings.isNullOrEmpty(arrivalAirport)) {
            rq.setTransferServices(TransferBookingRequest.TRANSFERSERVICES.ARRIVAL);

            TransferBookingRequest.STATUS s =  TransferBookingRequest.STATUS.OK;
            if (!"Confirmed".equalsIgnoreCase(m.get("BookingStatusDescription")))
                s= TransferBookingRequest.STATUS.CANCELLED;
            rq.setArrivalStatus(s);

            //if (tr.getChildText("confirmationcode")!=null && !tr.getChildText("confirmationcode").isEmpty())
            //    rq.setArrivalConfirmed(true);

            rq.setArrivalAirport(m.get("OutboundFlightArrivalAirport"));
            rq.setArrivalResort(m.get("DestinationName"));
            rq.setArrivalAddress(m.get("DestinationAddress"));
            rq.setArrivalFlightDate(convertirFormatoFecha(m.get("OutboundFlightArrivalDate")));
            rq.setArrivalFlightTime(m.get("OutboundFlightArrivalTime"));
            rq.setArrivalFlightNumber(m.get("OutboundFlightNumber"));
            rq.setArrivalFlightCompany("");
            rq.setArrivalOriginAirport(m.get("OutboundFlightDepartureAirport"));
            rq.setArrivalComments(extras + "");
            rq.setArrivalPickupDate("");
            rq.setArrivalPickupTime("");
        }
        if (!Strings.isNullOrEmpty(departureAirport)) {
            rq.setTransferServices(TransferBookingRequest.TRANSFERSERVICES.DEPARTURE);



            TransferBookingRequest.STATUS s =  TransferBookingRequest.STATUS.OK;
            if (!"Confirmed".equalsIgnoreCase(m.get("BookingStatusDescription")))
                s= TransferBookingRequest.STATUS.CANCELLED;
            rq.setDepartureStatus(s);

            //if (tr.getChildText("confirmationcode")!=null && !tr.getChildText("confirmationcode").isEmpty())
            //    rq.setDepartureConfirmed(true);

            rq.setDepartureAirport(m.get("InboundFlightDepartureAirport"));
            rq.setDepartureResort(m.get("DestinationName"));
            rq.setDepartureAddress(m.get("DestinationAddress"));
            rq.setDepartureFlightDate(convertirFormatoFecha(m.get("InboundFlightDepartureDate")));
            rq.setDepartureFlightTime(m.get("InboundFlightDepartureTime"));
            rq.setDepartureFlightNumber(m.get("InboundFlightNumber"));
            rq.setDepartureFlightCompany("");
            rq.setDepartureDestinationAirport(m.get("InboundFlightArrivalAirport"));
            rq.setDepartureComments(extras + "");
            rq.setDeparturePickupDate("");
            rq.setDeparturePickupTime("");

        }
        if (!Strings.isNullOrEmpty(arrivalAirport) && !Strings.isNullOrEmpty(departureAirport)) {
            rq.setTransferServices(TransferBookingRequest.TRANSFERSERVICES.BOTH);
        }



        return rq;
    }

    private String convertirFormatoFecha(String s) {
        try {
            return LocalDate.parse(s, DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.UK)).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            e.printStackTrace();
            return s;
        }
    }



    public static void main(String... args) throws Throwable {

        System.setProperty("appconf", "/home/miguel/quonext/mateu.properties");


        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Throwable {

                TravelRepublicImportTask t = new TravelRepublicImportTask();
                em.persist(t);
                t.setAudit(new Audit());
                t.setCustomer(em.find(Actor.class, 47l));
                t.setHtml(Helper.leerFichero(getClass().getResourceAsStream("/manifest.csv")));
                t.execute(em);

            }
        });

    }

}
