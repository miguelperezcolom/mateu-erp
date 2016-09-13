package mateu.erp.model.authentication;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * holder for users of our erp. It can be an internal user or a user created for a customer or a supplier
 *
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Table(name = "MUUSER")
@Getter@Setter
public class User {

    /**
     * login must always be uppercase. It is the primary key.
     */
    @Id
    @Column(name = "USRLOGIN")
    private String login;

    @Column(name = "USREMAIL")
    private String email;

    @Column(name = "USRPASSWORD")
    private String password;

    @Column(name = "USRSTATUS")
    private USER_STATUS status;

    @Column(name = "USRCREATEDBY")
    private User createdBy;

    @Column(name = "USRCREATED")
    private Date created;

    @Column(name = "USRMODIFIEDBY")
    private User modifiedBy;

    @Column(name = "USRMODIFIED")
    private Date modified;

    @OneToMany(mappedBy = "user")
    private List<Grant> grants = new ArrayList<Grant>();
}
