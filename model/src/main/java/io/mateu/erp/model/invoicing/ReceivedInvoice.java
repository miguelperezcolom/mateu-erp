package io.mateu.erp.model.invoicing;


import io.mateu.erp.model.config.AppConfig;
import io.mateu.erp.model.financials.FinancialAgent;
import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.Indelible;
import io.mateu.mdd.core.annotations.NewNotAllowed;
import io.mateu.mdd.core.interfaces.WizardPage;
import io.mateu.mdd.core.model.authentication.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import java.util.Collection;

@Entity
@Getter
@Setter
@NewNotAllowed
@Indelible
public class ReceivedInvoice extends Invoice {


    public ReceivedInvoice() {
        super();
    }

    public ReceivedInvoice(User u, Collection<PurchaseCharge> charges, FinancialAgent issuer, FinancialAgent recipient, String invoiceNumber) throws Throwable {
        //todo: rellenar factura
        //super(u, charges, false, issuer, recipient, invoiceNumber);
    }

    @Override
    public String getXslfo(EntityManager em) {
        return AppConfig.get(em).getXslfoForIssuedInvoice();
    }


    @Action("Enter invoices")
    public static WizardPage enter() {
        return new EnterInvoicesWizardParametersPage();
    }

}
