package io.mateu.erp.client.product;

import io.mateu.erp.model.product.transfer.*;
import io.mateu.ui.core.client.app.AbstractModule;
import io.mateu.ui.core.client.app.MenuEntry;
import io.mateu.ui.mdd.client.MDDAction;
import io.mateu.ui.mdd.client.MDDMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class TransferModule extends AbstractModule {
    @Override
    public String getName() {
        return "Transfer";
    }

    @Override
    public List<MenuEntry> buildMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new MDDAction("Transfer points", TransferPoint.class));

        m.add(new MDDAction("Contracts", Contract.class));

        m.add(new MDDAction("Prices", Price.class));

        m.add(new MDDAction("Vehicles", Vehicle.class));

        m.add(new MDDAction("Price zones", Zone.class));

        m.add(new MDDMenu("Routes", "Routes", Route.class, "Stops", RouteStop.class, "Times", RouteTime.class));

        return m;
    }
}
