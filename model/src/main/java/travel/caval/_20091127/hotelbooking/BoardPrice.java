
package travel.caval._20091127.hotelbooking;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para boardPrice complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="boardPrice"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="boardCategoryId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="boardCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="boardName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="oldNetPrice" type="{http://caval.travel/20091127/hotelBooking}amount" minOccurs="0"/&gt;
 *         &lt;element name="oldGrossPrice" type="{http://caval.travel/20091127/hotelBooking}amount" minOccurs="0"/&gt;
 *         &lt;element name="netPrice" type="{http://caval.travel/20091127/hotelBooking}amount" minOccurs="0"/&gt;
 *         &lt;element name="grossPrice" type="{http://caval.travel/20091127/hotelBooking}amount" minOccurs="0"/&gt;
 *         &lt;element name="offer" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="offerDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="appliedCategoryOffers" type="{http://caval.travel/20091127/hotelBooking}offerCategory" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "boardPrice", propOrder = {
    "boardCategoryId",
    "boardCode",
    "boardName",
    "oldNetPrice",
    "oldGrossPrice",
    "netPrice",
    "grossPrice",
    "offer",
    "offerDescription",
    "appliedCategoryOffers",
    "key"
})
public class BoardPrice {

    protected String boardCategoryId;
    protected String boardCode;
    protected String boardName;
    protected Amount oldNetPrice;
    protected Amount oldGrossPrice;
    protected Amount netPrice;
    protected Amount grossPrice;
    protected Boolean offer;
    protected String offerDescription;
    @XmlElement(nillable = true)
    protected List<OfferCategory> appliedCategoryOffers;
    protected String key;

    /**
     * Obtiene el valor de la propiedad boardCategoryId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBoardCategoryId() {
        return boardCategoryId;
    }

    /**
     * Define el valor de la propiedad boardCategoryId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBoardCategoryId(String value) {
        this.boardCategoryId = value;
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
     * Obtiene el valor de la propiedad oldNetPrice.
     * 
     * @return
     *     possible object is
     *     {@link Amount }
     *     
     */
    public Amount getOldNetPrice() {
        return oldNetPrice;
    }

    /**
     * Define el valor de la propiedad oldNetPrice.
     * 
     * @param value
     *     allowed object is
     *     {@link Amount }
     *     
     */
    public void setOldNetPrice(Amount value) {
        this.oldNetPrice = value;
    }

    /**
     * Obtiene el valor de la propiedad oldGrossPrice.
     * 
     * @return
     *     possible object is
     *     {@link Amount }
     *     
     */
    public Amount getOldGrossPrice() {
        return oldGrossPrice;
    }

    /**
     * Define el valor de la propiedad oldGrossPrice.
     * 
     * @param value
     *     allowed object is
     *     {@link Amount }
     *     
     */
    public void setOldGrossPrice(Amount value) {
        this.oldGrossPrice = value;
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
     * Obtiene el valor de la propiedad offer.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isOffer() {
        return offer;
    }

    /**
     * Define el valor de la propiedad offer.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOffer(Boolean value) {
        this.offer = value;
    }

    /**
     * Obtiene el valor de la propiedad offerDescription.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOfferDescription() {
        return offerDescription;
    }

    /**
     * Define el valor de la propiedad offerDescription.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOfferDescription(String value) {
        this.offerDescription = value;
    }

    /**
     * Gets the value of the appliedCategoryOffers property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the appliedCategoryOffers property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAppliedCategoryOffers().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OfferCategory }
     * 
     * 
     */
    public List<OfferCategory> getAppliedCategoryOffers() {
        if (appliedCategoryOffers == null) {
            appliedCategoryOffers = new ArrayList<OfferCategory>();
        }
        return this.appliedCategoryOffers;
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
