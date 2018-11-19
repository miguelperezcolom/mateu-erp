package io.mateu.erp.model.booking.transfer;

import com.google.common.base.Strings;
import io.mateu.erp.model.booking.PurchaseOrder;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.product.transfer.TransferPoint;
import io.mateu.erp.model.product.transfer.TransferType;
import org.jdom2.Document;
import org.jdom2.Element;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 20/5/17.
 */
public class IslandbusHelper {

    public static Document toPrivateXml(List<PurchaseOrder> pos) {
        Element root;
        Document doc = new Document(root = new Element("Partes"));
        int parte = 1;

        //DDMMYY
        DateTimeFormatter df = DateTimeFormatter.ofPattern("ddMMyy");
        DateTimeFormatter hf = DateTimeFormatter.ofPattern("HH:mm");


        for (PurchaseOrder po : pos) {

            List<TransferService> transfers = new ArrayList<>();
            for (Service s : po.getServices()) if (s instanceof TransferService && !TransferType.SHUTTLE.equals(((TransferService) s).getTransferType())) transfers.add((TransferService) s);

            if (transfers.size() > 0) {

                TransferService firstService = null;
                LocalDateTime firstTime = null;
                for (TransferService s : transfers) {
                    LocalDateTime time = null;
                    if (TransferDirection.INBOUND.equals(s.getDirection())) time = s.getFlightTime(); else time = s.getPickupTime();
                    if (firstService == null || (time != null && firstTime != null && firstTime.isAfter(time))) {
                        firstService  = s;
                        firstTime = time;
                    }
                }

                List<String> flightnos = new ArrayList<>();

                Element xmlParte = new Element("Parte" + parte++);
                root.addContent(xmlParte);

                //Parte1 NoParte="2896" Shuttle="si" Garaje="CENTRAL" Cliente="SHUTTLE" TTOO="URBIS" CodigoServicio="CUEVAS" Fecha="220411" Guia="" ZonaTrabajo="SUR" HoraInicio="20:35" LugarPresentacion="" Observaciones=""
                xmlParte.setAttribute("NoParte", "" + po.getId());

                xmlParte.setAttribute("TTOO","SHUPOINT");
                xmlParte.setAttribute("Cliente","VIAJESIBIZA");
                xmlParte.setAttribute("Garaje","IBIZATOURS");




                xmlParte.setAttribute("Shuttle","no");
                xmlParte.setAttribute("CodigoServicio",TransferDirection.INBOUND.equals(firstService.getDirection())?"ENTRADA":"SALIDA");
                xmlParte.setAttribute("Fecha", (firstTime != null)?firstTime.format(df):"");
                xmlParte.setAttribute("Guia","");
                xmlParte.setAttribute("ZonaTrabajo","");
                xmlParte.setAttribute("HoraInicio", (firstTime != null)?firstTime.format(hf):"");
                xmlParte.setAttribute("LugarPresentacion",TransferDirection.INBOUND.equals(firstService.getDirection())?"AEROPUERTO":"");

                //Añadimos las líneas y los Vuelos
                Element xmlLineas = new Element("Lineas");
                Element xmlVuelos = new Element("Vuelos");
                xmlParte.addContent(xmlLineas);
                xmlParte.addContent(xmlVuelos);

                int numBooking = 1;
                int numVuelo = 1;
                for (TransferService s : transfers) {

                    Element punto = new Element("Punto"+numBooking);
                    numBooking++;
                    xmlLineas.addContent(punto);

                    TransferPoint tp = TransferDirection.INBOUND.equals(firstService.getDirection())?s.getEffectiveDropoff():s.getEffectivePickup();

                    punto.setAttribute("PuntoRecogida", "" + tp.getId());
                    punto.setAttribute("NombrePunto", tp.getName());
                    LocalDateTime t = null;
                    if (TransferDirection.INBOUND.equals(firstService.getDirection())) t = s.getFlightTime();
                    else if (TransferDirection.INBOUND.equals(firstService.getDirection())) t = s.getPickupTime();
                    else if (TransferDirection.POINTTOPOINT.equals(firstService.getDirection())) t = (s.getPickupTime() != null)?s.getPickupTime():s.getFlightTime();
                    punto.setAttribute("Hora", (t != null)?t.format(hf):"");
                    punto.setAttribute("TTOO",s.getBooking().getAgency().getName());
                    punto.setAttribute("ZonaFisica",tp.getZone().getName());

                    punto.setAttribute("Adultos", ""+ s.getPax());
                    punto.setAttribute("Niños", ""+0);

                    punto.setAttribute("AdultosInvitados", ""+0);
                    punto.setAttribute("NiñosInvitados", ""+0);
                    punto.setAttribute("NombrePax", s.getBooking().getLeadName());
                    punto.setAttribute("localizador", "" + po.getId());
                    punto.setAttribute("Vuelos", formatFlight(s.getFlightNumber()));
                    punto.setAttribute("HoraVuelo", s.getFlightTime().format(hf));
                    String c = s.getBooking().getSpecialRequests();
                    if (!Strings.isNullOrEmpty(s.getOperationsComment())) {
                        if (c == null) c = "";
                        else if (!"".equals(c)) c += " / ";
                        c += s.getOperationsComment();
                    }
                    punto.setAttribute("Observaciones", "" + (TransferDirection.INBOUND.equals(firstService.getDirection())?"LLEGADA":"SALIDA") + ". " + ((c != null)?c:""));

                    //Vuelos
                    //<Vuelo1 Fecha="220411" RefVuelo="" Hora="" TTOO="JUMBOSH" FechaLlegadaVuelo="220411" EntSal="E"/>

                    //Los vuelos deben ser como máximo de 3 letras y 4 números
                    String flightno = formatFlight(s.getFlightNumber());
                    if (!flightnos.contains(flightno)){
                        flightnos.add(flightno);

                        Element vuelo = new Element("Vuelo"+numVuelo);
                        numVuelo++;
                        xmlVuelos.addContent(vuelo);

                        vuelo.setAttribute("Fecha", s.getFlightTime().format(df));
                        vuelo.setAttribute("RefVuelo",flightno);
                        vuelo.setAttribute("Hora",s.getFlightTime().format(hf));
                        vuelo.setAttribute("TTOO",s.getBooking().getAgency().getName());
                        vuelo.setAttribute("FechaLlegadaVuelo",s.getFlightTime().format(hf));
                        vuelo.setAttribute("EntSal",TransferDirection.INBOUND.equals(firstService.getDirection())?"E":"S");
                    }

                }

                xmlParte.setAttribute("Observaciones","");
            }


        }

        return doc;
    }



    public static Document toShuttleXml(List<PurchaseOrder> pos) {
        Element root;
        Document doc = new Document(root = new Element("traslados"));
        int parte = 1;

        //DDMMYY
        DateTimeFormatter df = DateTimeFormatter.ofPattern("DDMMYY");
        DateTimeFormatter dfx = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter hf = DateTimeFormatter.ofPattern("HH:mm");

        root.setAttribute("terminal", "AEROPUERTO IBIZA");

        for (PurchaseOrder po : pos) {

            List<TransferService> transfers = new ArrayList<>();
            for (Service s : po.getServices()) if (s instanceof TransferService && TransferType.SHUTTLE.equals(((TransferService) s).getTransferType())) transfers.add((TransferService) s);

            if (transfers.size() > 0) {

                TransferService firstService = null;
                LocalDateTime firstTime = null;
                for (TransferService s : transfers) {
                    LocalDateTime time = s.getFlightTime();
                    if (firstService == null || (time != null && firstTime != null && firstTime.isAfter(time))) {
                        firstService  = s;
                        firstTime = time;
                    }
                }
                if (firstTime != null) root.setAttribute("fecha", firstTime.format(dfx));


                for (TransferService s : transfers) {
                    Element shut = new Element("traslado");
                    root.addContent(shut);

                    String touroperador = s.getBooking().getAgency().getName();
                    touroperador = touroperador.replaceAll(" ","").toUpperCase();
                    if (touroperador.length() > 10) touroperador = touroperador.substring(0,10);

                    shut.addContent(new Element("codigoTTOO").setText(touroperador));
                    shut.addContent(new Element("nombreTTOO").setText(touroperador));
                    shut.addContent(new Element("numero").setText("" + po.getId()));
                    shut.addContent(new Element("tipo").setText(TransferDirection.INBOUND.equals(firstService.getDirection())?"ENTRADA":"SALIDA"));
                    shut.addContent(new Element("fecha").setText(s.getFlightTime().format(dfx)));
                    shut.addContent(new Element("vuelo").setText(formatFlight(s.getFlightNumber())));
                    shut.addContent(new Element("hora").setText(s.getFlightTime().format(hf)));
                    shut.addContent(new Element("nombre").setText(s.getBooking().getLeadName()));

                    shut.addContent(new Element("adultos").setText("" + s.getPax()));
                    shut.addContent(new Element("ninyos").setText("" + 0));
                    shut.addContent(new Element("bebes").setText("" + 0));


                    TransferPoint tp = TransferDirection.INBOUND.equals(firstService.getDirection())?s.getEffectiveDropoff():s.getEffectivePickup();

                    shut.addContent(new Element("codigoHotel").setText("" + tp.getId()));

                    String hotel = tp.getName().toUpperCase();
                    if (hotel.length() > 50) hotel = hotel.substring(0, 50);
                    shut.addContent(new Element("hotel").setText(hotel));

                    String routeN = tp.getZone().getName();
                    if (routeN.length() > 10) routeN = routeN.substring(0, 10);
                    shut.addContent(new Element("zona").setText(routeN));

                    String c = s.getBooking().getSpecialRequests();
                    if (!Strings.isNullOrEmpty(s.getOperationsComment())) {
                        if (c == null) c = "";
                        else if (!"".equals(c)) c += " / ";
                        c += s.getOperationsComment();
                    }

                    shut.addContent(new Element("observaciones").setText((c != null)?c:""));

                    shut.addContent(new Element("accion").setText("ALTA"));

                }

            }


        }

        return doc;
    }


    //Los vuelos deben ser como máximo de 3 letras y 4 números
    private static String formatFlight(String flightNumber) {
        String fn = "";
        if (flightNumber != null) {
            String s = flightNumber.replaceAll("[^a-zA-Z]", "");
            if (s.length() > 3) s = s.substring(0, 3);
            String n = flightNumber.replaceAll("[^\\d]", "");
            if (n.length() > 4) n = n.substring(0, 4);
            fn = s + n;
        }
        return fn;
    }

}
