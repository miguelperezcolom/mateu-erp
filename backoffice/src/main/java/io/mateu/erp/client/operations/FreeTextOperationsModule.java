package io.mateu.erp.client.operations;

import io.mateu.erp.model.booking.PurchaseOrder;
import io.mateu.erp.model.booking.freetext.FreeTextService;
import io.mateu.mdd.core.app.AbstractModule;
import io.mateu.mdd.core.app.MDDOpenCRUDAction;
import io.mateu.mdd.core.app.MenuEntry;

import java.util.ArrayList;
import java.util.List;

public class FreeTextOperationsModule extends AbstractModule {
    @Override
    public String getName() {
        return "Free text";
    }

    @Override
    public List<MenuEntry> buildMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new MDDOpenCRUDAction("Services", FreeTextService.class));

        m.add(new MDDOpenCRUDAction("Purchase orders", PurchaseOrder.class));

        return m;
    }
}
