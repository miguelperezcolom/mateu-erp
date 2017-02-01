
package travel.caval._20091127.commons;

import javax.xml.bind.annotation.*;


/**
 * <p>Clase Java para abstractAuthenticatedRQ complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="abstractAuthenticatedRQ"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="login" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="gzipResponse" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="rqId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "abstractAuthenticatedRQ", propOrder = {
    "login",
    "password",
    "gzipResponse",
    "rqId"
})
@XmlSeeAlso({
    AbstractAuthenticatedAgencyRQ.class
})
public abstract class AbstractAuthenticatedRQ {

    @XmlElement(required = true)
    protected String login;
    @XmlElement(required = true)
    protected String password;
    protected Boolean gzipResponse;
    protected String rqId;

    /**
     * Obtiene el valor de la propiedad login.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLogin() {
        return login;
    }

    /**
     * Define el valor de la propiedad login.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLogin(String value) {
        this.login = value;
    }

    /**
     * Obtiene el valor de la propiedad password.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Define el valor de la propiedad password.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Obtiene el valor de la propiedad gzipResponse.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isGzipResponse() {
        return gzipResponse;
    }

    /**
     * Define el valor de la propiedad gzipResponse.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setGzipResponse(Boolean value) {
        this.gzipResponse = value;
    }

    /**
     * Obtiene el valor de la propiedad rqId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRqId() {
        return rqId;
    }

    /**
     * Define el valor de la propiedad rqId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRqId(String value) {
        this.rqId = value;
    }

}
