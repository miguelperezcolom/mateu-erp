
package travel.caval._20091127.commons;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Clase Java para destinationActivityService complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="destinationActivityService"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="netPrice" type="{http://caval.travel/20091127/commons}amount" minOccurs="0"/&gt;
 *         &lt;element name="grossPrice" type="{http://caval.travel/20091127/commons}amount" minOccurs="0"/&gt;
 *         &lt;element name="destinationActivityId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="destinationActivityName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="destinationActivityDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="destinationActivityDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="meetingPointId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="meetingPointName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="meetingPointDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="supplements" type="{http://caval.travel/20091127/commons}destinationActivitySupplement" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="commentForDestinationActivity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
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
@XmlType(name = "destinationActivityService", propOrder = {
    "id",
    "status",
    "netPrice",
    "grossPrice",
    "destinationActivityId",
    "destinationActivityName",
    "destinationActivityDescription",
    "destinationActivityDate",
    "meetingPointId",
    "meetingPointName",
    "meetingPointDescription",
    "supplements",
    "commentForDestinationActivity",
    "commentForBookingDepartment",
    "voucherText"
})
public class DestinationActivityService {

    protected String id;
    protected String status;
    protected Amount netPrice;
    protected Amount grossPrice;
    protected String destinationActivityId;
    protected String destinationActivityName;
    protected String destinationActivityDescription;
    protected String destinationActivityDate;
    protected String meetingPointId;
    protected String meetingPointName;
    protected String meetingPointDescription;
    @XmlElement(nillable = true)
    protected List<DestinationActivitySupplement> supplements;
    protected String commentForDestinationActivity;
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
     * Obtiene el valor de la propiedad destinationActivityId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestinationActivityId() {
        return destinationActivityId;
    }

    /**
     * Define el valor de la propiedad destinationActivityId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestinationActivityId(String value) {
        this.destinationActivityId = value;
    }

    /**
     * Obtiene el valor de la propiedad destinationActivityName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestinationActivityName() {
        return destinationActivityName;
    }

    /**
     * Define el valor de la propiedad destinationActivityName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestinationActivityName(String value) {
        this.destinationActivityName = value;
    }

    /**
     * Obtiene el valor de la propiedad destinationActivityDescription.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestinationActivityDescription() {
        return destinationActivityDescription;
    }

    /**
     * Define el valor de la propiedad destinationActivityDescription.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestinationActivityDescription(String value) {
        this.destinationActivityDescription = value;
    }

    /**
     * Obtiene el valor de la propiedad destinationActivityDate.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestinationActivityDate() {
        return destinationActivityDate;
    }

    /**
     * Define el valor de la propiedad destinationActivityDate.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestinationActivityDate(String value) {
        this.destinationActivityDate = value;
    }

    /**
     * Obtiene el valor de la propiedad meetingPointId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMeetingPointId() {
        return meetingPointId;
    }

    /**
     * Define el valor de la propiedad meetingPointId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMeetingPointId(String value) {
        this.meetingPointId = value;
    }

    /**
     * Obtiene el valor de la propiedad meetingPointName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMeetingPointName() {
        return meetingPointName;
    }

    /**
     * Define el valor de la propiedad meetingPointName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMeetingPointName(String value) {
        this.meetingPointName = value;
    }

    /**
     * Obtiene el valor de la propiedad meetingPointDescription.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMeetingPointDescription() {
        return meetingPointDescription;
    }

    /**
     * Define el valor de la propiedad meetingPointDescription.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMeetingPointDescription(String value) {
        this.meetingPointDescription = value;
    }

    /**
     * Gets the value of the supplements property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supplements property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupplements().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DestinationActivitySupplement }
     * 
     * 
     */
    public List<DestinationActivitySupplement> getSupplements() {
        if (supplements == null) {
            supplements = new ArrayList<DestinationActivitySupplement>();
        }
        return this.supplements;
    }

    /**
     * Obtiene el valor de la propiedad commentForDestinationActivity.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommentForDestinationActivity() {
        return commentForDestinationActivity;
    }

    /**
     * Define el valor de la propiedad commentForDestinationActivity.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommentForDestinationActivity(String value) {
        this.commentForDestinationActivity = value;
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
