
package travel.caval._20091127.hotelbooking;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Clase Java para valuatedOccupation complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="valuatedOccupation"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="numberOfRooms" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="adultsPerRoom" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="childrenPerRoom" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="childAges" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="roomCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="roomName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "valuatedOccupation", propOrder = {
    "numberOfRooms",
    "adultsPerRoom",
    "childrenPerRoom",
    "childAges",
    "roomCode",
    "roomName",
    "status"
})
public class ValuatedOccupation {

    protected Integer numberOfRooms;
    protected Integer adultsPerRoom;
    protected Integer childrenPerRoom;
    @XmlElement(nillable = true)
    protected List<Integer> childAges;
    protected String roomCode;
    protected String roomName;
    protected String status;

    /**
     * Obtiene el valor de la propiedad numberOfRooms.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumberOfRooms() {
        return numberOfRooms;
    }

    /**
     * Define el valor de la propiedad numberOfRooms.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumberOfRooms(Integer value) {
        this.numberOfRooms = value;
    }

    /**
     * Obtiene el valor de la propiedad adultsPerRoom.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAdultsPerRoom() {
        return adultsPerRoom;
    }

    /**
     * Define el valor de la propiedad adultsPerRoom.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAdultsPerRoom(Integer value) {
        this.adultsPerRoom = value;
    }

    /**
     * Obtiene el valor de la propiedad childrenPerRoom.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getChildrenPerRoom() {
        return childrenPerRoom;
    }

    /**
     * Define el valor de la propiedad childrenPerRoom.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setChildrenPerRoom(Integer value) {
        this.childrenPerRoom = value;
    }

    /**
     * Gets the value of the childAges property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the childAges property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getChildAges().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getChildAges() {
        if (childAges == null) {
            childAges = new ArrayList<Integer>();
        }
        return this.childAges;
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

}
