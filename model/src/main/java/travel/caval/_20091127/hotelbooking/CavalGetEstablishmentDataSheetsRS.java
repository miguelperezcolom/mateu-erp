
package travel.caval._20091127.hotelbooking;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para cavalGetEstablishmentDataSheetsRS complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="cavalGetEstablishmentDataSheetsRS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://caval.travel/20091127/hotelBooking}abstractRS"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dataSheets" type="{http://caval.travel/20091127/hotelBooking}establishmentDataSheet" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cavalGetEstablishmentDataSheetsRS", propOrder = {
    "dataSheets"
})
public class CavalGetEstablishmentDataSheetsRS
    extends AbstractRS
{

    @XmlElement(nillable = true)
    protected List<EstablishmentDataSheet> dataSheets;

    /**
     * Gets the value of the dataSheets property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dataSheets property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDataSheets().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EstablishmentDataSheet }
     * 
     * 
     */
    public List<EstablishmentDataSheet> getDataSheets() {
        if (dataSheets == null) {
            dataSheets = new ArrayList<EstablishmentDataSheet>();
        }
        return this.dataSheets;
    }

}
