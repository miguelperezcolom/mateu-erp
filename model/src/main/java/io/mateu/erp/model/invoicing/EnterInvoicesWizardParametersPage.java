package io.mateu.erp.model.invoicing;

import io.mateu.erp.model.partners.Partner;
import io.mateu.mdd.core.views.AbstractServerSideWizardPage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnterInvoicesWizardParametersPage extends AbstractServerSideWizardPage {

    private Partner provider;

    @Override
    public String getTitle() {
        return "Parameters";
    }
}
