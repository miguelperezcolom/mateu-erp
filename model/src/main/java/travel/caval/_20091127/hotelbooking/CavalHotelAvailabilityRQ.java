
package travel.caval._20091127.hotelbooking;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para cavalHotelAvailabilityRQ complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="cavalHotelAvailabilityRQ"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://caval.travel/20091127/hotelBooking}abstractAuthenticatedAgencyRQ"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="stateIds" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="cityIds" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="establishmentIds" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="airportIds" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="checkIn" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="checkOut" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="occupations" type="{http://caval.travel/20091127/hotelBooking}availRQOccupation" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="hotelCategoryGroupFilter" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="boardGroupFilter" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="roomGroupFilter" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="establishmentClassificationFilter" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="establishmentNameFilter" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="excludeOnRequest" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="onlyOffers" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="removeHotelInfo" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="fromRow" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="numRows" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="coverageArea" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="googleLatitude" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                   &lt;element name="googleLongitude" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                   &lt;element name="radius" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="includeCancellationCostsAndRemarks" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="guestCountryCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cavalHotelAvailabilityRQ", propOrder = {
    "stateIds",
    "cityIds",
    "establishmentIds",
    "airportIds",
    "checkIn",
    "checkOut",
    "occupations",
    "hotelCategoryGroupFilter",
    "boardGroupFilter",
    "roomGroupFilter",
    "establishmentClassificationFilter",
    "establishmentNameFilter",
    "excludeOnRequest",
    "onlyOffers",
    "removeHotelInfo",
    "fromRow",
    "numRows",
    "coverageArea",
    "includeCancellationCostsAndRemarks",
    "guestCountryCode"
})
public class CavalHotelAvailabilityRQ
    extends AbstractAuthenticatedAgencyRQ
{

    @XmlElement(nillable = true)
    protected List<String> stateIds;
    @XmlElement(nillable = true)
    protected List<String> cityIds;
    @XmlElement(nillable = true)
    protected List<String> establishmentIds;
    @XmlElement(nillable = true)
    protected List<String> airportIds;
    @XmlElement(required = true)
    protected String checkIn;
    @XmlElement(required = true)
    protected String checkOut;
    @XmlElement(nillable = true)
    protected List<AvailRQOccupation> occupations;
    @XmlElement(nillable = true)
    protected List<String> hotelCategoryGroupFilter;
    @XmlElement(nillable = true)
    protected List<String> boardGroupFilter;
    @XmlElement(nillable = true)
    protected List<String> roomGroupFilter;
    @XmlElement(nillable = true)
    protected List<String> establishmentClassificationFilter;
    protected String establishmentNameFilter;
    protected Boolean excludeOnRequest;
    protected Boolean onlyOffers;
    protected Boolean removeHotelInfo;
    protected Integer fromRow;
    protected Integer numRows;
    protected CavalHotelAvailabilityRQ.CoverageArea coverageArea;
    protected Boolean includeCancellationCostsAndRemarks;
    protected String guestCountryCode;

    /**
     * Gets the value of the stateIds property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the stateIds property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStateIds().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getStateIds() {
        if (stateIds == null) {
            stateIds = new ArrayList<String>();
        }
        return this.stateIds;
    }

    /**
     * Gets the value of the cityIds property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cityIds property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCityIds().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getCityIds() {
        if (cityIds == null) {
            cityIds = new ArrayList<String>();
        }
        return this.cityIds;
    }

    /**
     * Gets the value of the establishmentIds property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the establishmentIds property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEstablishmentIds().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getEstablishmentIds() {
        if (establishmentIds == null) {
            establishmentIds = new ArrayList<String>();
        }
        return this.establishmentIds;
    }

    /**
     * Gets the value of the airportIds property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the airportIds property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAirportIds().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAirportIds() {
        if (airportIds == null) {
            airportIds = new ArrayList<String>();
        }
        return this.airportIds;
    }

    /**
     * Obtiene el valor de la propiedad checkIn.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCheckIn() {
        return checkIn;
    }

    /**
     * Define el valor de la propiedad checkIn.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCheckIn(String value) {
        this.checkIn = value;
    }

    /**
     * Obtiene el valor de la propiedad checkOut.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCheckOut() {
        return checkOut;
    }

    /**
     * Define el valor de la propiedad checkOut.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCheckOut(String value) {
        this.checkOut = value;
    }

    /**
     * Gets the value of the occupations property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the occupations property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOccupations().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AvailRQOccupation }
     * 
     * 
     */
    public List<AvailRQOccupation> getOccupations() {
        if (occupations == null) {
            occupations = new ArrayList<AvailRQOccupation>();
        }
        return this.occupations;
    }

    /**
     * Gets the value of the hotelCategoryGroupFilter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the hotelCategoryGroupFilter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHotelCategoryGroupFilter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getHotelCategoryGroupFilter() {
        if (hotelCategoryGroupFilter == null) {
            hotelCategoryGroupFilter = new ArrayList<String>();
        }
        return this.hotelCategoryGroupFilter;
    }

    /**
     * Gets the value of the boardGroupFilter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the boardGroupFilter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBoardGroupFilter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getBoardGroupFilter() {
        if (boardGroupFilter == null) {
            boardGroupFilter = new ArrayList<String>();
        }
        return this.boardGroupFilter;
    }

    /**
     * Gets the value of the roomGroupFilter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the roomGroupFilter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRoomGroupFilter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getRoomGroupFilter() {
        if (roomGroupFilter == null) {
            roomGroupFilter = new ArrayList<String>();
        }
        return this.roomGroupFilter;
    }

    /**
     * Gets the value of the establishmentClassificationFilter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the establishmentClassificationFilter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEstablishmentClassificationFilter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getEstablishmentClassificationFilter() {
        if (establishmentClassificationFilter == null) {
            establishmentClassificationFilter = new ArrayList<String>();
        }
        return this.establishmentClassificationFilter;
    }

    /**
     * Obtiene el valor de la propiedad establishmentNameFilter.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstablishmentNameFilter() {
        return establishmentNameFilter;
    }

    /**
     * Define el valor de la propiedad establishmentNameFilter.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstablishmentNameFilter(String value) {
        this.establishmentNameFilter = value;
    }

    /**
     * Obtiene el valor de la propiedad excludeOnRequest.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExcludeOnRequest() {
        return excludeOnRequest;
    }

    /**
     * Define el valor de la propiedad excludeOnRequest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExcludeOnRequest(Boolean value) {
        this.excludeOnRequest = value;
    }

    /**
     * Obtiene el valor de la propiedad onlyOffers.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isOnlyOffers() {
        return onlyOffers;
    }

    /**
     * Define el valor de la propiedad onlyOffers.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOnlyOffers(Boolean value) {
        this.onlyOffers = value;
    }

    /**
     * Obtiene el valor de la propiedad removeHotelInfo.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRemoveHotelInfo() {
        return removeHotelInfo;
    }

    /**
     * Define el valor de la propiedad removeHotelInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRemoveHotelInfo(Boolean value) {
        this.removeHotelInfo = value;
    }

    /**
     * Obtiene el valor de la propiedad fromRow.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFromRow() {
        return fromRow;
    }

    /**
     * Define el valor de la propiedad fromRow.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFromRow(Integer value) {
        this.fromRow = value;
    }

    /**
     * Obtiene el valor de la propiedad numRows.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumRows() {
        return numRows;
    }

    /**
     * Define el valor de la propiedad numRows.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumRows(Integer value) {
        this.numRows = value;
    }

    /**
     * Obtiene el valor de la propiedad coverageArea.
     * 
     * @return
     *     possible object is
     *     {@link CavalHotelAvailabilityRQ.CoverageArea }
     *     
     */
    public CavalHotelAvailabilityRQ.CoverageArea getCoverageArea() {
        return coverageArea;
    }

    /**
     * Define el valor de la propiedad coverageArea.
     * 
     * @param value
     *     allowed object is
     *     {@link CavalHotelAvailabilityRQ.CoverageArea }
     *     
     */
    public void setCoverageArea(CavalHotelAvailabilityRQ.CoverageArea value) {
        this.coverageArea = value;
    }

    /**
     * Obtiene el valor de la propiedad includeCancellationCostsAndRemarks.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIncludeCancellationCostsAndRemarks() {
        return includeCancellationCostsAndRemarks;
    }

    /**
     * Define el valor de la propiedad includeCancellationCostsAndRemarks.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIncludeCancellationCostsAndRemarks(Boolean value) {
        this.includeCancellationCostsAndRemarks = value;
    }

    /**
     * Obtiene el valor de la propiedad guestCountryCode.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuestCountryCode() {
        return guestCountryCode;
    }

    /**
     * Define el valor de la propiedad guestCountryCode.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuestCountryCode(String value) {
        this.guestCountryCode = value;
    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="googleLatitude" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *         &lt;element name="googleLongitude" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *         &lt;element name="radius" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "googleLatitude",
        "googleLongitude",
        "radius"
    })
    public static class CoverageArea {

        @XmlElement(required = true)
        protected String googleLatitude;
        @XmlElement(required = true)
        protected String googleLongitude;
        @XmlElement(required = true)
        protected String radius;

        /**
         * Obtiene el valor de la propiedad googleLatitude.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getGoogleLatitude() {
            return googleLatitude;
        }

        /**
         * Define el valor de la propiedad googleLatitude.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setGoogleLatitude(String value) {
            this.googleLatitude = value;
        }

        /**
         * Obtiene el valor de la propiedad googleLongitude.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getGoogleLongitude() {
            return googleLongitude;
        }

        /**
         * Define el valor de la propiedad googleLongitude.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setGoogleLongitude(String value) {
            this.googleLongitude = value;
        }

        /**
         * Obtiene el valor de la propiedad radius.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRadius() {
            return radius;
        }

        /**
         * Define el valor de la propiedad radius.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRadius(String value) {
            this.radius = value;
        }

    }

}
