package io.mateu.erp.model.authentication;

import io.mateu.ui.mdd.serverside.annotations.Ignored;
import io.mateu.ui.mdd.serverside.annotations.ListColumn;
import io.mateu.ui.mdd.serverside.annotations.Output;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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

    @Embedded
    @Output
    private Audit audit = new Audit();

    /**
     * login must always be uppercase. It is the primary key.
     */
    @Id
    @ListColumn("Login")
    private String login;

    @ListColumn("Name")
    private String name;

    @ListColumn("Email")
    private String email;

    @Ignored
    private String password;

    @ListColumn("Status")
    private USER_STATUS status;


    @OneToMany(mappedBy = "user")
    private List<Grant> grants = new ArrayList<Grant>();
}
