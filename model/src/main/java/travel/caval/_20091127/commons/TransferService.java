
package travel.caval._20091127.commons;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para transferService complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="transferService"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="transferId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="transferType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="shortDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="longDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="transportArrivalInstructions" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="transportDepartureInstructions" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="duration" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="estimatedPickupTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="pickups" type="{http://caval.travel/20091127/commons}pickup" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="dropoffs" type="{http://caval.travel/20091127/commons}dropoff" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="netPrice" type="{http://caval.travel/20091127/commons}amount" minOccurs="0"/&gt;
 *         &lt;element name="grossPrice" type="{http://caval.travel/20091127/commons}amount" minOccurs="0"/&gt;
 *         &lt;element name="commentForTransfer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
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
@XmlType(name = "transferService", propOrder = {
    "id",
    "status",
    "transferId",
    "transferType",
    "shortDescription",
    "longDescription",
    "transportArrivalInstructions",
    "transportDepartureInstructions",
    "duration",
    "estimatedPickupTime",
    "pickups",
    "dropoffs",
    "netPrice",
    "grossPrice",
    "commentForTransfer",
    "commentForBookingDepartment",
    "voucherText"
})
public class TransferService {

    protected String id;
    protected String status;
    protected String transferId;
    protected String transferType;
    protected String shortDescription;
    protected String longDescription;
    protected String transportArrivalInstructions;
    protected String transportDepartureInstructions;
    protected String duration;
    protected String estimatedPickupTime;
    @XmlElement(nillable = true)
    protected List<Pickup> pickups;
    @XmlElement(nillable = true)
    protected List<Dropoff> dropoffs;
    protected Amount netPrice;
    protected Amount grossPrice;
    protected String commentForTransfer;
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
     * Obtiene el valor de la propiedad transferId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransferId() {
        return transferId;
    }

    /**
     * Define el valor de la propiedad transferId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransferId(String value) {
        this.transferId = value;
    }

    /**
     * Obtiene el valor de la propiedad transferType.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransferType() {
        return transferType;
    }

    /**
     * Define el valor de la propiedad transferType.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransferType(String value) {
        this.transferType = value;
    }

    /**
     * Obtiene el valor de la propiedad shortDescription.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShortDescription() {
        return shortDescription;
    }

    /**
     * Define el valor de la propiedad shortDescription.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShortDescription(String value) {
        this.shortDescription = value;
    }

    /**
     * Obtiene el valor de la propiedad longDescription.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLongDescription() {
        return longDescription;
    }

    /**
     * Define el valor de la propiedad longDescription.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLongDescription(String value) {
        this.longDescription = value;
    }

    /**
     * Obtiene el valor de la propiedad transportArrivalInstructions.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransportArrivalInstructions() {
        return transportArrivalInstructions;
    }

    /**
     * Define el valor de la propiedad transportArrivalInstructions.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransportArrivalInstructions(String value) {
        this.transportArrivalInstructions = value;
    }

    /**
     * Obtiene el valor de la propiedad transportDepartureInstructions.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransportDepartureInstructions() {
        return transportDepartureInstructions;
    }

    /**
     * Define el valor de la propiedad transportDepartureInstructions.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransportDepartureInstructions(String value) {
        this.transportDepartureInstructions = value;
    }

    /**
     * Obtiene el valor de la propiedad duration.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDuration() {
        return duration;
    }

    /**
     * Define el valor de la propiedad duration.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDuration(String value) {
        this.duration = value;
    }

    /**
     * Obtiene el valor de la propiedad estimatedPickupTime.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstimatedPickupTime() {
        return estimatedPickupTime;
    }

    /**
     * Define el valor de la propiedad estimatedPickupTime.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstimatedPickupTime(String value) {
        this.estimatedPickupTime = value;
    }

    /**
     * Gets the value of the pickups property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pickups property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPickups().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Pickup }
     * 
     * 
     */
    public List<Pickup> getPickups() {
        if (pickups == null) {
            pickups = new ArrayList<Pickup>();
        }
        return this.pickups;
    }

    /**
     * Gets the value of the dropoffs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dropoffs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDropoffs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Dropoff }
     * 
     * 
     */
    public List<Dropoff> getDropoffs() {
        if (dropoffs == null) {
            dropoffs = new ArrayList<Dropoff>();
        }
        return this.dropoffs;
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
     * Obtiene el valor de la propiedad commentForTransfer.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommentForTransfer() {
        return commentForTransfer;
    }

    /**
     * Define el valor de la propiedad commentForTransfer.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommentForTransfer(String value) {
        this.commentForTransfer = value;
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
