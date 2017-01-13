
package travel.caval._20091127.hotelbooking;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para cavalHotelBookingValuationRQ complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="cavalHotelBookingValuationRQ"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://caval.travel/20091127/hotelBooking}abstractAuthenticatedAgencyRQ"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="establishmentId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="checkIn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="checkOut" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="boardCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="occupations" type="{http://caval.travel/20091127/hotelBooking}occupation" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="desiredSupplements" type="{http://caval.travel/20091127/hotelBooking}desiredSupplement" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="guestCountryCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cavalHotelBookingValuationRQ", propOrder = {
    "establishmentId",
    "checkIn",
    "checkOut",
    "boardCode",
    "occupations",
    "desiredSupplements",
    "guestCountryCode",
    "key"
})
@XmlSeeAlso({
    CavalHotelBookingConfirmRQ.class
})
public class CavalHotelBookingValuationRQ
    extends AbstractAuthenticatedAgencyRQ
{

    protected String establishmentId;
    protected String checkIn;
    protected String checkOut;
    protected String boardCode;
    @XmlElement(nillable = true)
    protected List<Occupation> occupations;
    @XmlElement(nillable = true)
    protected List<DesiredSupplement> desiredSupplements;
    protected String guestCountryCode;
    protected String key;

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
     * {@link Occupation }
     * 
     * 
     */
    public List<Occupation> getOccupations() {
        if (occupations == null) {
            occupations = new ArrayList<Occupation>();
        }
        return this.occupations;
    }

    /**
     * Gets the value of the desiredSupplements property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the desiredSupplements property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDesiredSupplements().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DesiredSupplement }
     * 
     * 
     */
    public List<DesiredSupplement> getDesiredSupplements() {
        if (desiredSupplements == null) {
            desiredSupplements = new ArrayList<DesiredSupplement>();
        }
        return this.desiredSupplements;
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
     * Obtiene el valor de la propiedad key.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKey() {
        return key;
    }

    /**
     * Define el valor de la propiedad key.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKey(String value) {
        this.key = value;
    }

}
