package io.mateu.erp.model.tpv;

import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.payments.Account;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class TPV {

        /*
    public static final String TEST_CLAVE = "sq7HjrUOBfKmC576ILgskD5srU870gJ7";
    public static final String TEST_CODIGO = "22052526";
    public static final String TEST_NOMBRE = "VIAJES URBIS, S.A.";
    public static final String TEST_URL = "http://www.viajesurbis.com";
*/


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private TPVTYPE type;

    private String name;
    private String paypalEmail;
    private boolean xml;
    private String merchantCode;
    private String merchantName;
    private String merchantSecret;
    private String privateKey;
    private String merchantTerminal;
    private String actionUrl;
    private String notificationUrl;
    private String okUrl;
    private String koUrl;

    @ManyToOne
    private Account account;

    @ManyToOne
    private Office office;


    @Override
    public String toString() {
        return name;
    }
}
