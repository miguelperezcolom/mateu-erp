package io.mateu.erp.model.invoicing;


import io.mateu.mdd.core.views.BaseServerSideWizard;

public class IssueInvoicesWizard extends BaseServerSideWizard {

    public IssueInvoicesWizard() {
        add(new IssueInvoicesParametersPage());
        add(new IssueInvoicesShowProformaPage());
    }

}
