
package travel.caval._20091127.hotelbooking;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Clase Java para cavalHotelAvailabilityRS complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="cavalHotelAvailabilityRS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://caval.travel/20091127/hotelBooking}abstractRS"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="availableEstablishments" type="{http://caval.travel/20091127/hotelBooking}availableEstablishment" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="totalRows" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="fromRow" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="numRows" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="statsKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cavalHotelAvailabilityRS", propOrder = {
    "availableEstablishments",
    "totalRows",
    "fromRow",
    "numRows",
    "statsKey"
})
public class CavalHotelAvailabilityRS
    extends AbstractRS
{

    @XmlElement(nillable = true)
    protected List<AvailableEstablishment> availableEstablishments;
    protected Integer totalRows;
    protected Integer fromRow;
    protected Integer numRows;
    protected String statsKey;

    /**
     * Gets the value of the availableEstablishments property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the availableEstablishments property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAvailableEstablishments().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AvailableEstablishment }
     * 
     * 
     */
    public List<AvailableEstablishment> getAvailableEstablishments() {
        if (availableEstablishments == null) {
            availableEstablishments = new ArrayList<AvailableEstablishment>();
        }
        return this.availableEstablishments;
    }

    /**
     * Obtiene el valor de la propiedad totalRows.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTotalRows() {
        return totalRows;
    }

    /**
     * Define el valor de la propiedad totalRows.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTotalRows(Integer value) {
        this.totalRows = value;
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
     * Obtiene el valor de la propiedad statsKey.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatsKey() {
        return statsKey;
    }

    /**
     * Define el valor de la propiedad statsKey.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatsKey(String value) {
        this.statsKey = value;
    }

}
