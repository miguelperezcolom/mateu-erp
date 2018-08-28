package io.mateu.erp.model.invoicing;


import io.mateu.mdd.core.interfaces.WizardPage;

import java.io.IOException;

public class EnterInvoicesWizardInvoiceDataPage implements WizardPage {

    private final EnterInvoicesWizardSelectChargesPage enterInvoicesWizardSelectChargesPage;

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
    public WizardPage getNext() {
        return null;
    }

    @Override
    public void onOk() {

    }
}
