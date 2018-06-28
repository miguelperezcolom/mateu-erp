package io.mateu.erp.client.operations;

import io.mateu.erp.client.booking.PickupTimeImportingView;
import io.mateu.erp.client.booking.TransfersSummaryView;
import io.mateu.erp.model.booking.PurchaseOrder;
import io.mateu.erp.model.booking.transfer.TransferPointMapping;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.AbstractModule;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.app.MenuEntry;
import io.mateu.ui.mdd.client.MDDAction;

import java.util.ArrayList;
import java.util.List;

public class TransferOperationsModule extends AbstractModule {
    @Override
    public String getName() {
        return "Transfer";
    }

    @Override
    public List<MenuEntry> buildMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new AbstractAction("Transfers summary") {
            @Override
            public void run() {
                MateuUI.openView(new TransfersSummaryView(), isModifierPressed());
            }
        });

        m.add(new MDDAction("Services", TransferService.class));

        m.add(new MDDAction("Buses", PurchaseOrder.class));

        m.add(new MDDAction("Mapping", TransferPointMapping.class));

        m.add(new AbstractAction("Import pickup times") {
            @Override
            public void run() {
                MateuUI.openView(new PickupTimeImportingView(), isModifierPressed());
            }
        });

        return m;
    }
}
