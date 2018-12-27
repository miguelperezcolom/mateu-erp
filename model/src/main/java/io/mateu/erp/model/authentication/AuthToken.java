package io.mateu.erp.model.authentication;

import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.Caption;
import io.mateu.mdd.core.model.authentication.Permission;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.mdd.core.annotations.Indelible;
import io.mateu.mdd.core.annotations.NewNotAllowed;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.IOException;
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
    private User user;

    @ManyToOne
    private PointOfSale pos;

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
        return Base64.getEncoder().encodeToString(("{ \"created\": \"" + new Date() + "\", \"userId\": \"" + u.getLogin() + "\"" + ((getPartner() != null)?", \"partnerId\": \"" + getPartner().getId() + "\"":"") + ((getHotel() != null)?", \"hotelId\": \"" + getHotel().getId() + "\"":"") + "}").getBytes());
    }

    public AuthToken renew(EntityManager em) {
        AuthToken t = new AuthToken();
        t.setId(t.createId(getUser()));
        t.setActive(true);
        t.setUser(getUser());
        t.setPos(getPos());
        t.setPartner(getPartner());
        t.setHotel(getHotel());
        em.persist(t);

        setMaturity(new Date(new Date().getTime() + 1l * 60l * 60l * 1000l));

        return t;
    }


    @Action
    public static void createToken(EntityManager em, @NotNull User user, @NotNull @Caption("Point Of Sale") PointOfSale pos, @NotNull @Caption("Partner") Partner p, @Caption("Hotel") Hotel h) throws IOException {
        AuthToken t = new AuthToken();
        t.setPartner(p);
        t.setPos(pos);
        t.setHotel(h);
        t.setUser(user);
        t.setMaturity(null);
        t.setActive(true);

        t.setId(t.createId(user));
        em.persist(t);

        System.out.println("token creado para el usuario " + user.getLogin() + " y el partner " + p.getName() + ": " + t.getId());
    }

}
