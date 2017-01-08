package io.mateu.erp.client.financial;

import io.mateu.erp.client.admin.CustomerCRUD;
import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.AbstractModule;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.app.MenuEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class FinancialModule extends AbstractModule {
    @Override
    public List<MenuEntry> getMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new AbstractAction("Invoices to customers") {
            @Override
            public void run() {
                MateuUI.openView(new CustomerCRUD());
            }
        });

        m.add(new AbstractAction("Invoices from providers") {
            @Override
            public void run() {
                MateuUI.openView(new CustomerCRUD());
            }
        });

        m.add(new AbstractAction("Payment gateways") {
            @Override
            public void run() {
                MateuUI.openView(new CustomerCRUD());
            }
        });

        m.add(new AbstractAction("VCC") {
            @Override
            public void run() {
                MateuUI.openView(new CustomerCRUD());
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

        return m;
    }
}
