package mateu.erp.model.authentication;

import javax.persistence.Entity;
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
public class User {

    private long id;

    private String login;

    private String email;

    private String password;

    private USER_STATUS status;

    private User createdBy;

    private Date created;

    private User modifiedBy;

    private Date modified;

    private List<Grant> grants = new ArrayList<Grant>();
}
