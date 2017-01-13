
package travel.caval._20091127.hotelbooking;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para getListOfBoardTypes complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="getListOfBoardTypes"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="rq" type="{http://caval.travel/20091127/hotelBooking}cavalGetListOfBoardTypesRQ" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getListOfBoardTypes", propOrder = {
    "rq"
})
public class GetListOfBoardTypes {

    protected CavalGetListOfBoardTypesRQ rq;

    /**
     * Obtiene el valor de la propiedad rq.
     * 
     * @return
     *     possible object is
     *     {@link CavalGetListOfBoardTypesRQ }
     *     
     */
    public CavalGetListOfBoardTypesRQ getRq() {
        return rq;
    }

    /**
     * Define el valor de la propiedad rq.
     * 
     * @param value
     *     allowed object is
     *     {@link CavalGetListOfBoardTypesRQ }
     *     
     */
    public void setRq(CavalGetListOfBoardTypesRQ value) {
        this.rq = value;
    }

}
