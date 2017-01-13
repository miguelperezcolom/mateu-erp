
package travel.caval._20091127.hotelbooking;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para getAvailableHotels complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="getAvailableHotels"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="rq" type="{http://caval.travel/20091127/hotelBooking}cavalHotelAvailabilityRQ" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAvailableHotels", propOrder = {
    "rq"
})
public class GetAvailableHotels {

    protected CavalHotelAvailabilityRQ rq;

    /**
     * Obtiene el valor de la propiedad rq.
     * 
     * @return
     *     possible object is
     *     {@link CavalHotelAvailabilityRQ }
     *     
     */
    public CavalHotelAvailabilityRQ getRq() {
        return rq;
    }

    /**
     * Define el valor de la propiedad rq.
     * 
     * @param value
     *     allowed object is
     *     {@link CavalHotelAvailabilityRQ }
     *     
     */
    public void setRq(CavalHotelAvailabilityRQ value) {
        this.rq = value;
    }

}
