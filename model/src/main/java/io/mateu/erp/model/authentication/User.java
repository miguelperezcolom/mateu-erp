package io.mateu.erp.model.authentication;

import io.mateu.erp.model.common.File;
import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.partners.Actor;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.ui.mdd.server.annotations.*;
import io.mateu.ui.mdd.server.annotations.Parameter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class User {

    @Tab("Info")
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
    private String password = "1";

    @ListColumn("Status")
    @NotNull
    private USER_STATUS status;

    private LocalDate expiryDate;

    @Output
    private LocalDateTime lastLogin;

    @Output
    private int failedLogins;

    @NotInList
    @ManyToOne
    private File photo;

    @TextArea
    private String comments;

    @Tab("Segmentation")
    @ManyToOne
    private Actor actor;

    @ManyToOne
    private Office office;

    @Tab("Permissions")
    @OneToMany
    private List<Permission> permissions = new ArrayList<Permission>();


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

}
