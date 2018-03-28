package io.mateu.erp.client.financial;

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

        /*
                <class>io.mateu.erp.model.payments.AbstractPaymentAllocation</class>
        <class>io.mateu.erp.model.payments.Account</class>
        <class>io.mateu.erp.model.payments.BankAccount</class>
        <class>io.mateu.erp.model.payments.BookingPaymentAllocation</class>
        <class>io.mateu.erp.model.payments.Deposit</class>
        <class>io.mateu.erp.model.payments.InvoicePaymentAllocation</class>
        <class>io.mateu.erp.model.payments.Payment</class>
        <class>io.mateu.erp.model.tpv.TPV</class>
        <class>io.mateu.erp.model.tpv.TPVTransaction</class>
        <class>io.mateu.erp.model.payments.VCC</class>
         */


        m.add(new MDDAction("Accounts", Account.class));

        m.add(new MDDAction("Payment gateways", TPV.class));

        m.add(new MDDAction("VCC", VCC.class));


        m.add(new MDDAction("Commissions", Invoice.class));

        m.add(new MDDAction("Abseiling", Invoice.class));

        m.add(new MDDAction("Prepayment", Deposit.class));

        m.add(new MDDAction("Portfolios", Invoice.class));

        m.add(new MDDAction("Payments", Payment.class));

        m.add(new MDDAction("Collections", Payment.class));

        m.add(new MDDAction("Litigations", Litigation.class));

        m.add(new MDDAction("Bank remittances", Invoice.class));

        m.add(new MDDAction("Direct payments", Payment.class));

        m.add(new MDDAction("Bank reconciliation", Invoice.class));

        m.add(new MDDAction("Accounting", Invoice.class));

        return m;
    }
}
