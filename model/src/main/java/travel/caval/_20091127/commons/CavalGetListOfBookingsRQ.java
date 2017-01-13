
package travel.caval._20091127.commons;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para cavalGetListOfBookingsRQ complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="cavalGetListOfBookingsRQ"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://caval.travel/20091127/commons}abstractAuthenticatedAgencyRQ"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="fromStartOfServicesDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="toStartOfServicesDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="fromFormalizationDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="toFormalizationDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="fromLastModificationDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="toLastModificationDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cavalGetListOfBookingsRQ", propOrder = {
    "fromStartOfServicesDate",
    "toStartOfServicesDate",
    "fromFormalizationDate",
    "toFormalizationDate",
    "fromLastModificationDate",
    "toLastModificationDate"
})
public class CavalGetListOfBookingsRQ
    extends AbstractAuthenticatedAgencyRQ
{

    protected String fromStartOfServicesDate;
    protected String toStartOfServicesDate;
    protected String fromFormalizationDate;
    protected String toFormalizationDate;
    protected String fromLastModificationDate;
    protected String toLastModificationDate;

    /**
     * Obtiene el valor de la propiedad fromStartOfServicesDate.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFromStartOfServicesDate() {
        return fromStartOfServicesDate;
    }

    /**
     * Define el valor de la propiedad fromStartOfServicesDate.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFromStartOfServicesDate(String value) {
        this.fromStartOfServicesDate = value;
    }

    /**
     * Obtiene el valor de la propiedad toStartOfServicesDate.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToStartOfServicesDate() {
        return toStartOfServicesDate;
    }

    /**
     * Define el valor de la propiedad toStartOfServicesDate.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToStartOfServicesDate(String value) {
        this.toStartOfServicesDate = value;
    }

    /**
     * Obtiene el valor de la propiedad fromFormalizationDate.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFromFormalizationDate() {
        return fromFormalizationDate;
    }

    /**
     * Define el valor de la propiedad fromFormalizationDate.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFromFormalizationDate(String value) {
        this.fromFormalizationDate = value;
    }

    /**
     * Obtiene el valor de la propiedad toFormalizationDate.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToFormalizationDate() {
        return toFormalizationDate;
    }

    /**
     * Define el valor de la propiedad toFormalizationDate.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToFormalizationDate(String value) {
        this.toFormalizationDate = value;
    }

    /**
     * Obtiene el valor de la propiedad fromLastModificationDate.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFromLastModificationDate() {
        return fromLastModificationDate;
    }

    /**
     * Define el valor de la propiedad fromLastModificationDate.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFromLastModificationDate(String value) {
        this.fromLastModificationDate = value;
    }

    /**
     * Obtiene el valor de la propiedad toLastModificationDate.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToLastModificationDate() {
        return toLastModificationDate;
    }

    /**
     * Define el valor de la propiedad toLastModificationDate.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToLastModificationDate(String value) {
        this.toLastModificationDate = value;
    }

}
