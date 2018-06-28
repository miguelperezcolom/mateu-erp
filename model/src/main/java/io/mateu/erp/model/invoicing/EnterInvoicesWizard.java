package io.mateu.erp.model.invoicing;

import io.mateu.ui.mdd.server.BaseServerSideWizard;

public class EnterInvoicesWizard extends BaseServerSideWizard {

    public EnterInvoicesWizard() {
        add(new EnterInvoicesWizardParametersPage());
        add(new EnterInvoicesWizardSelectChargesPage());
        add(new EnterInvoicesWizardInvoiceDataPage());
    }

}
