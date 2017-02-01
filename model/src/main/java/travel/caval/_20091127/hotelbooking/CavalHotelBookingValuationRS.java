
package travel.caval._20091127.hotelbooking;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Clase Java para cavalHotelBookingValuationRS complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="cavalHotelBookingValuationRS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://caval.travel/20091127/hotelBooking}abstractRS"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="establishmentId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="establishmentName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="establishmentCategory" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="establishmentAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="establishmentZip" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="establishmentCity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="establishmentCountry" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="establishmentImageUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="establishmentDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="checkin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="checkout" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="boardCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="boardName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="occupations" type="{http://caval.travel/20091127/hotelBooking}valuatedOccupation" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="offer" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="offerDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="appliedCategoryOffers" type="{http://caval.travel/20091127/hotelBooking}offerCategory" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="netPrice" type="{http://caval.travel/20091127/hotelBooking}amount" minOccurs="0"/&gt;
 *         &lt;element name="grossPrice" type="{http://caval.travel/20091127/hotelBooking}amount" minOccurs="0"/&gt;
 *         &lt;element name="remarks" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="valuationLines" type="{http://caval.travel/20091127/hotelBooking}valuatedLine" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="availableSupplements" type="{http://caval.travel/20091127/hotelBooking}supplement" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="cancellationCosts" type="{http://caval.travel/20091127/hotelBooking}cancellationCost" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="statsKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
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
@XmlType(name = "cavalHotelBookingValuationRS", propOrder = {
    "establishmentId",
    "establishmentName",
    "establishmentCategory",
    "establishmentAddress",
    "establishmentZip",
    "establishmentCity",
    "establishmentCountry",
    "establishmentImageUrl",
    "establishmentDescription",
    "checkin",
    "checkout",
    "boardCode",
    "boardName",
    "occupations",
    "status",
    "offer",
    "offerDescription",
    "appliedCategoryOffers",
    "netPrice",
    "grossPrice",
    "remarks",
    "valuationLines",
    "availableSupplements",
    "cancellationCosts",
    "statsKey",
    "key"
})
public class CavalHotelBookingValuationRS
    extends AbstractRS
{

    protected String establishmentId;
    protected String establishmentName;
    protected String establishmentCategory;
    protected String establishmentAddress;
    protected String establishmentZip;
    protected String establishmentCity;
    protected String establishmentCountry;
    protected String establishmentImageUrl;
    protected String establishmentDescription;
    protected String checkin;
    protected String checkout;
    protected String boardCode;
    protected String boardName;
    @XmlElement(nillable = true)
    protected List<ValuatedOccupation> occupations;
    protected String status;
    protected boolean offer;
    protected String offerDescription;
    @XmlElement(nillable = true)
    protected List<OfferCategory> appliedCategoryOffers;
    protected Amount netPrice;
    protected Amount grossPrice;
    @XmlElement(nillable = true)
    protected List<String> remarks;
    @XmlElement(nillable = true)
    protected List<ValuatedLine> valuationLines;
    @XmlElement(nillable = true)
    protected List<Supplement> availableSupplements;
    @XmlElement(nillable = true)
    protected List<CancellationCost> cancellationCosts;
    protected String statsKey;
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
     * Obtiene el valor de la propiedad establishmentCategory.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstablishmentCategory() {
        return establishmentCategory;
    }

    /**
     * Define el valor de la propiedad establishmentCategory.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstablishmentCategory(String value) {
        this.establishmentCategory = value;
    }

    /**
     * Obtiene el valor de la propiedad establishmentAddress.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstablishmentAddress() {
        return establishmentAddress;
    }

    /**
     * Define el valor de la propiedad establishmentAddress.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstablishmentAddress(String value) {
        this.establishmentAddress = value;
    }

    /**
     * Obtiene el valor de la propiedad establishmentZip.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstablishmentZip() {
        return establishmentZip;
    }

    /**
     * Define el valor de la propiedad establishmentZip.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstablishmentZip(String value) {
        this.establishmentZip = value;
    }

    /**
     * Obtiene el valor de la propiedad establishmentCity.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstablishmentCity() {
        return establishmentCity;
    }

    /**
     * Define el valor de la propiedad establishmentCity.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstablishmentCity(String value) {
        this.establishmentCity = value;
    }

    /**
     * Obtiene el valor de la propiedad establishmentCountry.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstablishmentCountry() {
        return establishmentCountry;
    }

    /**
     * Define el valor de la propiedad establishmentCountry.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstablishmentCountry(String value) {
        this.establishmentCountry = value;
    }

    /**
     * Obtiene el valor de la propiedad establishmentImageUrl.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstablishmentImageUrl() {
        return establishmentImageUrl;
    }

    /**
     * Define el valor de la propiedad establishmentImageUrl.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstablishmentImageUrl(String value) {
        this.establishmentImageUrl = value;
    }

    /**
     * Obtiene el valor de la propiedad establishmentDescription.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstablishmentDescription() {
        return establishmentDescription;
    }

    /**
     * Define el valor de la propiedad establishmentDescription.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstablishmentDescription(String value) {
        this.establishmentDescription = value;
    }

    /**
     * Obtiene el valor de la propiedad checkin.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCheckin() {
        return checkin;
    }

    /**
     * Define el valor de la propiedad checkin.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCheckin(String value) {
        this.checkin = value;
    }

    /**
     * Obtiene el valor de la propiedad checkout.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCheckout() {
        return checkout;
    }

    /**
     * Define el valor de la propiedad checkout.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCheckout(String value) {
        this.checkout = value;
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
     * {@link ValuatedOccupation }
     * 
     * 
     */
    public List<ValuatedOccupation> getOccupations() {
        if (occupations == null) {
            occupations = new ArrayList<ValuatedOccupation>();
        }
        return this.occupations;
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
     * Obtiene el valor de la propiedad offer.
     * 
     */
    public boolean isOffer() {
        return offer;
    }

    /**
     * Define el valor de la propiedad offer.
     * 
     */
    public void setOffer(boolean value) {
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
     * Gets the value of the valuationLines property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the valuationLines property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getValuationLines().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ValuatedLine }
     * 
     * 
     */
    public List<ValuatedLine> getValuationLines() {
        if (valuationLines == null) {
            valuationLines = new ArrayList<ValuatedLine>();
        }
        return this.valuationLines;
    }

    /**
     * Gets the value of the availableSupplements property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the availableSupplements property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAvailableSupplements().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Supplement }
     * 
     * 
     */
    public List<Supplement> getAvailableSupplements() {
        if (availableSupplements == null) {
            availableSupplements = new ArrayList<Supplement>();
        }
        return this.availableSupplements;
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
