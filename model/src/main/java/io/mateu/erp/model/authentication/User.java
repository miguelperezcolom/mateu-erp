package io.mateu.erp.model.authentication;

import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.partners.Agency;
import io.mateu.erp.model.partners.CommissionAgent;
import io.mateu.erp.model.partners.Provider;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity(name = "ERPUser")
@Getter
@Setter
public class User extends io.mateu.mdd.core.model.authentication.User {

    @ManyToOne
    private Agency agency;

    @ManyToOne
    private Provider provider;

    @ManyToOne
    private CommissionAgent commissionAgent;

    @ManyToOne
    private Office office;



}
