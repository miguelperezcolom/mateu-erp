
package travel.caval._20091127.hotelbooking;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para combinationPrice complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="combinationPrice"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="rooms" type="{http://caval.travel/20091127/hotelBooking}roomOccupation" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="boardPrices" type="{http://caval.travel/20091127/hotelBooking}boardPrice" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="onRequest" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="cancellationCosts" type="{http://caval.travel/20091127/hotelBooking}cancellationCost" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="remarks" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="nonRefundable" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "combinationPrice", propOrder = {
    "rooms",
    "boardPrices",
    "onRequest",
    "cancellationCosts",
    "remarks",
    "nonRefundable"
})
public class CombinationPrice {

    @XmlElement(nillable = true)
    protected List<RoomOccupation> rooms;
    @XmlElement(nillable = true)
    protected List<BoardPrice> boardPrices;
    protected Boolean onRequest;
    @XmlElement(nillable = true)
    protected List<CancellationCost> cancellationCosts;
    @XmlElement(nillable = true)
    protected List<String> remarks;
    protected Boolean nonRefundable;

    /**
     * Gets the value of the rooms property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rooms property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRooms().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RoomOccupation }
     * 
     * 
     */
    public List<RoomOccupation> getRooms() {
        if (rooms == null) {
            rooms = new ArrayList<RoomOccupation>();
        }
        return this.rooms;
    }

    /**
     * Gets the value of the boardPrices property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the boardPrices property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBoardPrices().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BoardPrice }
     * 
     * 
     */
    public List<BoardPrice> getBoardPrices() {
        if (boardPrices == null) {
            boardPrices = new ArrayList<BoardPrice>();
        }
        return this.boardPrices;
    }

    /**
     * Obtiene el valor de la propiedad onRequest.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isOnRequest() {
        return onRequest;
    }

    /**
     * Define el valor de la propiedad onRequest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOnRequest(Boolean value) {
        this.onRequest = value;
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
     * Obtiene el valor de la propiedad nonRefundable.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNonRefundable() {
        return nonRefundable;
    }

    /**
     * Define el valor de la propiedad nonRefundable.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNonRefundable(Boolean value) {
        this.nonRefundable = value;
    }

}
