package io.mateu.erp.model.authentication;

import io.mateu.erp.model.common.File;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.ui.mdd.server.annotations.*;
import io.mateu.ui.mdd.server.annotations.Parameter;
import io.mateu.ui.mdd.server.interfaces.WithTriggers;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * holder for users of our erp. It can be an internal user or a user created for a customer or a supplier
 *
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Table(name = "_USER")
@Getter@Setter
public class User implements WithTriggers {

    @Embedded
    @Output
    private Audit audit = new Audit();

    /**
     * login must always be uppercase. It is the primary key.
     */
    @Id
    @ListColumn("Login")
    @NotNull
    private String login;

    @ListColumn("Name")
    @NotNull
    private String name;

    @ListColumn("Email")
    @NotNull
    private String email;

    @Ignored
    private String password;

    @ListColumn("Status")
    @NotNull
    private USER_STATUS status;

    @ManyToOne
    private Actor actor;


    @OneToMany
    @Ignored
    private List<Permission> permissions = new ArrayList<Permission>();


    @Ignored
    @ManyToOne
    private File photo;


    @Action(name = "Create token")
    public void createToken(EntityManager em, @NotNull @Parameter(name = "Agency") Actor a, @Parameter(name = "Hotel") Hotel h) throws IOException {
        AuthToken t = new AuthToken();
        em.persist(t);
        t.setActor(a);
        t.setHotel(h);
        t.setUser(this);
        t.setMaturity(null);
        t.setActive(true);

        t.setId(t.createId(this));
        System.out.println("token creado para el usuario " + getLogin() + " y el actor " + a.getName() + ": " + t.getId());
    }

    @Override
    public void beforeSet(EntityManager em, boolean isNew) throws Exception {

    }

    @Override
    public void afterSet(EntityManager em, boolean isNew) throws Exception {
        setPassword("1");
    }

    @Override
    public void beforeDelete(EntityManager em) throws Exception {

    }

    @Override
    public void afterDelete(EntityManager em) throws Exception {

    }

}
