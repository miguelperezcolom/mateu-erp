package io.mateu.erp.model.authentication;

import io.mateu.ui.mdd.serverside.annotations.Output;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static javax.persistence.TemporalType.TIMESTAMP;

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
    private String login;

    private String email;

    private String password;

    private USER_STATUS status;


    @OneToMany(mappedBy = "user")
    private List<Grant> grants = new ArrayList<Grant>();
}
