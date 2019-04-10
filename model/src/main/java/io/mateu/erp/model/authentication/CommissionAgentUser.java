package io.mateu.erp.model.authentication;

import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.partners.Agency;
import io.mateu.erp.model.partners.CommissionAgent;
import io.mateu.erp.model.partners.Provider;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class CommissionAgentUser extends io.mateu.mdd.core.model.authentication.User {

    @ManyToOne@NotNull
    private CommissionAgent commissionAgent;

}
