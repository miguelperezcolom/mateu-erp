
package travel.caval._20091127.commons;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para cavalGetBookingRS complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="cavalGetBookingRS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://caval.travel/20091127/commons}abstractRS"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="locator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="yourReference" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="titular" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="formalizationDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="lastModificationDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="hotelServices" type="{http://caval.travel/20091127/commons}hotelService" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="transferServices" type="{http://caval.travel/20091127/commons}transferService" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="destinationActivityServices" type="{http://caval.travel/20091127/commons}destinationActivityService" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="netPrice" type="{http://caval.travel/20091127/commons}amount" minOccurs="0"/&gt;
 *         &lt;element name="grossPrice" type="{http://caval.travel/20091127/commons}amount" minOccurs="0"/&gt;
 *         &lt;element name="cancellationCosts" type="{http://caval.travel/20091127/commons}cancellationCost" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="saleDetail" type="{http://caval.travel/20091127/commons}saleDetail" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cavalGetBookingRS", propOrder = {
    "locator",
    "yourReference",
    "titular",
    "formalizationDate",
    "lastModificationDate",
    "status",
    "hotelServices",
    "transferServices",
    "destinationActivityServices",
    "netPrice",
    "grossPrice",
    "cancellationCosts",
    "saleDetail"
})
public class CavalGetBookingRS
    extends AbstractRS
{

    protected String locator;
    protected String yourReference;
    protected String titular;
    protected String formalizationDate;
    protected String lastModificationDate;
    protected String status;
    @XmlElement(nillable = true)
    protected List<HotelService> hotelServices;
    @XmlElement(nillable = true)
    protected List<TransferService> transferServices;
    @XmlElement(nillable = true)
    protected List<DestinationActivityService> destinationActivityServices;
    protected Amount netPrice;
    protected Amount grossPrice;
    @XmlElement(nillable = true)
    protected List<CancellationCost> cancellationCosts;
    @XmlElement(nillable = true)
    protected List<SaleDetail> saleDetail;

    /**
     * Obtiene el valor de la propiedad locator.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocator() {
        return locator;
    }

    /**
     * Define el valor de la propiedad locator.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocator(String value) {
        this.locator = value;
    }

    /**
     * Obtiene el valor de la propiedad yourReference.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getYourReference() {
        return yourReference;
    }

    /**
     * Define el valor de la propiedad yourReference.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setYourReference(String value) {
        this.yourReference = value;
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
     * Obtiene el valor de la propiedad formalizationDate.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFormalizationDate() {
        return formalizationDate;
    }

    /**
     * Define el valor de la propiedad formalizationDate.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFormalizationDate(String value) {
        this.formalizationDate = value;
    }

    /**
     * Obtiene el valor de la propiedad lastModificationDate.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastModificationDate() {
        return lastModificationDate;
    }

    /**
     * Define el valor de la propiedad lastModificationDate.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastModificationDate(String value) {
        this.lastModificationDate = value;
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
     * Gets the value of the hotelServices property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the hotelServices property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHotelServices().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link HotelService }
     * 
     * 
     */
    public List<HotelService> getHotelServices() {
        if (hotelServices == null) {
            hotelServices = new ArrayList<HotelService>();
        }
        return this.hotelServices;
    }

    /**
     * Gets the value of the transferServices property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the transferServices property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransferServices().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransferService }
     * 
     * 
     */
    public List<TransferService> getTransferServices() {
        if (transferServices == null) {
            transferServices = new ArrayList<TransferService>();
        }
        return this.transferServices;
    }

    /**
     * Gets the value of the destinationActivityServices property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the destinationActivityServices property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDestinationActivityServices().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DestinationActivityService }
     * 
     * 
     */
    public List<DestinationActivityService> getDestinationActivityServices() {
        if (destinationActivityServices == null) {
            destinationActivityServices = new ArrayList<DestinationActivityService>();
        }
        return this.destinationActivityServices;
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
     * Gets the value of the cancellationCosts property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cancellationCosts property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCancellationCosts().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CancellationCost }
     * 
     * 
     */
    public List<CancellationCost> getCancellationCosts() {
        if (cancellationCosts == null) {
            cancellationCosts = new ArrayList<CancellationCost>();
        }
        return this.cancellationCosts;
    }

    /**
     * Gets the value of the saleDetail property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the saleDetail property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSaleDetail().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SaleDetail }
     * 
     * 
     */
    public List<SaleDetail> getSaleDetail() {
        if (saleDetail == null) {
            saleDetail = new ArrayList<SaleDetail>();
        }
        return this.saleDetail;
    }

}
