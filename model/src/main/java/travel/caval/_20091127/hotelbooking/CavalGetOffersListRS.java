
package travel.caval._20091127.hotelbooking;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para cavalGetOffersListRS complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="cavalGetOffersListRS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://caval.travel/20091127/hotelBooking}abstractRS"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="zonesWithOffers" type="{http://caval.travel/20091127/hotelBooking}zoneWithOffers" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cavalGetOffersListRS", propOrder = {
    "zonesWithOffers"
})
public class CavalGetOffersListRS
    extends AbstractRS
{

    @XmlElement(nillable = true)
    protected List<ZoneWithOffers> zonesWithOffers;

    /**
     * Gets the value of the zonesWithOffers property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the zonesWithOffers property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getZonesWithOffers().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ZoneWithOffers }
     * 
     * 
     */
    public List<ZoneWithOffers> getZonesWithOffers() {
        if (zonesWithOffers == null) {
            zonesWithOffers = new ArrayList<ZoneWithOffers>();
        }
        return this.zonesWithOffers;
    }

}
