
package travel.caval._20091127.commons;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Clase Java para hotelService complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="hotelService"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="establishmentId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="establishmentName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cityId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cityName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="stateId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="stateName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="countryCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="countryName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="boardCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="boardName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="roomOccupations" type="{http://caval.travel/20091127/commons}roomOccupation" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="netPrice" type="{http://caval.travel/20091127/commons}amount" minOccurs="0"/&gt;
 *         &lt;element name="grossPrice" type="{http://caval.travel/20091127/commons}amount" minOccurs="0"/&gt;
 *         &lt;element name="remarks" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="commentForHotel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="commentForBookingDepartment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="voucherText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "hotelService", propOrder = {
    "id",
    "status",
    "establishmentId",
    "establishmentName",
    "cityId",
    "cityName",
    "stateId",
    "stateName",
    "countryCode",
    "countryName",
    "boardCode",
    "boardName",
    "roomOccupations",
    "netPrice",
    "grossPrice",
    "remarks",
    "commentForHotel",
    "commentForBookingDepartment",
    "voucherText"
})
public class HotelService {

    protected String id;
    protected String status;
    protected String establishmentId;
    protected String establishmentName;
    protected String cityId;
    protected String cityName;
    protected String stateId;
    protected String stateName;
    protected String countryCode;
    protected String countryName;
    protected String boardCode;
    protected String boardName;
    @XmlElement(nillable = true)
    protected List<RoomOccupation> roomOccupations;
    protected Amount netPrice;
    protected Amount grossPrice;
    @XmlElement(nillable = true)
    protected List<String> remarks;
    protected String commentForHotel;
    protected String commentForBookingDepartment;
    protected String voucherText;

    /**
     * Obtiene el valor de la propiedad id.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Define el valor de la propiedad id.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
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
     * Obtiene el valor de la propiedad establishmentId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstablishmentId() {
        return establishmentId;
    }

    /**
     * Define el valor de la propiedad establishmentId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstablishmentId(String value) {
        this.establishmentId = value;
    }

    /**
     * Obtiene el valor de la propiedad establishmentName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstablishmentName() {
        return establishmentName;
    }

    /**
     * Define el valor de la propiedad establishmentName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstablishmentName(String value) {
        this.establishmentName = value;
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
     * Obtiene el valor de la propiedad stateId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStateId() {
        return stateId;
    }

    /**
     * Define el valor de la propiedad stateId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStateId(String value) {
        this.stateId = value;
    }

    /**
     * Obtiene el valor de la propiedad stateName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStateName() {
        return stateName;
    }

    /**
     * Define el valor de la propiedad stateName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStateName(String value) {
        this.stateName = value;
    }

    /**
     * Obtiene el valor de la propiedad countryCode.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Define el valor de la propiedad countryCode.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountryCode(String value) {
        this.countryCode = value;
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
     * Obtiene el valor de la propiedad boardCode.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBoardCode() {
        return boardCode;
    }

    /**
     * Define el valor de la propiedad boardCode.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBoardCode(String value) {
        this.boardCode = value;
    }

    /**
     * Obtiene el valor de la propiedad boardName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBoardName() {
        return boardName;
    }

    /**
     * Define el valor de la propiedad boardName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBoardName(String value) {
        this.boardName = value;
    }

    /**
     * Gets the value of the roomOccupations property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the roomOccupations property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRoomOccupations().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RoomOccupation }
     * 
     * 
     */
    public List<RoomOccupation> getRoomOccupations() {
        if (roomOccupations == null) {
            roomOccupations = new ArrayList<RoomOccupation>();
        }
        return this.roomOccupations;
    }

    /**
     * Obtiene el valor de la propiedad netPrice.
     * 
     * @return
     *     possible object is
     *     {@link Amount }
     *     
     */
    public Amount getNetPrice() {
        return netPrice;
    }

    /**
     * Define el valor de la propiedad netPrice.
     * 
     * @param value
     *     allowed object is
     *     {@link Amount }
     *     
     */
    public void setNetPrice(Amount value) {
        this.netPrice = value;
    }

    /**
     * Obtiene el valor de la propiedad grossPrice.
     * 
     * @return
     *     possible object is
     *     {@link Amount }
     *     
     */
    public Amount getGrossPrice() {
        return grossPrice;
    }

    /**
     * Define el valor de la propiedad grossPrice.
     * 
     * @param value
     *     allowed object is
     *     {@link Amount }
     *     
     */
    public void setGrossPrice(Amount value) {
        this.grossPrice = value;
    }

    /**
     * Gets the value of the remarks property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the remarks property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRemarks().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getRemarks() {
        if (remarks == null) {
            remarks = new ArrayList<String>();
        }
        return this.remarks;
    }

    /**
     * Obtiene el valor de la propiedad commentForHotel.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommentForHotel() {
        return commentForHotel;
    }

    /**
     * Define el valor de la propiedad commentForHotel.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommentForHotel(String value) {
        this.commentForHotel = value;
    }

    /**
     * Obtiene el valor de la propiedad commentForBookingDepartment.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommentForBookingDepartment() {
        return commentForBookingDepartment;
    }

    /**
     * Define el valor de la propiedad commentForBookingDepartment.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommentForBookingDepartment(String value) {
        this.commentForBookingDepartment = value;
    }

    /**
     * Obtiene el valor de la propiedad voucherText.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVoucherText() {
        return voucherText;
    }

    /**
     * Define el valor de la propiedad voucherText.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVoucherText(String value) {
        this.voucherText = value;
    }

}
