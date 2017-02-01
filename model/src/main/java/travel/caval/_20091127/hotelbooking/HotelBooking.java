
package travel.caval._20091127.hotelbooking;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Clase Java para hotelBooking complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="hotelBooking"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="contractId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="tourOperatorId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="reference" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="hotelId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="hotelName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="titular" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="telephone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cityId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cityName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="countryId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="countryName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="passengers" type="{http://caval.travel/20091127/hotelBooking}passenger" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="commentsForHotel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="commentsForBookingDepartment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="rooms" type="{http://caval.travel/20091127/hotelBooking}room" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="wantsArrivalTransfer" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="arrivalFlightNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="arrivalFlightDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="arrivalFlightTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="arrivalFlightOrigin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="wantsDepartureTransfer" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="departureFlightNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="departureFlightDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="departureFlightTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="departureFlightDestination" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="price" type="{http://caval.travel/20091127/hotelBooking}amount" minOccurs="0"/&gt;
 *         &lt;element name="formalizationTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="lastModificationTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "hotelBooking", propOrder = {
    "contractId",
    "tourOperatorId",
    "reference",
    "status",
    "hotelId",
    "hotelName",
    "titular",
    "email",
    "telephone",
    "address",
    "cityId",
    "cityName",
    "countryId",
    "countryName",
    "passengers",
    "commentsForHotel",
    "commentsForBookingDepartment",
    "rooms",
    "wantsArrivalTransfer",
    "arrivalFlightNumber",
    "arrivalFlightDate",
    "arrivalFlightTime",
    "arrivalFlightOrigin",
    "wantsDepartureTransfer",
    "departureFlightNumber",
    "departureFlightDate",
    "departureFlightTime",
    "departureFlightDestination",
    "price",
    "formalizationTime",
    "lastModificationTime"
})
public class HotelBooking {

    protected String contractId;
    protected String tourOperatorId;
    protected String reference;
    protected String status;
    protected String hotelId;
    protected String hotelName;
    protected String titular;
    protected String email;
    protected String telephone;
    protected String address;
    protected String cityId;
    protected String cityName;
    protected String countryId;
    protected String countryName;
    @XmlElement(nillable = true)
    protected List<Passenger> passengers;
    protected String commentsForHotel;
    protected String commentsForBookingDepartment;
    @XmlElement(nillable = true)
    protected List<Room> rooms;
    protected Boolean wantsArrivalTransfer;
    protected String arrivalFlightNumber;
    protected String arrivalFlightDate;
    protected String arrivalFlightTime;
    protected String arrivalFlightOrigin;
    protected Boolean wantsDepartureTransfer;
    protected String departureFlightNumber;
    protected String departureFlightDate;
    protected String departureFlightTime;
    protected String departureFlightDestination;
    protected Amount price;
    protected String formalizationTime;
    protected String lastModificationTime;

    /**
     * Obtiene el valor de la propiedad contractId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContractId() {
        return contractId;
    }

    /**
     * Define el valor de la propiedad contractId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContractId(String value) {
        this.contractId = value;
    }

    /**
     * Obtiene el valor de la propiedad tourOperatorId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTourOperatorId() {
        return tourOperatorId;
    }

    /**
     * Define el valor de la propiedad tourOperatorId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTourOperatorId(String value) {
        this.tourOperatorId = value;
    }

    /**
     * Obtiene el valor de la propiedad reference.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReference() {
        return reference;
    }

    /**
     * Define el valor de la propiedad reference.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReference(String value) {
        this.reference = value;
    }

    /**
     * Obtiene el valor de la propiedad status.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Define el valor de la propiedad status.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Obtiene el valor de la propiedad hotelId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHotelId() {
        return hotelId;
    }

    /**
     * Define el valor de la propiedad hotelId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHotelId(String value) {
        this.hotelId = value;
    }

    /**
     * Obtiene el valor de la propiedad hotelName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHotelName() {
        return hotelName;
    }

    /**
     * Define el valor de la propiedad hotelName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHotelName(String value) {
        this.hotelName = value;
    }

    /**
     * Obtiene el valor de la propiedad titular.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitular() {
        return titular;
    }

    /**
     * Define el valor de la propiedad titular.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitular(String value) {
        this.titular = value;
    }

    /**
     * Obtiene el valor de la propiedad email.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
        return email;
    }

    /**
     * Define el valor de la propiedad email.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Obtiene el valor de la propiedad telephone.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * Define el valor de la propiedad telephone.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTelephone(String value) {
        this.telephone = value;
    }

    /**
     * Obtiene el valor de la propiedad address.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddress() {
        return address;
    }

    /**
     * Define el valor de la propiedad address.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddress(String value) {
        this.address = value;
    }

    /**
     * Obtiene el valor de la propiedad cityId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCityId() {
        return cityId;
    }

    /**
     * Define el valor de la propiedad cityId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCityId(String value) {
        this.cityId = value;
    }

    /**
     * Obtiene el valor de la propiedad cityName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCityName() {
        return cityName;
    }

    /**
     * Define el valor de la propiedad cityName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCityName(String value) {
        this.cityName = value;
    }

    /**
     * Obtiene el valor de la propiedad countryId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountryId() {
        return countryId;
    }

    /**
     * Define el valor de la propiedad countryId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountryId(String value) {
        this.countryId = value;
    }

    /**
     * Obtiene el valor de la propiedad countryName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * Define el valor de la propiedad countryName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountryName(String value) {
        this.countryName = value;
    }

    /**
     * Gets the value of the passengers property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the passengers property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPassengers().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Passenger }
     * 
     * 
     */
    public List<Passenger> getPassengers() {
        if (passengers == null) {
            passengers = new ArrayList<Passenger>();
        }
        return this.passengers;
    }

    /**
     * Obtiene el valor de la propiedad commentsForHotel.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommentsForHotel() {
        return commentsForHotel;
    }

    /**
     * Define el valor de la propiedad commentsForHotel.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommentsForHotel(String value) {
        this.commentsForHotel = value;
    }

    /**
     * Obtiene el valor de la propiedad commentsForBookingDepartment.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommentsForBookingDepartment() {
        return commentsForBookingDepartment;
    }

    /**
     * Define el valor de la propiedad commentsForBookingDepartment.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommentsForBookingDepartment(String value) {
        this.commentsForBookingDepartment = value;
    }

    /**
     * Gets the value of the rooms property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rooms property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRooms().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Room }
     * 
     * 
     */
    public List<Room> getRooms() {
        if (rooms == null) {
            rooms = new ArrayList<Room>();
        }
        return this.rooms;
    }

    /**
     * Obtiene el valor de la propiedad wantsArrivalTransfer.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isWantsArrivalTransfer() {
        return wantsArrivalTransfer;
    }

    /**
     * Define el valor de la propiedad wantsArrivalTransfer.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setWantsArrivalTransfer(Boolean value) {
        this.wantsArrivalTransfer = value;
    }

    /**
     * Obtiene el valor de la propiedad arrivalFlightNumber.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArrivalFlightNumber() {
        return arrivalFlightNumber;
    }

    /**
     * Define el valor de la propiedad arrivalFlightNumber.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArrivalFlightNumber(String value) {
        this.arrivalFlightNumber = value;
    }

    /**
     * Obtiene el valor de la propiedad arrivalFlightDate.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArrivalFlightDate() {
        return arrivalFlightDate;
    }

    /**
     * Define el valor de la propiedad arrivalFlightDate.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArrivalFlightDate(String value) {
        this.arrivalFlightDate = value;
    }

    /**
     * Obtiene el valor de la propiedad arrivalFlightTime.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArrivalFlightTime() {
        return arrivalFlightTime;
    }

    /**
     * Define el valor de la propiedad arrivalFlightTime.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArrivalFlightTime(String value) {
        this.arrivalFlightTime = value;
    }

    /**
     * Obtiene el valor de la propiedad arrivalFlightOrigin.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArrivalFlightOrigin() {
        return arrivalFlightOrigin;
    }

    /**
     * Define el valor de la propiedad arrivalFlightOrigin.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArrivalFlightOrigin(String value) {
        this.arrivalFlightOrigin = value;
    }

    /**
     * Obtiene el valor de la propiedad wantsDepartureTransfer.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isWantsDepartureTransfer() {
        return wantsDepartureTransfer;
    }

    /**
     * Define el valor de la propiedad wantsDepartureTransfer.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setWantsDepartureTransfer(Boolean value) {
        this.wantsDepartureTransfer = value;
    }

    /**
     * Obtiene el valor de la propiedad departureFlightNumber.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepartureFlightNumber() {
        return departureFlightNumber;
    }

    /**
     * Define el valor de la propiedad departureFlightNumber.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepartureFlightNumber(String value) {
        this.departureFlightNumber = value;
    }

    /**
     * Obtiene el valor de la propiedad departureFlightDate.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepartureFlightDate() {
        return departureFlightDate;
    }

    /**
     * Define el valor de la propiedad departureFlightDate.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepartureFlightDate(String value) {
        this.departureFlightDate = value;
    }

    /**
     * Obtiene el valor de la propiedad departureFlightTime.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepartureFlightTime() {
        return departureFlightTime;
    }

    /**
     * Define el valor de la propiedad departureFlightTime.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepartureFlightTime(String value) {
        this.departureFlightTime = value;
    }

    /**
     * Obtiene el valor de la propiedad departureFlightDestination.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepartureFlightDestination() {
        return departureFlightDestination;
    }

    /**
     * Define el valor de la propiedad departureFlightDestination.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepartureFlightDestination(String value) {
        this.departureFlightDestination = value;
    }

    /**
     * Obtiene el valor de la propiedad price.
     * 
     * @return
     *     possible object is
     *     {@link Amount }
     *     
     */
    public Amount getPrice() {
        return price;
    }

    /**
     * Define el valor de la propiedad price.
     * 
     * @param value
     *     allowed object is
     *     {@link Amount }
     *     
     */
    public void setPrice(Amount value) {
        this.price = value;
    }

    /**
     * Obtiene el valor de la propiedad formalizationTime.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFormalizationTime() {
        return formalizationTime;
    }

    /**
     * Define el valor de la propiedad formalizationTime.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFormalizationTime(String value) {
        this.formalizationTime = value;
    }

    /**
     * Obtiene el valor de la propiedad lastModificationTime.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastModificationTime() {
        return lastModificationTime;
    }

    /**
     * Define el valor de la propiedad lastModificationTime.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastModificationTime(String value) {
        this.lastModificationTime = value;
    }

}
