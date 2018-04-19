package io.mateu.erp.client.financial;

import io.mateu.erp.model.accounting.AccountingEntry;
import io.mateu.erp.model.accounting.LineItem;
import io.mateu.erp.model.financials.Abseiling;
import io.mateu.erp.model.financials.BillingConcept;
import io.mateu.erp.model.financials.CurrencyExchange;
import io.mateu.erp.model.financials.FinancialAgent;
import io.mateu.erp.model.taxes.VAT;
import io.mateu.erp.model.taxes.VATPercent;
import io.mateu.erp.model.taxes.VATSettlement;
import io.mateu.ui.core.client.app.AbstractModule;
import io.mateu.ui.core.client.app.MenuEntry;
import io.mateu.ui.mdd.client.MDDAction;
import io.mateu.ui.mdd.client.MDDMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class FinancialModule extends AbstractModule {
    @Override
    public String getName() {
        return "Financial";
    }

    @Override
    public List<MenuEntry> buildMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new MDDAction("Agents", FinancialAgent.class));

        m.add(new MDDAction("Currency exchanges", CurrencyExchange.class));

        m.add(new MDDAction("Billing concepts", BillingConcept.class));

        m.add(new MDDAction("Abseiling settlements", Abseiling.class));

        m.add(new MDDMenu("VAT", "VAT", VAT.class, "Percents", VATPercent.class, "Settlements", VATSettlement.class));


        m.add(new MDDMenu("Accounting", "Accounts", io.mateu.erp.model.accounting.Account.class, "Entries", AccountingEntry.class, "Line items", LineItem.class));


        return m;
    }
}
