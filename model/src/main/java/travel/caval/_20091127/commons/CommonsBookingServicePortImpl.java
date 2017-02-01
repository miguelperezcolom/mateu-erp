
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package travel.caval._20091127.commons;

import java.util.logging.Logger;

/**
 * This class was generated by Apache CXF 3.1.7
 * 2016-10-01T19:27:28.778+02:00
 * Generated source version: 3.1.7
 * 
 */

@javax.jws.WebService(
                      serviceName = "CommonsBookingService",
                      portName = "CommonsBookingServicePort",
                      targetNamespace = "http://caval.travel/20091127/commons",
                      wsdlLocation = "http://caval.travel/tech_specs/wsdls/CommonsBookingService.wsdl",
                      endpointInterface = "travel.caval._20091127.commons.CommonsBookingService")
                      
public class CommonsBookingServicePortImpl implements CommonsBookingService {

    private static final Logger LOG = Logger.getLogger(CommonsBookingServicePortImpl.class.getName());

    /* (non-Javadoc)
     * @see travel.caval._20091127.commons.CommonsBookingService#getBooking(travel.caval._20091127.commons.CavalGetBookingRQ rq)*
     */
    public travel.caval._20091127.commons.CavalGetBookingRS getBooking(travel.caval._20091127.commons.CavalGetBookingRQ rq) { 
        LOG.info("Executing operation getBooking");
        System.out.println(rq);
        try {
            travel.caval._20091127.commons.CavalGetBookingRS _return = null;
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see travel.caval._20091127.commons.CommonsBookingService#cancelBooking(travel.caval._20091127.commons.CavalCancelBookingRQ rq)*
     */
    public travel.caval._20091127.commons.CavalCancelBookingRS cancelBooking(travel.caval._20091127.commons.CavalCancelBookingRQ rq) { 
        LOG.info("Executing operation cancelBooking");
        System.out.println(rq);
        try {
            travel.caval._20091127.commons.CavalCancelBookingRS _return = null;
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see travel.caval._20091127.commons.CommonsBookingService#getListOfBookings(travel.caval._20091127.commons.CavalGetListOfBookingsRQ rq)*
     */
    public travel.caval._20091127.commons.CavalGetListOfBookingsRS getListOfBookings(travel.caval._20091127.commons.CavalGetListOfBookingsRQ rq) { 
        LOG.info("Executing operation getListOfBookings");
        System.out.println(rq);
        try {
            travel.caval._20091127.commons.CavalGetListOfBookingsRS _return = null;
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see travel.caval._20091127.commons.CommonsBookingService#getWholeSupportedMap(travel.caval._20091127.commons.CavalGetWholeSupportedMapRQ rq)*
     */
    public travel.caval._20091127.commons.CavalGetWholeSupportedMapRS getWholeSupportedMap(travel.caval._20091127.commons.CavalGetWholeSupportedMapRQ rq) { 
        LOG.info("Executing operation getWholeSupportedMap");
        System.out.println(rq);
        try {
            travel.caval._20091127.commons.CavalGetWholeSupportedMapRS _return = null;
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

}
