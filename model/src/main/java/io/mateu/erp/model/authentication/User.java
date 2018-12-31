package io.mateu.erp.model.authentication;

import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.partners.Partner;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity(name = "ERPUser")
@Getter
@Setter
public class User extends io.mateu.mdd.core.model.authentication.User {

    @ManyToOne
    private Partner actor;

    @ManyToOne
    private Office office;



}
