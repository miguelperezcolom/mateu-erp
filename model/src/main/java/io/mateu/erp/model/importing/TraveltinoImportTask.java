package io.mateu.erp.model.importing;

import com.Ostermiller.util.CSVParser;
import com.google.common.base.Strings;
import io.mateu.erp.model.authentication.ERPUser;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.partners.Agency;
import io.mateu.erp.model.product.transfer.TransferType;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.util.Constants;
import io.mateu.mdd.core.model.util.EmailHelper;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.workflow.WorkflowEngine;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Antonia on 26/03/2017.
 */
@Entity
@Getter@Setter
public class TraveltinoImportTask extends TransferImportTask {

    public TraveltinoImportTask() {}

    public TraveltinoImportTask(String name, ERPUser user, Agency customer, String html, Office office, PointOfSale pos, BillingConcept billingConcept)
    {
       this.setCustomer(customer);

       this.setName(name);

       this.setAudit(new Audit(user));

       this.setPriority(0);

       this.setStatus(STATUS.PENDING);

       this.setHtml(html);

       setOffice(office);

       setPointOfSale(pos);

       setBillingConcept(billingConcept);
    }

    @Override
    public void execute(EntityManager em) {

        System.out.println("Running TraveltinoImporTask");
        System.out.println("**********************");
        System.out.println(getHtml());
        System.out.println("**********************");

        String result = "";
        this.setAdditions(0);
        this.setCancellations(0);
        this.setModifications(0);
        this.setUnmodified(0);
        this.setErrors(0);
        this.setTotal(0);

        try {


            String[][] csv = CSVParser.parse(getHtml());


            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            process(em, csv, pw);
            result += sw.toString();

            this.setStatus(STATUS.OK);//fichero procesado

        } catch (Exception ex) {
            ex.printStackTrace();

            System.out.println("html=" + getHtml());

            result += "General exception: " + ex.getClass() + " - " + ex.getMessage();
            this.setStatus(STATUS.ERROR);//fichero no procesado
        }

        this.getAudit().touch(em.find(ERPUser.class, Constants.IMPORTING_USER_LOGIN));
        this.setReport(result.replaceFirst("\n",""));

      }


    public void process(EntityManager em, String[][] csv, PrintWriter pw) {

        Agency traveltino = (Agency) em.createQuery("select x from " + Agency.class.getName() + " x where upper(x.name) = 'TRAVELTINO'").getResultList().get(0);

        Map<String, Integer> cabecera = new HashMap<>();
        boolean cabeceraEncontrada = false;
        for (String[] l : csv) {

            System.out.println("procesando " + Arrays.toString(l));

            if (!cabeceraEncontrada) {
                if (l.length > 0) {
                    for (int i = 0; i < l.length; i++) if (!Strings.isNullOrEmpty(l[i]) && !cabecera.containsKey(l[i].trim())) cabecera.put(l[i].trim(), i);
                    cabeceraEncontrada = cabecera.size() > 0;
                }
                System.out.println("cabecera encontrada = " + cabeceraEncontrada);
                System.out.println("cabeceras = " + cabecera.size());
            } else {

                System.out.println("l.length = " + l.length);

                if (l.length > 0) {
                    int celdasConContenido = 0;
                    for (int i = 0; i < l.length; i++) if (!Strings.isNullOrEmpty(l[i])) celdasConContenido++;

                    if (celdasConContenido > 6) {

                        try {

                            System.out.println("rellenando rq");

                            TransferBookingRequest rq = rellenarRq(em, l, cabecera, traveltino);

                            TransferBookingRequest last = TransferBookingRequest.getByAgencyRef(em, rq.getAgencyReference(), traveltino);
                            if (last == null || !last.getSavedSignature().equals(rq.getSignature())) {
                                rq.setSavedSignature(rq.getSignature());
                                em.persist(rq);
                                System.out.println("grabando rq");
                                em.persist(rq);
                            } else {
                                System.out.println("rq no grabada. Ya existe y la firma no ha cambiado");
                            }

                        } catch (Throwable t) {
                            t.printStackTrace();
                        }

                    }

                }

            }

        }



    }

    private TransferBookingRequest rellenarRq(EntityManager em, String[] l, Map<String, Integer> cabecera, Agency agencia) {
        TransferBookingRequest rq = new TransferBookingRequest();

        rq.setTask(this);
        rq.setCustomer(agencia);
        rq.setAgencyReference(get(l, cabecera, "locatorProvider"));
        rq.setAdults(getInt(l, cabecera, "Adults"));
        rq.setArrivalAddress(get(l, cabecera, "Destination Hotel Address"));
        rq.setArrivalAirport(get(l, cabecera, "Origin Arrival IATA Code"));
        rq.setArrivalComments("");
        rq.setArrivalFlightCompany("");
        rq.setArrivalFlightDate(get(l, cabecera, "Origin Arrival Flight Date", 0));
        rq.setArrivalFlightNumber(get(l, cabecera, "Origin Flight ID"));
        rq.setArrivalFlightTime(get(l, cabecera, "Origin Arrival Flight Date", 1));
        rq.setArrivalOriginAirport(get(l, cabecera, "Origin Departure IATA Code"));
        rq.setArrivalPickupDate(get(l, cabecera, "Destination Hotel Pickup Date", 0));
        rq.setArrivalPickupTime(get(l, cabecera, "Destination Hotel Pickup Date", 1));
        rq.setArrivalResort(get(l, cabecera, "Destination Hotel Name"));
        rq.setArrivalStatus("RESERVED".equalsIgnoreCase(get(l, cabecera, "reservationStatus"))?TransferBookingRequest.STATUS.OK:TransferBookingRequest.STATUS.CANCELLED);
        rq.setBabies(getInt(l, cabecera, "Infants"));
        rq.setChildren(getInt(l, cabecera, "Children"));
        rq.setComments("");
        rq.setCreated("");
        rq.setCurrency("EUR");
        rq.setDepartureAddress(get(l, cabecera, "Origin Hotel Address"));
        rq.setDepartureAirport(get(l, cabecera, "Destination Departure IATA Code"));
        rq.setDepartureComments("");
        rq.setDepartureDestinationAirport(get(l, cabecera, "Destination Arrival IATA Code"));
        rq.setDepartureFlightCompany("");
        rq.setDepartureFlightDate(get(l, cabecera, "Destination Departure Flight Date", 0));
        rq.setDepartureFlightNumber(get(l, cabecera, "Destination Flight ID"));
        rq.setDepartureFlightTime(get(l, cabecera, "Destination Departure Flight Date", 1));
        rq.setDeparturePickupDate(get(l, cabecera, "Origin Hotel Pickup Date", 0));
        rq.setDeparturePickupTime(get(l, cabecera, "Origin Hotel Pickup Date", 1));
        rq.setDepartureResort(get(l, cabecera, "Origin Hotel Name"));
        rq.setDepartureStatus("RESERVED".equalsIgnoreCase(get(l, cabecera, "reservationStatus"))?TransferBookingRequest.STATUS.OK:TransferBookingRequest.STATUS.CANCELLED);
        rq.setEmail("");
        rq.setExtras(getInt(l, cabecera, "Number Bags"));
        rq.setPassengerName(get(l, cabecera, "CustomerName") + " " + get(l, cabecera, "CustomerSurname"));
        rq.setPhone("");
        rq.setServiceType("shared".equalsIgnoreCase(get(l, cabecera, "Transfer Type"))?TransferType.SHUTTLE:TransferType.PRIVATE);
        rq.setSource(Arrays.toString(l));
        TransferBookingRequest.TRANSFERSERVICES ts = TransferBookingRequest.TRANSFERSERVICES.BOTH;
        if (Strings.isNullOrEmpty(rq.getArrivalFlightDate())) ts = TransferBookingRequest.TRANSFERSERVICES.DEPARTURE;
        else if (Strings.isNullOrEmpty(rq.getDepartureFlightDate())) ts = TransferBookingRequest.TRANSFERSERVICES.ARRIVAL;
        rq.setTransferServices(ts);
        rq.setValue(getDouble(l, cabecera, "Total Rate", 0));
        rq.setVehicle("");
        rq.setWhen(LocalDateTime.now());
        rq.setVehicle("-");

        return rq;
    }

    private static int getInt(String[] l, Map<String, Integer> cabecera, String key) {
        return getInt(l, cabecera, key, 0);
    }
    private static int getInt(String[] l, Map<String, Integer> cabecera, String key, int token) {
        int v = 0;
        try {
            v = Integer.parseInt(get(l, cabecera, key, token));
        } catch (Exception e) {

        }
        return v;
    }

    private static double getDouble(String[] l, Map<String, Integer> cabecera, String key) {
        return getDouble(l, cabecera, key, 0);
    }
    private static double getDouble(String[] l, Map<String, Integer> cabecera, String key, int token) {
        double v = 0;
        try {
            v = Double.parseDouble(get(l, cabecera, key, token));
        } catch (Exception e) {

        }
        return v;
    }

    private static String get(String[] l, Map<String, Integer> cabecera, String key, int token) {
        String v = null;

        if (cabecera.containsKey(key) && l.length > cabecera.get(key)) {
            v = l[cabecera.get(key)];
            if (!Strings.isNullOrEmpty(v)) {
                String[] ts = v.split(" ");
                if (token < ts.length) v = ts[token];
                else v = null;
            }
        }

        if (v == null) return "";
        return v;
    }

    private static String get(String[] l, Map<String, Integer> cabecera, String key) {
        String v = null;

        if (cabecera.containsKey(key) && l.length > cabecera.get(key)) {
            v = l[cabecera.get(key)];
        }

        if (v == null) return "";
        return v;
    }







    public static void main(String[] args) {

        System.setProperty("appconf", "/home/miguel/work/quotravel.properties");

        EmailHelper.setTesting(true);

        run();

        WorkflowEngine.exit(0);

    }

    public static void run() {
        try {
            Helper.transact(em -> {

                ((List<TraveltinoImportTask>)em.createQuery("select x from " + TraveltinoImportTask.class.getName() + " x").getResultList()).forEach(t -> {
                    t.execute(em);
                });

            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }
}
