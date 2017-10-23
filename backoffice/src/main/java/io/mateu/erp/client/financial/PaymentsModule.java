package io.mateu.erp.client.financial;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.AbstractModule;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.app.MenuEntry;
import io.mateu.ui.mdd.client.ERPServiceAsync;
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
    public List<MenuEntry> getMenu() {
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


        m.add(new AbstractAction("Accounts") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.payments.Account", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Payment gateways") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.tpv.TPV", new MDDCallback());
            }
        });

        m.add(new AbstractAction("VCC") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.payments.VCC", new MDDCallback());
            }
        });


        m.add(new AbstractAction("Commissions") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.invoicing.Invoice", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Abseiling") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.invoicing.Invoice", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Prepayment") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.payments.Deposit", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Portfolios") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.invoicing.Invoice", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Payments") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.payments.Payment", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Collections") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.payments.Payment", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Litigations") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.payments.Litigation", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Bank remittances") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.invoicing.Invoice", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Direct payments") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.invoicing.Invoice", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Bank reconciliation") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.invoicing.Invoice", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Accounting") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.invoicing.Invoice", new MDDCallback());
            }
        });

        return m;
    }
}
