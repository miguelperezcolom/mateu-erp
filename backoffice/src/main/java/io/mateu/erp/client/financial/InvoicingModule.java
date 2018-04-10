package io.mateu.erp.client.financial;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.invoicing.Charge;
import io.mateu.erp.model.invoicing.Invoice;
import io.mateu.erp.model.payments.Litigation;
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
public class InvoicingModule extends AbstractModule {
    @Override
    public String getName() {
        return "Invoicing";
    }

    @Override
    public List<MenuEntry> buildMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new MDDAction("Issued invoices", Invoice.class));

        m.add(new MDDAction("Charges", Charge.class));

        m.add(new MDDAction("Commissions", Charge.class));

        m.add(new MDDAction("Litigations", Litigation.class));

        m.add(new MDDAction("Received invoices", Invoice.class));

        return m;
    }
}
