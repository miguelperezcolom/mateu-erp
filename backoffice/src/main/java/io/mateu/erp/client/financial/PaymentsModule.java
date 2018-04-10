package io.mateu.erp.client.financial;

import io.mateu.erp.model.accounting.AccountingEntry;
import io.mateu.erp.model.accounting.LineItem;
import io.mateu.erp.model.financials.BankRemittance;
import io.mateu.erp.model.financials.BankStatement;
import io.mateu.erp.model.invoicing.Invoice;
import io.mateu.erp.model.payments.*;
import io.mateu.erp.model.tpv.TPV;
import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.AbstractModule;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.app.MenuEntry;
import io.mateu.ui.mdd.client.ERPServiceAsync;
import io.mateu.ui.mdd.client.MDDAction;
import io.mateu.ui.mdd.client.MDDCallback;
import io.mateu.ui.mdd.client.MDDMenu;
import io.mateu.ui.mdd.shared.ERPService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class PaymentsModule extends AbstractModule {
    @Override
    public String getName() {
        return "Payments";
    }

    @Override
    public List<MenuEntry> buildMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new MDDAction("Accounts", Account.class));

        m.add(new MDDAction("Payment gateways", TPV.class));

        m.add(new MDDAction("VCC", VCC.class));

        m.add(new MDDAction("Deposits", Deposit.class));

        m.add(new MDDAction("Payments", Payment.class));

        m.add(new MDDAction("Bank remittances", BankRemittance.class));

        m.add(new MDDAction("Bank reconciliation", BankStatement.class));

        m.add(new MDDMenu("Accounting", "Accounts", io.mateu.erp.model.accounting.Account.class, "Entries", AccountingEntry.class, "Line items", LineItem.class));

        return m;
    }
}
