package io.mateu.erp.model.invoicing;

import io.mateu.erp.model.booking.parts.SupplierDataProvider;
import io.mateu.erp.model.partners.Partner;
import io.mateu.mdd.core.annotations.DataProvider;
import io.mateu.mdd.core.interfaces.WizardPage;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class EnterInvoicesWizardParametersPage implements WizardPage {

    @NotNull
    @DataProvider(dataProvider = SupplierDataProvider.class)
    private Partner provider;

    @Override
    public String toString() {
        return "Parameters";
    }

    @Override
    public WizardPage getPrevious() {
        return null;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public WizardPage getNext() {
        return new EnterInvoicesWizardSelectChargesPage(this);
    }
}
