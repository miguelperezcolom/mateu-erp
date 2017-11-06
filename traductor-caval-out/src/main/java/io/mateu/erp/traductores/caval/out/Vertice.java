package io.mateu.erp.traductores.caval.out;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Splitter;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Ints;
import io.vertx.ext.web.handler.BodyHandler;
import org.easytravelapi.common.*;
import org.easytravelapi.common.Amount;
import org.easytravelapi.hotel.*;
import travel.caval._20091127.commons.*;
import travel.caval._20091127.commons.Booking;
import travel.caval._20091127.commons.City;
import travel.caval._20091127.commons.Country;
import travel.caval._20091127.commons.State;
import travel.caval._20091127.hotelbooking.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.*;
import io.vertx.ext.web.client.*;
import io.vertx.ext.web.codec.BodyCodec;
import travel.caval._20091127.hotelbooking.BoardPrice;
import travel.caval._20091127.hotelbooking.CancellationCost;
import travel.caval._20091127.hotelbooking.ObjectFactory;
import travel.caval._20091127.hotelbooking.RoomOccupation;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;

public class Vertice extends AbstractVerticle {

    private static final QName SERVICE_NAME_HOTEL = new QName("http://caval.travel/20091127/hotelBooking", "HotelBookingService");
    private static final QName SERVICE_NAME_COMMONS = new QName("http://caval.travel/20091127/commons", "CommonsBookingService");


    HotelBookingService hotelPort;
    CommonsBookingService commonsPort;



    private WebClient client;
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");


    //todo: manejar excepciones


    @Override
    public void start() {

        hotelPort = new HotelBookingService_Service(HotelBookingService_Service.WSDL_LOCATION, SERVICE_NAME_HOTEL).getHotelBookingServicePort();

        // Use the BindingProvider's context to set the endpoint
        ((BindingProvider) hotelPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://xml.demo.busso.io/serveis/caval/20091127/soap/HotelBookingService");


        commonsPort = new CommonsBookingService_Service(CommonsBookingService_Service.WSDL_LOCATION, SERVICE_NAME_COMMONS).getCommonsBookingServicePort();

        // Use the BindingProvider's context to set the endpoint
        ((BindingProvider) commonsPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://xml.demo.busso.io/serveis/caval/20091127/soap/CommonsBookingService");




        client = WebClient.create(vertx);
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());

        router.get("/").handler(this::hola);




        //commons
        router.get("/:authtoken/commons/portfolio").handler(this::portfolio);
        router.delete("/:authtoken/commons/booking/:id").handler(this::cancel);
        router.get("/:authtoken/commons/datasheet/:id").handler(this::dataSheet);
        router.get("/:authtoken/commons/bookings").handler(this::bookings);



        //hotel
        router.get("/:authtoken/hotel/available").handler(this::availableHotels);
        router.get("/:authtoken/hotel/pricedetails/:key").handler(this::hotelPriceDetails);
        router.put("/:authtoken/hotel/booking").handler(this::bookHotel);




        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(Integer.parseInt(System.getProperty("puerto", "8081")));

    }

    private void bookings(RoutingContext rc) {

        GetListOfBookings parametros = new GetListOfBookings();
        CavalGetListOfBookingsRQ rq;
        parametros.setRq(rq = new CavalGetListOfBookingsRQ());

        try {
            Credenciales creds = new Credenciales(new String(BaseEncoding.base64().decode(rc.request().getParam("authtoken"))));
            rq.setAgentId(creds.getAgentId());
            rq.setLanguage(creds.getLan());
            rq.setLogin(creds.getLogin());
            rq.setPassword(creds.getPass());
        } catch (IOException e) {
            e.printStackTrace();
        }

        rq.setRqId("1");

        if (rc.request().getParam("confirmedfrom") != null) rq.setFromFormalizationDate(Helper.toDate(Integer.parseInt(rc.request().getParam("confirmedfrom"))).format(dtf));
        if (rc.request().getParam("confirmedto") != null) rq.setToFormalizationDate(Helper.toDate(Integer.parseInt(rc.request().getParam("confirmedto"))).format(dtf));
        //if (rc.request().getParam("confirmedfrom") != null) rq.setFromLastModificationDate(Helper.toDate(Integer.parseInt(rc.request().getParam("confirmedfrom"))).format(dtf));
        //if (rc.request().getParam("confirmedfrom") != null) rq.setToLastModificationDate(Helper.toDate(Integer.parseInt(rc.request().getParam("confirmedfrom"))).format(dtf));
        if (rc.request().getParam("startingfrom") != null) rq.setFromStartOfServicesDate(Helper.toDate(Integer.parseInt(rc.request().getParam("startingfrom"))).format(dtf));
        if (rc.request().getParam("startingto") != null) rq.setToStartOfServicesDate(Helper.toDate(Integer.parseInt(rc.request().getParam("startingto"))).format(dtf));



        commonsPort.getListOfBookingsAsync(parametros, res -> {

            CavalGetListOfBookingsRS r = null;
            try {
                r = res.get().getReturn();


                GetBookingsRS rs = new GetBookingsRS();

                rs.setStatusCode(r.getResultCode());
                rs.setMsg(r.getMessage());

                for (Booking b : r.getBookings()) {
                    org.easytravelapi.common.Booking x;
                    rs.getBookings().add(x = new org.easytravelapi.common.Booking());

                    x.setStatus(b.getStatus());
                    x.setServiceDescription(b.getDescription());
                    x.setServiceType("");
                    if (b.getNetPrice() != null) x.setNetValue(new Amount(b.getNetPrice().getCurrencyCode(), b.getNetPrice().getValue()));
                    x.setBookingId(b.getLocator());
                    x.setEnd(b.getToDate());
                    x.setStart(b.getFromDate());
                    x.setLeadName(b.getTitular());
                    x.setModified(null);
                    x.setCreatedBy(null);
                    x.setCreated(b.getFormalizationDate());
                    x.setCommentsToProvider(null);
                    x.setCommissionValue(null);
                    x.setPrivateComments(b.getYourReference());

                }

                rc.response().end("" + Helper.toJson(rs));


            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        });

    }

    private void dataSheet(RoutingContext rc) {

        GetEstablishmentDataSheets parametros = new GetEstablishmentDataSheets();
        CavalGetEstablishmentDataSheetsRQ rq;
        parametros.setRq(rq = new CavalGetEstablishmentDataSheetsRQ());

        try {
            Credenciales creds = new Credenciales(new String(BaseEncoding.base64().decode(rc.request().getParam("authtoken"))));
            rq.setAgentId(creds.getAgentId());
            rq.setLanguage(creds.getLan());
            rq.setLogin(creds.getLogin());
            rq.setPassword(creds.getPass());
        } catch (IOException e) {
            e.printStackTrace();
        }

        rq.setRqId("1");

        rq.getEstablishmentIds().add(rc.request().getParam("id").substring("hot_".length()));



        hotelPort.getEstablishmentDataSheetsAsync(parametros, res -> {

            CavalGetEstablishmentDataSheetsRS r = null;
            try {
                r = res.get().getReturn();


                GetDataSheetRS rs = new GetDataSheetRS();

                rs.setStatusCode(r.getResultCode());
                rs.setMsg(r.getMessage());

                EstablishmentDataSheet d = r.getDataSheets().get(0);

                rs.getValues().add(new Pair("/name", d.getName()));
                rs.getValues().add(new Pair("/address", d.getAddress()));
                rs.getValues().add(new Pair("/category/code", d.getCategoryCode()));
                rs.getValues().add(new Pair("/category/name", d.getCategoryName()));
                rs.getValues().add(new Pair("/fax", d.getFax()));
                rs.getValues().add(new Pair("/lat", d.getGoogleLatitude()));
                rs.getValues().add(new Pair("/lon", d.getGoogleLongitude()));
                rs.getValues().add(new Pair("/description/long", d.getLongDescription()));
                rs.getValues().add(new Pair("/description/short", d.getShortDescription()));
                rs.getValues().add(new Pair("/telephone", d.getTelephone()));
                rs.getValues().add(new Pair("/image", d.getMainImageUrl()));
                rs.getValues().add(new Pair("/status", d.getStatus()));
                rs.getValues().add(new Pair("/zip", d.getZipCode()));
                rs.getValues().add(new Pair("/country/code", d.getCountryCode()));
                rs.getValues().add(new Pair("/country/name", d.getCountryName()));
                rs.getValues().add(new Pair("/state/id", d.getStateId()));
                rs.getValues().add(new Pair("/state/name", d.getStateName()));
                rs.getValues().add(new Pair("/city/id", d.getCityId()));
                rs.getValues().add(new Pair("/city/name", d.getCityName()));
                int pos = 0;
                for (String s : d.getOtherImagesUrls()) {
                    rs.getValues().add(new Pair("/images/" + pos++, s));
                }
                pos = 0;
                for (EstablishmentRemark s : d.getRemarks()) {
                    rs.getValues().add(new Pair("/remarks/" + pos + "/description", s.getDescription()));
                    rs.getValues().add(new Pair("/remarks/" + pos + "/from", s.getFromDate()));
                    rs.getValues().add(new Pair("/remarks/" + pos++ + "/to", s.getToDate()));
                }


                rc.response().end("" + Helper.toJson(rs));


            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        });

    }

    private void cancel(RoutingContext rc) {
        CancelBooking parametros = new CancelBooking();
        CavalCancelBookingRQ rq;
        parametros.setRq(rq = new CavalCancelBookingRQ());

        try {
            Credenciales creds = new Credenciales(new String(BaseEncoding.base64().decode(rc.request().getParam("authtoken"))));
            rq.setAgentId(creds.getAgentId());
            rq.setLanguage(creds.getLan());
            rq.setLogin(creds.getLogin());
            rq.setPassword(creds.getPass());
        } catch (IOException e) {
            e.printStackTrace();
        }

        rq.setRqId("1");

        rq.setLocator(rc.request().getParam("id"));



        commonsPort.cancelBookingAsync(parametros, res -> {

            CavalCancelBookingRS r = null;
            try {
                r = res.get().getReturn();


                CancelBookingRS rs = new CancelBookingRS();

                rs.setStatusCode(r.getResultCode());
                rs.setMsg(r.getMessage());


                rc.response().end("" + Helper.toJson(rs));


            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        });

    }

    private void portfolio(RoutingContext rc) {

        GetWholeSupportedMap parametros = new GetWholeSupportedMap();
        CavalGetWholeSupportedMapRQ rq;
        parametros.setRq(rq = new CavalGetWholeSupportedMapRQ());

        try {
            Credenciales creds = new Credenciales(new String(BaseEncoding.base64().decode(rc.request().getParam("authtoken"))));
            rq.setAgentId(creds.getAgentId());
            rq.setLanguage(creds.getLan());
            rq.setLogin(creds.getLogin());
            rq.setPassword(creds.getPass());
        } catch (IOException e) {
            e.printStackTrace();
        }

        rq.setRqId("1");

        rq.setPropietaryCodes(true);



        commonsPort.getWholeSupportedMapAsync(parametros, res -> {

            CavalGetWholeSupportedMapRS r = null;
            try {
                r = res.get().getReturn();


                GetPortfolioRS rs = new GetPortfolioRS();

                rs.setStatusCode(r.getResultCode());
                rs.setMsg(r.getMessage());

                for (Country c : r.getCountries()) {
                    org.easytravelapi.common.Country xc;
                    rs.getCountries().add(xc = new org.easytravelapi.common.Country());

                    xc.setResourceId("cou_" + c.getId());
                    xc.setName(c.getName());
                    xc.setUrlFriendlyName("");

                    for (State s : c.getStates()) {
                        org.easytravelapi.common.State xs;
                        xc.getStates().add(xs = new org.easytravelapi.common.State());

                        xs.setResourceId("stt_" + s.getId());
                        xs.setName(s.getName());
                        xs.setUrlFriendlyName("");


                        for (City l : s.getCities()) {
                            org.easytravelapi.common.City xl;
                            xs.getCities().add(xl = new org.easytravelapi.common.City());

                            xl.setResourceId("cty_" + s.getId());
                            xl.setName(l.getName());
                            xl.setUrlFriendlyName("");


                            for (Hotel h : l.getHotels()) {
                                org.easytravelapi.common.Resource xh;
                                xl.getResources().add(xh = new org.easytravelapi.common.Resource());

                                xh.setResourceId("hot_" + s.getId());
                                xh.setName(h.getName());
                                xh.setType("HOTEL");
                                xh.setDescription(null);
                                xh.setLatitude(null);
                                xh.setLongitude(null);
                            }
                        }

                    }

                }


                rc.response().end("" + Helper.toJson(rs));


            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        });
    }

    private void bookHotel(RoutingContext rc) {

        try {
            BookHotelRQ b = Helper.fromJson(rc.getBodyAsString(), BookHotelRQ.class);


            ConfirmHotelBooking parametros = new ConfirmHotelBooking();
            CavalHotelBookingConfirmRQ rq;
            parametros.setRq(rq = new CavalHotelBookingConfirmRQ());

            try {
                Credenciales creds = new Credenciales(new String(BaseEncoding.base64().decode(rc.request().getParam("authtoken"))));
                rq.setAgentId(creds.getAgentId());
                rq.setLanguage(creds.getLan());
                rq.setLogin(creds.getLogin());
                rq.setPassword(creds.getPass());
            } catch (IOException e) {
                e.printStackTrace();
            }

            rq.setRqId("1");

            rq.setKey(new String(BaseEncoding.base64().decode(b.getKey())));

            ObjectFactory f = new ObjectFactory();

            rq.getRest().add(f.createCavalHotelBookingConfirmRQAgencyReference(b.getBookingReference()));
            rq.getRest().add(f.createCavalHotelBookingConfirmRQAgencyEmail(""));
            rq.getRest().add(f.createCavalHotelBookingConfirmRQTitular(b.getLeadName()));
            rq.getRest().add(f.createCavalHotelBookingConfirmRQCommentForHotel(b.getCommentsToProvider()));
            rq.getRest().add(f.createCavalHotelBookingConfirmRQCommentForBookingDept(b.getPrivateComments()));


            hotelPort.confirmHotelBookingAsync(parametros, res -> {

                CavalHotelBookingConfirmRS r = null;
                try {
                    r = res.get().getReturn();


                    BookHotelRS rs = new BookHotelRS();

                    rs.setStatusCode(r.getResultCode());
                    rs.setMsg(r.getMessage());

                    rs.setBookingId(r.getLocator());

                    rc.response().end("" + Helper.toJson(rs));


                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void hotelPriceDetails(RoutingContext rc) {

        GetDetailedValuation parametros = new GetDetailedValuation();
        CavalHotelBookingValuationRQ rq;
        parametros.setRq(rq = new CavalHotelBookingValuationRQ());

        try {
            Credenciales creds = new Credenciales(new String(BaseEncoding.base64().decode(rc.request().getParam("authtoken"))));
            rq.setAgentId(creds.getAgentId());
            rq.setLanguage(creds.getLan());
            rq.setLogin(creds.getLogin());
            rq.setPassword(creds.getPass());
        } catch (IOException e) {
            e.printStackTrace();
        }

        rq.setRqId("1");

        rq.setKey(new String(BaseEncoding.base64().decode(rc.request().getParam("key"))));



        hotelPort.getDetailedValuationAsync(parametros, res -> {

            CavalHotelBookingValuationRS r = null;
            try {
                r = res.get().getReturn();


                GetHotelPriceDetailsRS rs = new GetHotelPriceDetailsRS();

                rs.setStatusCode(r.getResultCode());
                rs.setMsg(r.getMessage());

                for (CancellationCost c : r.getCancellationCosts()) {
                    org.easytravelapi.common.CancellationCost x;
                    rs.getCancellationCosts().add(x = new org.easytravelapi.common.CancellationCost());
                    x.setGMTtime(c.getFrom());
                    Amount a;
                    x.setNet(a = new Amount());
                    a.setCurrencyIsoCode(c.getNetPrice().getCurrencyCode());
                    a.setValue(c.getNetPrice().getValue());
                    x.setCommission(null);
                    x.setRetail(null);
                }

                for (String s : r.getRemarks()) {
                    Remark x;
                    rs.getRemarks().add(x = new Remark());
                    x.setText(s);
                    x.setText("WARNING");
                }

                rc.response().end("" + Helper.toJson(rs));


            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        });

    }

    private void availableHotels(RoutingContext rc) {

        GetAvailableHotels parametros = new GetAvailableHotels();
        CavalHotelAvailabilityRQ rq;
        parametros.setRq(rq = new CavalHotelAvailabilityRQ());

        rq.setCheckIn(Helper.toDate(Integer.parseInt(rc.request().getParam("checkin"))).format(dtf));
        rq.setCheckOut(Helper.toDate(Integer.parseInt(rc.request().getParam("checkout"))).format(dtf));
        //rq.setCoverageArea(new CavalHotelAvailabilityRQ.CoverageArea());
        //rq.setGuestCountryCode();
        rq.setIncludeCancellationCostsAndRemarks(false);
        rq.setFromRow(0);
        rq.setNumRows(30);
        rq.setRemoveHotelInfo(true);

        try {
            Credenciales creds = new Credenciales(new String(BaseEncoding.base64().decode(rc.request().getParam("authtoken"))));
            rq.setAgentId(creds.getAgentId());
            rq.setLanguage(creds.getLan());
            rq.setLogin(creds.getLogin());
            rq.setPassword(creds.getPass());
        } catch (IOException e) {
            e.printStackTrace();
        }

        rq.setRqId("1");

        for (String s : Splitter.on(',')
                .trimResults()
                .omitEmptyStrings()
                .split(rc.request().getParam("resorts"))) rq.getAirportIds().add(s);

        for (String s : Splitter.on(',')
                .trimResults()
                .omitEmptyStrings()
                .split(rc.request().getParam("occupancies").toLowerCase())) {

            //1x4-4-8

            System.out.println("occ=" + s);

            AvailRQOccupation o;
            rq.getOccupations().add(o = new AvailRQOccupation());
            o.setAdultsPerRoom(Integer.parseInt(s.split("x")[1].split("-")[0]));
            o.setNumberOfRooms(Integer.parseInt(s.split("x")[0]));

            String[] sas = s.split("-");
            if (sas.length > 1) {
                for (int i = 1; i < sas.length; i++) {
                    int edad = Integer.parseInt(sas[i]);
                    if (edad < 20) {
                        o.getChildAges().add(edad);
                    }
                }
            }

            o.setChildrenPerRoom(o.getChildAges().size() / o.getNumberOfRooms());
            o.setAdultsPerRoom(o.getAdultsPerRoom() - o.getChildrenPerRoom());

        }

        long t0 = System.currentTimeMillis();


        hotelPort.getAvailableHotelsAsync(parametros, res -> {

            try {


                CavalHotelAvailabilityRS r = res.get().getReturn();

                GetAvailableHotelsRS rs = new GetAvailableHotelsRS();

                long t = System.currentTimeMillis();

                rs.setStatusCode(r.getResultCode());
                rs.setMsg(((r.getMessage() != null)?r.getMessage():"") + ". Took " + (t - t0) + " ms.");

                for (AvailableEstablishment e : r.getAvailableEstablishments()) {
                    AvailableHotel h;
                    rs.getHotels().add(h = new AvailableHotel());
                    h.setHotelId(e.getEstablishmentId());
                    h.setHotelName(e.getEstablishmentName());
                    h.setLatitude(e.getGoogleLatitude());
                    h.setLongitude(e.getGoogleLongitude());
                    h.setHotelCategoryId(e.getCategoryId());
                    h.setHotelCategoryName(e.getCategoryName());

                    for (CombinationPrice cp : e.getPrices()) {
                        Option o;
                        h.getOptions().add(o = new Option());

                        StringBuffer sb = new StringBuffer();

                        int pos = 0;
                        for (RoomOccupation ro : cp.getRooms()) {
                            Allocation a;
                            o.getDistribution().add(a = new Allocation());
                            a.setRoomId(ro.getRoomCode());
                            a.setRoomName(ro.getRoomName());
                            a.setNumberOfRooms(ro.getNumberOfRooms());
                            a.setPaxPerRoom(ro.getAdultsPerRoom() + ro.getChildrenPerRoom());
                            a.setAges(Ints.toArray(ro.getChildAges()));

                            if (pos > 0) sb.append(" and ");
                            sb.append(a.getNumberOfRooms() * a.getPaxPerRoom());
                            sb.append(" pax in ");
                            sb.append(a.getNumberOfRooms());
                            sb.append(" ");
                            sb.append(a.getRoomName());
                        }
                        o.setDistributionString(sb.toString());

                        for (BoardPrice bp : cp.getBoardPrices()) {
                            org.easytravelapi.hotel.BoardPrice p;
                            o.getPrices().add(p = new org.easytravelapi.hotel.BoardPrice());

                            p.setKey(BaseEncoding.base64().encode(bp.getKey().getBytes()));
                            p.setBoardBasisId(bp.getBoardCode());
                            p.setBoardBasisName(bp.getBoardName());
                            //p.setOnRequest(bp.);
                            p.setOnRequestText(null);
                            p.setOffer(bp.isOffer());
                            p.setOfferText(bp.getOfferDescription());
                            p.setNonRefundable(false);
                            p.setRetailPrice(null);
                            p.setCommission(null);
                            Amount n;
                            p.setNetPrice(n = new Amount());
                            n.setValue(bp.getNetPrice().getValue());
                            n.setCurrencyIsoCode(bp.getNetPrice().getCurrencyCode());

                        }

                    }


                }

                rc.response().end("" + Helper.toJson(rs));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        });

    }

    private void hola(RoutingContext rc) {
        rc.response().end("Hola!");
    }

    private void invokeMyFirstMicroservice(RoutingContext rc) {

        HttpRequest<JsonObject> request = client
                .get(8080, "localhost","/vert.x")
                .as(BodyCodec.jsonObject());

        request.send(ar -> { if (ar.failed()) {
            rc.fail(ar.cause()); }else{
            rc.response().end(ar.result().body().encode());
        }

        });

    }
}