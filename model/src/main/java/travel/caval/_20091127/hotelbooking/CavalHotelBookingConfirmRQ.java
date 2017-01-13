
package travel.caval._20091127.hotelbooking;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para cavalHotelBookingConfirmRQ complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="cavalHotelBookingConfirmRQ"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://caval.travel/20091127/hotelBooking}cavalHotelBookingValuationRQ"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="agencyReference" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="agencyEmail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="titular" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="guestCountryCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="passengers" type="{http://caval.travel/20091127/hotelBooking}passenger" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="commentForHotel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="commentForBookingDept" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="arrivalFlightNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="arrivalFlightTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="departureFlightNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="departureFlightTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="expectedNetPrice" type="{http://caval.travel/20091127/hotelBooking}amount" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cavalHotelBookingConfirmRQ", propOrder = {
    "rest"
})
public class CavalHotelBookingConfirmRQ
    extends CavalHotelBookingValuationRQ
{

    @XmlElementRefs({
        @XmlElementRef(name = "titular", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "arrivalFlightNumber", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "agencyReference", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "passengers", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "commentForBookingDept", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "expectedNetPrice", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "departureFlightTime", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "agencyEmail", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "arrivalFlightTime", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "guestCountryCode", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "departureFlightNumber", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "commentForHotel", type = JAXBElement.class, required = false)
    })
    protected List<JAXBElement<?>> rest;

    /**
     * Obtiene el resto del modelo de contenido. 
     * 
     * <p>
     * Ha obtenido esta propiedad que permite capturar todo por el siguiente motivo: 
     * El nombre de campo "GuestCountryCode" se está utilizando en dos partes diferentes de un esquema. Consulte: 
     * línea 391 de http://caval.travel/tech_specs/wsdls/HotelBookingService.wsdl
     * línea 414 de http://caval.travel/tech_specs/wsdls/HotelBookingService.wsdl
     * <p>
     * Para deshacerse de esta propiedad, aplique una personalización de propiedad a una
     * de las dos declaraciones siguientes para cambiarles de nombre: 
     * Gets the value of the rest property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rest property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRest().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link Passenger }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link Amount }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * 
     */
    public List<JAXBElement<?>> getRest() {
        if (rest == null) {
            rest = new ArrayList<JAXBElement<?>>();
        }
        return this.rest;
    }

}
