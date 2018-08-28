package io.mateu.erp.model.invoicing;


import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.interfaces.WizardPage;

public class EnterInvoicesWizardSelectChargesPage implements WizardPage {

    @Ignored
    private final EnterInvoicesWizardParametersPage enterInvoicesWizardParametersPage;






    public EnterInvoicesWizardSelectChargesPage(EnterInvoicesWizardParametersPage enterInvoicesWizardParametersPage) {
        this.enterInvoicesWizardParametersPage = enterInvoicesWizardParametersPage;
    }

    @Override
    public String toString() {
        return "Select charges";
    }

    @Override
    public WizardPage getPrevious() {
        return enterInvoicesWizardParametersPage;
    }

    @Override
    public WizardPage getNext() {
        return new EnterInvoicesWizardInvoiceDataPage(this);
    }
}
