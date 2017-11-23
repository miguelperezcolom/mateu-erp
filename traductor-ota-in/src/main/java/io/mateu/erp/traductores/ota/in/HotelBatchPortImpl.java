
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package io.mateu.erp.traductores.ota.in;

import com.xmltravelgate.schemas.hub._2012._06.*;

import javax.jws.WebService;
import java.util.logging.Logger;

/**
 * This class was generated by Apache CXF 3.2.0
 * 2017-11-23T16:22:10.435+01:00
 * Generated source version: 3.2.0
 * 
 */

@WebService(
                      serviceName = "HotelBatch",
                      portName = "InsecureHttpPort",
                      targetNamespace = "http://schemas.xmltravelgate.com/hub/2012/06",
                      wsdlLocation = "xmltravelgate/HotelBatch.wsdl",
                      endpointInterface = "com.xmltravelgate.schemas.hub._2012._06.IServiceHotelBatch")

public class HotelBatchPortImpl implements IServiceHotelBatch {

    private static final Logger LOG = Logger.getLogger(HotelBatchPortImpl.class.getName());

    /* (non-Javadoc)
     * @see com.xmltravelgate.schemas.hub._2012._06.IServiceHotelBatch#runtimeConfiguration(com.xmltravelgate.schemas.hub._2012._06.RuntimeConfiguration parameters)*
     */
    public RuntimeConfigurationResponse runtimeConfiguration(RuntimeConfiguration parameters) {
        LOG.info("Executing operation runtimeConfiguration");
        System.out.println(parameters);
        try {
            RuntimeConfigurationResponse _return = null;
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see com.xmltravelgate.schemas.hub._2012._06.IServiceHotelBatch#staticConfiguration(com.xmltravelgate.schemas.hub._2012._06.StaticConfiguration parameters)*
     */
    public StaticConfigurationResponse staticConfiguration(StaticConfiguration parameters) {
        LOG.info("Executing operation staticConfiguration");
        System.out.println(parameters);
        try {
            StaticConfigurationResponse _return = null;
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see com.xmltravelgate.schemas.hub._2012._06.IServiceHotelBatch#descriptiveInfoExtended(com.xmltravelgate.schemas.hub._2012._06.DescriptiveInfoExtended parameters)*
     */
    public DescriptiveInfoExtendedResponse descriptiveInfoExtended(DescriptiveInfoExtended parameters) {
        LOG.info("Executing operation descriptiveInfoExtended");
        System.out.println(parameters);
        try {
            DescriptiveInfoExtendedResponse _return = null;
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see com.xmltravelgate.schemas.hub._2012._06.IServiceHotelBatch#roomList(com.xmltravelgate.schemas.hub._2012._06.RoomList parameters)*
     */
    public RoomListResponse roomList(RoomList parameters) {
        LOG.info("Executing operation roomList");
        System.out.println(parameters);
        try {
            RoomListResponse _return = null;
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see com.xmltravelgate.schemas.hub._2012._06.IServiceHotelBatch#descriptiveInfo(com.xmltravelgate.schemas.hub._2012._06.DescriptiveInfo parameters)*
     */
    public DescriptiveInfoResponse descriptiveInfo(DescriptiveInfo parameters) {
        LOG.info("Executing operation descriptiveInfo");
        System.out.println(parameters);
        try {
            DescriptiveInfoResponse _return = null;
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see com.xmltravelgate.schemas.hub._2012._06.IServiceHotelBatch#mealPlanList(com.xmltravelgate.schemas.hub._2012._06.MealPlanList parameters)*
     */
    public MealPlanListResponse mealPlanList(MealPlanList parameters) {
        LOG.info("Executing operation mealPlanList");
        System.out.println(parameters);
        try {
            MealPlanListResponse _return = null;
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see com.xmltravelgate.schemas.hub._2012._06.IServiceHotelBatch#geographicDestinationTree(com.xmltravelgate.schemas.hub._2012._06.GeographicDestinationTree parameters)*
     */
    public GeographicDestinationTreeResponse geographicDestinationTree(GeographicDestinationTree parameters) {
        LOG.info("Executing operation geographicDestinationTree");
        System.out.println(parameters);
        try {
            GeographicDestinationTreeResponse _return = null;
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see com.xmltravelgate.schemas.hub._2012._06.IServiceHotelBatch#marketList(com.xmltravelgate.schemas.hub._2012._06.MarketList parameters)*
     */
    public MarketListResponse marketList(MarketList parameters) {
        LOG.info("Executing operation marketList");
        System.out.println(parameters);
        try {
            MarketListResponse _return = null;
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see com.xmltravelgate.schemas.hub._2012._06.IServiceHotelBatch#availDestinationTree(com.xmltravelgate.schemas.hub._2012._06.AvailDestinationTree parameters)*
     */
    public AvailDestinationTreeResponse availDestinationTree(AvailDestinationTree parameters) {
        LOG.info("Executing operation availDestinationTree");
        System.out.println(parameters);
        try {
            AvailDestinationTreeResponse _return = null;
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see com.xmltravelgate.schemas.hub._2012._06.IServiceHotelBatch#currencyList(com.xmltravelgate.schemas.hub._2012._06.CurrencyList parameters)*
     */
    public CurrencyListResponse currencyList(CurrencyList parameters) {
        LOG.info("Executing operation currencyList");
        System.out.println(parameters);
        try {
            CurrencyListResponse _return = null;
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see com.xmltravelgate.schemas.hub._2012._06.IServiceHotelBatch#categoryList(com.xmltravelgate.schemas.hub._2012._06.CategoryList parameters)*
     */
    public CategoryListResponse categoryList(CategoryList parameters) {
        LOG.info("Executing operation categoryList");
        System.out.println(parameters);
        try {
            CategoryListResponse _return = null;
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see com.xmltravelgate.schemas.hub._2012._06.IServiceHotelBatch#hotelList(com.xmltravelgate.schemas.hub._2012._06.HotelList parameters)*
     */
    public HotelListResponse hotelList(HotelList parameters) {
        LOG.info("Executing operation hotelList");
        System.out.println(parameters);
        try {
            HotelListResponse _return = null;
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

}