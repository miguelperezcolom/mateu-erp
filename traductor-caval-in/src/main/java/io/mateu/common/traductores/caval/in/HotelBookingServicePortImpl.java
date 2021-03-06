
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package io.mateu.common.traductores.caval.in;

import com.google.common.base.Strings;
import org.easytravelapi.hotel.*;
import travel.caval._20091127.hotelbooking.*;

import javax.jws.WebService;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.Date;
import java.util.logging.Logger;

/**
 * This class was generated by Apache CXF 3.2.0
 * 2017-11-23T11:12:28.082+01:00
 * Generated source version: 3.2.0
 * 
 */

@WebService(
                      serviceName = "HotelBookingService",
                      portName = "HotelBookingServicePort",
                      targetNamespace = "http://caval.travel/20091127/hotelBooking",
                      wsdlLocation = "caval/HotelBookingService.wsdl",
                      endpointInterface = "travel.caval._20091127.hotelbooking.HotelBookingService")

public class HotelBookingServicePortImpl implements HotelBookingService {

    private static final Logger LOG = Logger.getLogger(travel.caval._20091127.hotelbooking.HotelBookingServicePortImpl.class.getName());

    private static Client client = ClientBuilder.newClient();

    /* (non-Javadoc)
     * @see travel.caval._20091127.hotelbooking.HotelBookingService#getAvailableHotels(travel.caval._20091127.hotelbooking.GetAvailableHotels parameters)*
     */
    public travel.caval._20091127.hotelbooking.GetAvailableHotelsResponse getAvailableHotels(GetAvailableHotels parameters) {
        LOG.info("Executing operation getAvailableHotels");
        System.out.println(parameters);
        try {


            //http://test.easytravelapi.com/rest/yourauthtoken/hotel/available?resorts=PMI&checkin=20180601&checkout=20180615&occupancies=1x4(4-8)&occupancies=1X2


            WebTarget t = client.target("http://test.easytravelapi.com/rest/" + parameters.getRq().getAgentId() + "/hotel/available");
            t = t.queryParam("resorts", "");
            t = t.queryParam("checkin", "");
            t = t.queryParam("checkout", "");
            t = t.queryParam("occupancies", "");

            GetAvailableHotelsRS rs = t.request("application/json")
                    .get(GetAvailableHotelsRS.class);


            travel.caval._20091127.hotelbooking.GetAvailableHotelsResponse _return = new GetAvailableHotelsResponse();
            CavalHotelAvailabilityRS v = new CavalHotelAvailabilityRS();
            _return.setReturn(v);
            v.setCpuTime("");
            v.setDateAtServer(rs.getSystemTime());
            v.setMessage(rs.getMsg());
            v.setResultCode(rs.getStatusCode());

            int numhoteles = 0;

            for (AvailableHotel h : rs.getHotels()) if (!"notavailable".equalsIgnoreCase(h.getBestDeal())) {
                AvailableEstablishment e;
                v.getAvailableEstablishments().add(e = new AvailableEstablishment());

                numhoteles++;

                e.setEstablishmentId(h.getHotelId());
                e.setEstablishmentName(h.getHotelName());
                e.setCityId("");
                e.setCityName("");
                e.setStateId("");
                e.setStateName("");
                e.setCountryId("");
                e.setCountryName("");
                e.setCategoryId(h.getHotelCategoryId());
                e.setCategoryName(h.getHotelCategoryName());
                e.setCategoryGroupId("");
                e.setDescription("");
                e.setGoogleLongitude(h.getLongitude());
                e.setGoogleLatitude(h.getLatitude());
                e.setImageUrl("");

                for (Option o : h.getOptions()) {
                    CombinationPrice cp;
                    e.getPrices().add(cp = new CombinationPrice());

                    for (Allocation a : o.getDistribution()) {
                        RoomOccupation r;
                        cp.getRooms().add(r = new RoomOccupation());

                        r.setRoomCode(a.getRoomId());
                        r.setRoomName(a.getRoomName());
                        r.setNumberOfRooms(a.getNumberOfRooms());
                        r.setAdultsPerRoom(a.getPaxPerRoom());
                        r.setChildrenPerRoom(0);
                        if (a.getAges() != null) for (int age : a.getAges()) r.getChildAges().add(age);
                        r.setStatus("");
                    }

                    for (org.easytravelapi.hotel.BoardPrice p : o.getPrices()) {
                        travel.caval._20091127.hotelbooking.BoardPrice xp;
                        cp.getBoardPrices().add(xp = new travel.caval._20091127.hotelbooking.BoardPrice());

                        //ojo!!!!
                        cp.setNonRefundable(p.isNonRefundable());
                        cp.setOnRequest(p.isOnRequest());

                        xp.setBoardCategoryId("");
                        xp.setBoardCode(p.getBoardBasisId());
                        xp.setBoardName(p.getBoardBasisName());
                        if (p.getRetailPrice() != null) {
                            Amount x;
                            xp.setGrossPrice(x = new Amount());
                            x.setCurrencyCode(p.getRetailPrice().getCurrencyIsoCode());
                            x.setValue(p.getRetailPrice().getValue());
                        }
                        xp.setKey(p.getKey());
                        if (p.getNetPrice() != null) {
                            Amount x;
                            xp.setNetPrice(x = new Amount());
                            x.setCurrencyCode(p.getNetPrice().getCurrencyIsoCode());
                            x.setValue(p.getNetPrice().getValue());
                        }
                        xp.setOffer(p.isOffer());
                        xp.setOfferDescription(p.getOfferText());
                        xp.setOldGrossPrice(null);
                        xp.setOldNetPrice(null);
                    }

                    //todo: completar
                    /*
                    CancellationCost cc;
                    cp.getCancellationCosts().add(cc = new CancellationCost());
                    cp.getRemarks().add();
                    */


                }


            }

            v.setFromRow(0);
            v.setNumRows(numhoteles);
            v.setStatsKey("");
            v.setTotalRows(numhoteles);


            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see travel.caval._20091127.hotelbooking.HotelBookingService#getDetailedValuation(travel.caval._20091127.hotelbooking.GetDetailedValuation parameters)*
     */
    public travel.caval._20091127.hotelbooking.GetDetailedValuationResponse getDetailedValuation(GetDetailedValuation parameters) {
        LOG.info("Executing operation getDetailedValuation");
        System.out.println(parameters);
        try {

            String key = parameters.getRq().getKey();
            if (Strings.isNullOrEmpty(key)) {
                //todo: crear key a aprtir del rq de caval
            }

            WebTarget t = client.target("http://test.easytravelapi.com/rest/" + parameters.getRq().getAgentId() + "/hotel/pricedetails/" + key);

            GetHotelPriceDetailsRS rs = t.request("application/json")
                    .get(GetHotelPriceDetailsRS.class);


            travel.caval._20091127.hotelbooking.GetDetailedValuationResponse _return = new GetDetailedValuationResponse();
            CavalHotelBookingValuationRS v = new CavalHotelBookingValuationRS();
            _return.setReturn(v);
            v.setCpuTime("");
            v.setDateAtServer(rs.getSystemTime());
            v.setMessage(rs.getMsg());
            v.setResultCode(rs.getStatusCode());

            /*
            v.setBoardCode();
            v.setBoardName();
            v.setCheckin();
            v.setCheckout();
            v.setEstablishmentAddress();
            v.setEstablishmentCategory();
            v.setEstablishmentCity();
            v.setEstablishmentCountry();
            v.setEstablishmentDescription();
            v.setEstablishmentId();
            v.setEstablishmentImageUrl();
            v.setEstablishmentName();
            v.setEstablishmentZip();
            v.setGrossPrice();
            v.setKey();
            v.setNetPrice();
            v.setOffer();
            v.setOfferDescription();
            v.setStatsKey();
            v.setStatus();
            */

            v.getRemarks();

            v.getCancellationCosts();

            v.getAvailableSupplements();

            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see travel.caval._20091127.hotelbooking.HotelBookingService#getOffersList(travel.caval._20091127.hotelbooking.GetOffersList parameters)*
     */
    public travel.caval._20091127.hotelbooking.GetOffersListResponse getOffersList(GetOffersList parameters) {
        LOG.info("Executing operation getOffersList");
        System.out.println(parameters);
        try {
            travel.caval._20091127.hotelbooking.GetOffersListResponse _return = new GetOffersListResponse();

            //este método no lo implementamos

            CavalGetOffersListRS v = new CavalGetOffersListRS();
            _return.setReturn(v);
            v.setCpuTime("");
            v.setDateAtServer("" + new Date());
            v.setMessage("Sorry. This method is not implemented");
            v.setResultCode(404);

            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see travel.caval._20091127.hotelbooking.HotelBookingService#confirmHotelBooking(travel.caval._20091127.hotelbooking.ConfirmHotelBooking parameters)*
     */
    public travel.caval._20091127.hotelbooking.ConfirmHotelBookingResponse confirmHotelBooking(ConfirmHotelBooking parameters) {
        LOG.info("Executing operation confirmHotelBooking");
        System.out.println(parameters);
        try {
            travel.caval._20091127.hotelbooking.ConfirmHotelBookingResponse _return = null;
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see travel.caval._20091127.hotelbooking.HotelBookingService#getListOfBoardTypes(travel.caval._20091127.hotelbooking.GetListOfBoardTypes parameters)*
     */
    public travel.caval._20091127.hotelbooking.GetListOfBoardTypesResponse getListOfBoardTypes(GetListOfBoardTypes parameters) {
        LOG.info("Executing operation getListOfBoardTypes");
        System.out.println(parameters);
        try {
            travel.caval._20091127.hotelbooking.GetListOfBoardTypesResponse _return = new GetListOfBoardTypesResponse();
            CavalGetListOfBoardTypesRS v = new CavalGetListOfBoardTypesRS();
            _return.setReturn(v);
            v.setCpuTime("");
            v.setDateAtServer("" + new Date());
            v.setMessage("");
            v.setResultCode(200);

            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see travel.caval._20091127.hotelbooking.HotelBookingService#getEstablishmentDataSheets(travel.caval._20091127.hotelbooking.GetEstablishmentDataSheets parameters)*
     */
    public travel.caval._20091127.hotelbooking.GetEstablishmentDataSheetsResponse getEstablishmentDataSheets(GetEstablishmentDataSheets parameters) {
        LOG.info("Executing operation getEstablishmentDataSheets");
        System.out.println(parameters);
        try {
            travel.caval._20091127.hotelbooking.GetEstablishmentDataSheetsResponse _return = new GetEstablishmentDataSheetsResponse();
            CavalGetEstablishmentDataSheetsRS v = new CavalGetEstablishmentDataSheetsRS();
            _return.setReturn(v);
            v.setCpuTime("");
            v.setDateAtServer("" + new Date());
            v.setMessage("");
            v.setResultCode(200);

            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see travel.caval._20091127.hotelbooking.HotelBookingService#notifyHotelBookings(travel.caval._20091127.hotelbooking.NotifyHotelBookings parameters)*
     */
    public travel.caval._20091127.hotelbooking.NotifyHotelBookingsResponse notifyHotelBookings(NotifyHotelBookings parameters) {
        LOG.info("Executing operation notifyHotelBookings");
        System.out.println(parameters);
        try {
            travel.caval._20091127.hotelbooking.NotifyHotelBookingsResponse _return = new NotifyHotelBookingsResponse();
            CavalHotelBookingNotificationRS v = new CavalHotelBookingNotificationRS();
            _return.setReturn(v);
            v.setCpuTime("");
            v.setDateAtServer("" + new Date());
            v.setMessage("Sorry. This method is not implemented");
            v.setResultCode(404);
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
}
