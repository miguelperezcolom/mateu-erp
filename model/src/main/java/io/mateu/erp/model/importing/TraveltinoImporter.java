package io.mateu.erp.model.importing;

import com.google.common.base.Strings;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.product.transfer.TransferType;

import javax.persistence.EntityManager;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TraveltinoImporter {

    public static void process(EntityManager em, String[][] csv, PrintWriter pw) {

        Partner traveltino = (Partner) em.createQuery("select x from " + Partner.class.getName() + " x where upper(x.name) = 'TRAVELTINO'").getResultList().get(0);

        Map<String, Integer> cabecera = new HashMap<>();
        boolean cabeceraEncontrada = false;
        for (String[] l : csv) {

            if (!cabeceraEncontrada) {
                if (l.length > 0) {
                    for (int i = 0; i < l.length; i++) if (!Strings.isNullOrEmpty(l[i])) cabecera.put(l[i].trim(), i);
                    cabeceraEncontrada = cabecera.size() > 0;
                }
            } else {

                if (l.length > 0) {
                    int celdasConContenido = 0;
                    for (int i = 0; i < l.length; i++) if (!Strings.isNullOrEmpty(l[i])) celdasConContenido++;

                    if (celdasConContenido > 6) {

                        try {

                            TransferBookingRequest rq = rellenarRq(em, l, cabecera, traveltino);

                            TransferBookingRequest last = TransferBookingRequest.getByAgencyRef(em, rq.getAgencyReference(), traveltino);
                            if (last == null || !last.getSignature().equals(rq.getSignature())) {
                                em.persist(rq);
                            }

                        } catch (Throwable t) {
                            t.printStackTrace();
                        }

                    }

                }

            }

        }



    }

    private static TransferBookingRequest rellenarRq(EntityManager em, String[] l, Map<String, Integer> cabecera, Partner agencia) {
        TransferBookingRequest rq = new TransferBookingRequest();

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
}
