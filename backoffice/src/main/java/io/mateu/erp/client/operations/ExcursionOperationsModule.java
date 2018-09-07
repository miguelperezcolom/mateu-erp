package io.mateu.erp.client.operations;

import io.mateu.erp.model.booking.ManagedEvent;
import io.mateu.erp.model.booking.PurchaseOrder;
import io.mateu.erp.model.booking.hotel.HotelService;
import io.mateu.mdd.core.app.AbstractModule;
import io.mateu.mdd.core.app.MDDOpenCRUDAction;
import io.mateu.mdd.core.app.MenuEntry;

import java.util.ArrayList;
import java.util.List;

public class ExcursionOperationsModule extends AbstractModule {
    @Override
    public String getName() {
        return "Excursions";
    }

    @Override
    public List<MenuEntry> buildMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new MDDOpenCRUDAction("Events", ManagedEvent.class));

        m.add(new MDDOpenCRUDAction("Purchase orders", PurchaseOrder.class));

        return m;
    }
}
