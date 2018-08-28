package io.mateu.erp.model.invoicing;


import io.mateu.mdd.core.interfaces.WizardPage;

public class IssueInvoicesShowProformaPage implements WizardPage {
    private final IssueInvoicesParametersPage issueInvoicesParametersPage;

    public IssueInvoicesShowProformaPage(IssueInvoicesParametersPage issueInvoicesParametersPage) {
        this.issueInvoicesParametersPage = issueInvoicesParametersPage;
    }

    @Override
    public String toString() {
        return "Proforma";
    }

    @Override
    public WizardPage getPrevious() {
        return issueInvoicesParametersPage;
    }

    @Override
    public WizardPage getNext() {
        return null;
    }

    @Override
    public void onOk() {

    }
}
