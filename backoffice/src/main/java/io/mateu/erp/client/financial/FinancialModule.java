package io.mateu.erp.client.financial;

import io.mateu.ui.mdd.client.ERPServiceAsync;
import io.mateu.ui.mdd.client.MDDCallback;
import io.mateu.ui.mdd.shared.ERPService;
import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.AbstractModule;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.app.MenuEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class FinancialModule extends AbstractModule {
    @Override
    public List<MenuEntry> getMenu() {
        List<MenuEntry> m = new ArrayList<>();

        if (false) m.add(new AbstractAction("Agents") {
            @Override
            public void run() {
                ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.financials.FinancialAgent", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Billing concepts") {
            @Override
            public void run() {
                ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.financials.BillingConcept", new MDDCallback());
            }
        });

        if (false) {

            m.add(new AbstractAction("Isued invoices") {
                @Override
                public void run() {
                }
            });

            m.add(new AbstractAction("Received invoices") {
                @Override
                public void run() {
                }
            });

            m.add(new AbstractAction("Payment gateways") {
                @Override
                public void run() {
                }
            });

            m.add(new AbstractAction("VCC") {
                @Override
                public void run() {
                }
            });

            m.add(new AbstractAction("VAT") {
                @Override
                public void run() {

                }
            });

            m.add(new AbstractAction("Commissions") {
                @Override
                public void run() {

                }
            });

            m.add(new AbstractAction("Abseiling") {
                @Override
                public void run() {

                }
            });

            m.add(new AbstractAction("Prepayment") {
                @Override
                public void run() {

                }
            });

            m.add(new AbstractAction("Portfolios") {
                @Override
                public void run() {

                }
            });

            m.add(new AbstractAction("Payments") {
                @Override
                public void run() {

                }
            });

            m.add(new AbstractAction("Collections") {
                @Override
                public void run() {

                }
            });

            m.add(new AbstractAction("Bank remittances") {
                @Override
                public void run() {

                }
            });

            m.add(new AbstractAction("Direct payments") {
                @Override
                public void run() {

                }
            });

            m.add(new AbstractAction("Bank reconciliation") {
                @Override
                public void run() {

                }
            });

            m.add(new AbstractAction("Accounting") {
                @Override
                public void run() {

                }
            });

        }

        return m;
    }
}
