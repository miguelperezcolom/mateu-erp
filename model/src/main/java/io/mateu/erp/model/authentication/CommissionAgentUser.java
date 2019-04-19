package io.mateu.erp.model.authentication;

import io.mateu.erp.model.organization.Office;
import io.mateu.erp.model.organization.PointOfSale;
import io.mateu.erp.model.partners.Agency;
import io.mateu.erp.model.partners.CommissionAgent;
import io.mateu.erp.model.partners.Provider;
import io.mateu.erp.model.payments.BankAccount;
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
    private PointOfSale pointOfSale;

    @ManyToOne@NotNull
    private CommissionAgent commissionAgent;

    @ManyToOne@NotNull
    private Agency agency;

    @ManyToOne@NotNull
    private BankAccount bank;
}
