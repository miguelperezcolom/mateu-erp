package io.mateu.erp.model.authentication;

import lombok.Getter;
import lombok.Setter;

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
    @JoinColumn(name = "GRAIDPER")
    private Permission permission;

    @ManyToOne
    @JoinColumn(name = "GRAUSRLOGIN")
    private User user;

    public Grant(User u, Permission p) {
        setUser(u);
        setPermission(p);
    }

    public Grant() {

    }

    //TODO: add scope (hotel, office, ...)
}
