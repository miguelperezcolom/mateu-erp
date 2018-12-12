package io.mateu.erp.model.invoicing;


import com.vaadin.data.provider.DataProvider;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.dataProviders.JPQLListDataProvider;
import io.mateu.mdd.core.interfaces.WizardPage;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

@Getter@Setter
public class IssueInvoicesParametersPage implements WizardPage {


    @NotEmpty
    private Set<IssueInvoicesItem> pending = new HashSet<>();


    public DataProvider getPendingDataProvider() throws Throwable {
        return new JPQLListDataProvider("select new " + IssueInvoicesItem.class.getName() + "(c.partner, sum(c.total.value)) from " + Charge.class.getName() + " c where c.type = " + ChargeType.SALE.getClass().getName() + "." + ChargeType.SALE.name() + " and c.invoice = null group by c.partner");
    }


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
        try {
            return new IssueInvoicesShowProformaPage(this);
        } catch (Throwable throwable) {
            MDD.alert(throwable);
            return null;
        }
    }
}
