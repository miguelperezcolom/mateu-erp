package mateu.erp.model.authentication;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;

import javax.persistence.*;

/**
 * holder for a permission grant to user. It also defines the scope of the grant (e.g. a hotel, a customer, an office, ...)
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Table(name = "MA_GRANT")
@Getter@Setter
public class Grant {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO, generator="grant_seq_gen")
    @SequenceGenerator(name="grant_seq_gen", sequenceName="GRAIDGRA_SEQ")
    @Column(name="GRAIDGRA")
    private long id;

    @ManyToOne
    @Column(name = "GRAIDPER")
    private Permission permission;

    @ManyToOne
    @Column(name = "GRAUSRLOGIN")
    private User user;

    public Grant(User u, Permission p) {
        setUser(u);
        setPermission(p);
    }

    //TODO: add scope (hotel, office, ...)
}
