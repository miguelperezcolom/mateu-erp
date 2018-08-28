package io.mateu.erp.model.invoicing;


import io.mateu.mdd.core.interfaces.WizardPage;

public class IssueInvoicesParametersPage implements WizardPage {

    @Override
    public String toString() {
        return "Parameters";
    }


    @Override
    public WizardPage getPrevious() {
        return null;
    }

    @Override
    public WizardPage getNext() {
        return new IssueInvoicesShowProformaPage(this);
    }
}
