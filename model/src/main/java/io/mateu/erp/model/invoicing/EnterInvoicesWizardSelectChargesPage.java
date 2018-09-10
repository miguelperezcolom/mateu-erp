package io.mateu.erp.model.invoicing;


import com.vaadin.data.provider.DataProvider;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.dataProviders.JPQLListDataProvider;
import io.mateu.mdd.core.interfaces.WizardPage;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

@Getter@Setter
public class EnterInvoicesWizardSelectChargesPage implements WizardPage {

    @Ignored
    private final EnterInvoicesWizardParametersPage enterInvoicesWizardParametersPage;



    @NotEmpty
    private Set<PurchaseCharge> pending = new HashSet<>();


    public DataProvider getPendingDataProvider() throws Throwable {
        return new JPQLListDataProvider("select c from " + PurchaseCharge.class.getName() + " c where c.invoice = null and c.partner.id = " + enterInvoicesWizardParametersPage.getProvider().getId());
    }




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
    public boolean hasNext() {
        return true;
    }

    @Override
    public WizardPage getNext() {
        return new EnterInvoicesWizardInvoiceDataPage(this);
    }
}
