
package travel.caval._20091127.hotelbooking;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para abstractRS complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="abstractRS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="resultCode" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cpuTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="dateAtServer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="rqId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "abstractRS", propOrder = {
    "resultCode",
    "message",
    "cpuTime",
    "dateAtServer",
    "rqId"
})
@XmlSeeAlso({
    CavalHotelBookingNotificationRS.class,
    CavalHotelAvailabilityRS.class,
    CavalGetOffersListRS.class,
    CavalHotelBookingConfirmRS.class,
    CavalHotelBookingValuationRS.class,
    CavalGetEstablishmentDataSheetsRS.class,
    CavalGetListOfBoardTypesRS.class
})
public abstract class AbstractRS {

    protected int resultCode;
    protected String message;
    protected String cpuTime;
    protected String dateAtServer;
    protected String rqId;

    /**
     * Obtiene el valor de la propiedad resultCode.
     * 
     */
    public int getResultCode() {
        return resultCode;
    }

    /**
     * Define el valor de la propiedad resultCode.
     * 
     */
    public void setResultCode(int value) {
        this.resultCode = value;
    }

    /**
     * Obtiene el valor de la propiedad message.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessage() {
        return message;
    }

    /**
     * Define el valor de la propiedad message.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessage(String value) {
        this.message = value;
    }

    /**
     * Obtiene el valor de la propiedad cpuTime.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCpuTime() {
        return cpuTime;
    }

    /**
     * Define el valor de la propiedad cpuTime.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCpuTime(String value) {
        this.cpuTime = value;
    }

    /**
     * Obtiene el valor de la propiedad dateAtServer.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDateAtServer() {
        return dateAtServer;
    }

    /**
     * Define el valor de la propiedad dateAtServer.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateAtServer(String value) {
        this.dateAtServer = value;
    }

    /**
     * Obtiene el valor de la propiedad rqId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRqId() {
        return rqId;
    }

    /**
     * Define el valor de la propiedad rqId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRqId(String value) {
        this.rqId = value;
    }

}
