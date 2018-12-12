package io.mateu.erp.model.invoicing;


import io.mateu.erp.model.organization.Company;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.interfaces.WizardPage;
import io.mateu.mdd.core.model.authentication.User;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.LocalDate;

@Getter@Setter
public class EnterInvoicesWizardInvoiceDataPage implements WizardPage {

    @Ignored
    private final EnterInvoicesWizardSelectChargesPage enterInvoicesWizardSelectChargesPage;


    @NotEmpty
    private String invoiceNumber;

    @NotNull
    private LocalDate invoiceDate;

    @NotNull
    private Company recipient;


    public EnterInvoicesWizardInvoiceDataPage(EnterInvoicesWizardSelectChargesPage enterInvoicesWizardSelectChargesPage) {
        this.enterInvoicesWizardSelectChargesPage = enterInvoicesWizardSelectChargesPage;
    }

    @Override
    public String toString() {
        return "Invoice data";
    }

    @Override
    public WizardPage getPrevious() {
        return enterInvoicesWizardSelectChargesPage;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public WizardPage getNext() {
        return null;
    }

    @Override
    public void onOk() throws Throwable {

        Helper.transact(em -> {

            User u = em.find(User.class, MDD.getUserData().getLogin());

            ReceivedInvoice i = new ReceivedInvoice(u, enterInvoicesWizardSelectChargesPage.getPending(), enterInvoicesWizardSelectChargesPage.getEnterInvoicesWizardParametersPage().getProvider().getFinancialAgent(), recipient.getFinancialAgent(), invoiceNumber);

            em.persist(i);

            enterInvoicesWizardSelectChargesPage.getPending().forEach(c -> em.merge(c));


        });

    }
}
