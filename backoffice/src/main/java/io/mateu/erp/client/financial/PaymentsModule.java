package io.mateu.erp.client.financial;

import io.mateu.erp.model.financials.BankRemittance;
import io.mateu.erp.model.financials.BankStatement;
import io.mateu.erp.model.payments.Account;
import io.mateu.erp.model.payments.Deposit;
import io.mateu.erp.model.payments.Payment;
import io.mateu.erp.model.payments.VCC;
import io.mateu.erp.model.tpv.TPV;
import io.mateu.ui.core.client.app.AbstractModule;
import io.mateu.ui.core.client.app.MenuEntry;
import io.mateu.ui.mdd.client.MDDAction;

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

        return m;
    }
}
