package io.mateu.erp.model.authentication;

import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.partners.Partner;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.Caption;
import io.mateu.mdd.core.annotations.Tab;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.io.IOException;

@Entity(name = "ERPUser")
@Getter
@Setter
public class User extends io.mateu.mdd.core.model.authentication.User {

    @ManyToOne
    private Partner actor;

    @ManyToOne
    private Office office;



    @Action
    public void createToken(EntityManager em, @NotNull @Caption("Partner") Partner p, @Caption("Hotel") Hotel h) throws IOException {
        AuthToken t = new AuthToken();
        t.setPartner(p);
        t.setHotel(h);
        t.setUser(this);
        t.setMaturity(null);
        t.setActive(true);

        t.setId(t.createId(this));
        em.persist(t);

        System.out.println("token creado para el usuario " + getLogin() + " y el partner " + p.getName() + ": " + t.getId());
    }




}
