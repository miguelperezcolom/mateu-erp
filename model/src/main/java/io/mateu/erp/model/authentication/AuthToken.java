package io.mateu.erp.model.authentication;

import io.mateu.mdd.core.model.authentication.Permission;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.mdd.core.annotations.Indelible;
import io.mateu.mdd.core.annotations.NewNotAllowed;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Base64;
import java.util.Date;

@Entity
@Getter@Setter
@NewNotAllowed
@Indelible
public class AuthToken {

    @Id
    private String id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date maturity;

    private boolean active = true;

    @ManyToOne
    private io.mateu.erp.model.authentication.User user;

    @ManyToOne
    private Partner partner;

    @ManyToOne
    private Hotel hotel;

    public String createId(User u) {
        Partner a = null;
        for (Permission p : u.getPermissions()) {
            //todo: relacinar con la agencia
        }
        //todo: utilizar jwt.io para encriptar
        return Base64.getEncoder().encodeToString(("{ \"created\": \"" + new Date() + "\", \"userId\": \"" + u.getLogin() + "\"" + ((getPartner() != null)?", \"partnerId\": \"" + getPartner().getId():"") + "\"" + ((getHotel() != null)?", \"hotelId\": \"" + getHotel().getId():"") + "\"}").getBytes());
    }

    public AuthToken renew(EntityManager em) {
        AuthToken t = new AuthToken();
        t.setId(t.createId(getUser()));
        t.setActive(true);
        t.setUser(getUser());
        t.setPartner(getPartner());
        t.setHotel(getHotel());
        em.persist(t);

        setMaturity(new Date(new Date().getTime() + 1l * 60l * 60l * 1000l));

        return t;
    }

}
