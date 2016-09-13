package mateu.erp.model.authentication;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * holder for permissions. ids 1-1000 are reserved. You can add your own permissions starting with id 1001
 *
 * 1: super admin
 *
 * Created by miguel on 13/9/16.
 */
@Entity
@Table(name = "MA_PERMISSION")
@Getter@Setter
public class Permission {

    @Id
    @Column(name = "PERIDPER")
    private long id;

    @Column(name = "PERNAME", length = -1)
    private String name;

}
