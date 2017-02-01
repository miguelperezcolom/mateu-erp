
package travel.caval._20091127.commons;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Clase Java para roomOccupation complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="roomOccupation"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="checkin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="checkout" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="roomCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="roomName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="adultsPerRoom" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="childrenPerRoom" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="infantsPerRoom" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="childrenAges" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="numberOfRooms" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "roomOccupation", propOrder = {
    "checkin",
    "checkout",
    "roomCode",
    "roomName",
    "adultsPerRoom",
    "childrenPerRoom",
    "infantsPerRoom",
    "childrenAges",
    "numberOfRooms"
})
public class RoomOccupation {

    protected String checkin;
    protected String checkout;
    protected String roomCode;
    protected String roomName;
    protected int adultsPerRoom;
    protected int childrenPerRoom;
    protected int infantsPerRoom;
    @XmlElement(nillable = true)
    protected List<Integer> childrenAges;
    protected int numberOfRooms;

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
     * Obtiene el valor de la propiedad roomCode.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRoomCode() {
        return roomCode;
    }

    /**
     * Define el valor de la propiedad roomCode.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoomCode(String value) {
        this.roomCode = value;
    }

    /**
     * Obtiene el valor de la propiedad roomName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRoomName() {
        return roomName;
    }

    /**
     * Define el valor de la propiedad roomName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoomName(String value) {
        this.roomName = value;
    }

    /**
     * Obtiene el valor de la propiedad adultsPerRoom.
     * 
     */
    public int getAdultsPerRoom() {
        return adultsPerRoom;
    }

    /**
     * Define el valor de la propiedad adultsPerRoom.
     * 
     */
    public void setAdultsPerRoom(int value) {
        this.adultsPerRoom = value;
    }

    /**
     * Obtiene el valor de la propiedad childrenPerRoom.
     * 
     */
    public int getChildrenPerRoom() {
        return childrenPerRoom;
    }

    /**
     * Define el valor de la propiedad childrenPerRoom.
     * 
     */
    public void setChildrenPerRoom(int value) {
        this.childrenPerRoom = value;
    }

    /**
     * Obtiene el valor de la propiedad infantsPerRoom.
     * 
     */
    public int getInfantsPerRoom() {
        return infantsPerRoom;
    }

    /**
     * Define el valor de la propiedad infantsPerRoom.
     * 
     */
    public void setInfantsPerRoom(int value) {
        this.infantsPerRoom = value;
    }

    /**
     * Gets the value of the childrenAges property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the childrenAges property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getChildrenAges().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getChildrenAges() {
        if (childrenAges == null) {
            childrenAges = new ArrayList<Integer>();
        }
        return this.childrenAges;
    }

    /**
     * Obtiene el valor de la propiedad numberOfRooms.
     * 
     */
    public int getNumberOfRooms() {
        return numberOfRooms;
    }

    /**
     * Define el valor de la propiedad numberOfRooms.
     * 
     */
    public void setNumberOfRooms(int value) {
        this.numberOfRooms = value;
    }

}
