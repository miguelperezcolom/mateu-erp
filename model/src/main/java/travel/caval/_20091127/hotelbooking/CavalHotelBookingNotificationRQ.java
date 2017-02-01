
package travel.caval._20091127.hotelbooking;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Clase Java para cavalHotelBookingNotificationRQ complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="cavalHotelBookingNotificationRQ"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://caval.travel/20091127/hotelBooking}abstractAuthenticatedAgencyRQ"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="booking" type="{http://caval.travel/20091127/hotelBooking}hotelBooking" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cavalHotelBookingNotificationRQ", propOrder = {
    "bookings"
})
public class CavalHotelBookingNotificationRQ
    extends AbstractAuthenticatedAgencyRQ
{

    @XmlElement(nillable = true)
    protected List<HotelBooking> bookings;

    /**
     * Gets the value of the booking property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the booking property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBookings().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link HotelBooking }
     * 
     * 
     */
    public List<HotelBooking> getBookings() {
        if (bookings == null) {
            bookings = new ArrayList<HotelBooking>();
        }
        return this.bookings;
    }

}
