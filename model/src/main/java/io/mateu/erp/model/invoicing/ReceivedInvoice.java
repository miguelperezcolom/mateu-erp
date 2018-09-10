package io.mateu.erp.model.invoicing;


import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.Indelible;
import io.mateu.mdd.core.annotations.KPI;
import io.mateu.mdd.core.annotations.NewNotAllowed;
import io.mateu.mdd.core.interfaces.WizardPage;
import io.mateu.mdd.core.model.authentication.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NewNotAllowed
@Indelible
public class ReceivedInvoice extends Invoice {


    public ReceivedInvoice() {
        super();
    }

    public ReceivedInvoice(User u, @NotEmpty Collection<PurchaseCharge> charges) throws Throwable {
        super(u, charges);
    }

    @Action("Enter invoices")
    public static WizardPage enter() {
        return new EnterInvoicesWizardParametersPage();
    }

}
