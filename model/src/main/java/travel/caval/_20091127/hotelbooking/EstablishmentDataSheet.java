
package travel.caval._20091127.hotelbooking;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Clase Java para establishmentDataSheet complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="establishmentDataSheet"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="lastModificationDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="categoryGroupId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="categoryCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="categoryName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cityId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cityName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="stateId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="stateName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="countryCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="countryName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="shortDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="shortDescriptionInOtherLanguages" type="{http://caval.travel/20091127/hotelBooking}multilanguageText" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="longDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="longDescriptionInOtherLanguages" type="{http://caval.travel/20091127/hotelBooking}multilanguageText" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="mainImageUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="otherImagesUrls" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="remarks" type="{http://caval.travel/20091127/hotelBooking}establishmentRemark" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="googleLatitude" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="googleLongitude" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="roomTypes" type="{http://caval.travel/20091127/hotelBooking}roomType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="featuresGroups" type="{http://caval.travel/20091127/hotelBooking}featuresGroup" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="telephone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="fax" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="zipCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "establishmentDataSheet", propOrder = {
    "id",
    "name",
    "status",
    "lastModificationDate",
    "categoryGroupId",
    "categoryCode",
    "categoryName",
    "cityId",
    "cityName",
    "stateId",
    "stateName",
    "countryCode",
    "countryName",
    "shortDescription",
    "shortDescriptionInOtherLanguages",
    "longDescription",
    "longDescriptionInOtherLanguages",
    "mainImageUrl",
    "otherImagesUrls",
    "remarks",
    "googleLatitude",
    "googleLongitude",
    "roomTypes",
    "featuresGroups",
    "telephone",
    "fax",
    "zipCode",
    "address"
})
public class EstablishmentDataSheet {

    protected String id;
    protected String name;
    protected String status;
    protected String lastModificationDate;
    protected String categoryGroupId;
    protected String categoryCode;
    protected String categoryName;
    protected String cityId;
    protected String cityName;
    protected String stateId;
    protected String stateName;
    protected String countryCode;
    protected String countryName;
    protected String shortDescription;
    @XmlElement(nillable = true)
    protected List<MultilanguageText> shortDescriptionInOtherLanguages;
    protected String longDescription;
    @XmlElement(nillable = true)
    protected List<MultilanguageText> longDescriptionInOtherLanguages;
    protected String mainImageUrl;
    @XmlElement(nillable = true)
    protected List<String> otherImagesUrls;
    @XmlElement(nillable = true)
    protected List<EstablishmentRemark> remarks;
    protected String googleLatitude;
    protected String googleLongitude;
    @XmlElement(nillable = true)
    protected List<RoomType> roomTypes;
    @XmlElement(nillable = true)
    protected List<FeaturesGroup> featuresGroups;
    protected String telephone;
    protected String fax;
    protected String zipCode;
    protected String address;

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
     * Obtiene el valor de la propiedad name.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Define el valor de la propiedad name.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
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
     * Obtiene el valor de la propiedad categoryGroupId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCategoryGroupId() {
        return categoryGroupId;
    }

    /**
     * Define el valor de la propiedad categoryGroupId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCategoryGroupId(String value) {
        this.categoryGroupId = value;
    }

    /**
     * Obtiene el valor de la propiedad categoryCode.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCategoryCode() {
        return categoryCode;
    }

    /**
     * Define el valor de la propiedad categoryCode.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCategoryCode(String value) {
        this.categoryCode = value;
    }

    /**
     * Obtiene el valor de la propiedad categoryName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * Define el valor de la propiedad categoryName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCategoryName(String value) {
        this.categoryName = value;
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
     * Gets the value of the shortDescriptionInOtherLanguages property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the shortDescriptionInOtherLanguages property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getShortDescriptionInOtherLanguages().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MultilanguageText }
     * 
     * 
     */
    public List<MultilanguageText> getShortDescriptionInOtherLanguages() {
        if (shortDescriptionInOtherLanguages == null) {
            shortDescriptionInOtherLanguages = new ArrayList<MultilanguageText>();
        }
        return this.shortDescriptionInOtherLanguages;
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
     * Gets the value of the longDescriptionInOtherLanguages property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the longDescriptionInOtherLanguages property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLongDescriptionInOtherLanguages().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MultilanguageText }
     * 
     * 
     */
    public List<MultilanguageText> getLongDescriptionInOtherLanguages() {
        if (longDescriptionInOtherLanguages == null) {
            longDescriptionInOtherLanguages = new ArrayList<MultilanguageText>();
        }
        return this.longDescriptionInOtherLanguages;
    }

    /**
     * Obtiene el valor de la propiedad mainImageUrl.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMainImageUrl() {
        return mainImageUrl;
    }

    /**
     * Define el valor de la propiedad mainImageUrl.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMainImageUrl(String value) {
        this.mainImageUrl = value;
    }

    /**
     * Gets the value of the otherImagesUrls property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the otherImagesUrls property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOtherImagesUrls().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getOtherImagesUrls() {
        if (otherImagesUrls == null) {
            otherImagesUrls = new ArrayList<String>();
        }
        return this.otherImagesUrls;
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
     * {@link EstablishmentRemark }
     * 
     * 
     */
    public List<EstablishmentRemark> getRemarks() {
        if (remarks == null) {
            remarks = new ArrayList<EstablishmentRemark>();
        }
        return this.remarks;
    }

    /**
     * Obtiene el valor de la propiedad googleLatitude.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGoogleLatitude() {
        return googleLatitude;
    }

    /**
     * Define el valor de la propiedad googleLatitude.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGoogleLatitude(String value) {
        this.googleLatitude = value;
    }

    /**
     * Obtiene el valor de la propiedad googleLongitude.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGoogleLongitude() {
        return googleLongitude;
    }

    /**
     * Define el valor de la propiedad googleLongitude.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGoogleLongitude(String value) {
        this.googleLongitude = value;
    }

    /**
     * Gets the value of the roomTypes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the roomTypes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRoomTypes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RoomType }
     * 
     * 
     */
    public List<RoomType> getRoomTypes() {
        if (roomTypes == null) {
            roomTypes = new ArrayList<RoomType>();
        }
        return this.roomTypes;
    }

    /**
     * Gets the value of the featuresGroups property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the featuresGroups property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFeaturesGroups().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FeaturesGroup }
     * 
     * 
     */
    public List<FeaturesGroup> getFeaturesGroups() {
        if (featuresGroups == null) {
            featuresGroups = new ArrayList<FeaturesGroup>();
        }
        return this.featuresGroups;
    }

    /**
     * Obtiene el valor de la propiedad telephone.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * Define el valor de la propiedad telephone.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTelephone(String value) {
        this.telephone = value;
    }

    /**
     * Obtiene el valor de la propiedad fax.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFax() {
        return fax;
    }

    /**
     * Define el valor de la propiedad fax.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFax(String value) {
        this.fax = value;
    }

    /**
     * Obtiene el valor de la propiedad zipCode.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * Define el valor de la propiedad zipCode.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZipCode(String value) {
        this.zipCode = value;
    }

    /**
     * Obtiene el valor de la propiedad address.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddress() {
        return address;
    }

    /**
     * Define el valor de la propiedad address.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddress(String value) {
        this.address = value;
    }

}
