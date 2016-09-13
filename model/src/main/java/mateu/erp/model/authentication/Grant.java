package mateu.erp.model.authentication;

import javax.persistence.Entity;

/**
 * holder for a permission grant to user. It also defines the scope of the grant (e.g. a hotel, a customer, an office, ...)
 *
 * Created by miguel on 13/9/16.
 */
@Entity
public class Grant {

    private long id;

    private Permission permission;

    private User user;

    //TODO: add scope (hotel, office, ...)
}
