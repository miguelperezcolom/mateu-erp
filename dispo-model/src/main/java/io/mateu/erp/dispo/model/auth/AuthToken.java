package io.mateu.erp.dispo.model.auth;

import io.mateu.erp.dispo.model.common.Actor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.ArrayList;

@Entity
@Getter@Setter
public class AuthToken {

    @Id
    private String id;

    private boolean active;

    @ManyToOne
    private Actor actor;

}
