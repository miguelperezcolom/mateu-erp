package io.mateu.erp.client.product;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.product.generic.Contract;
import io.mateu.erp.model.product.generic.Product;
import io.mateu.erp.model.product.generic.Shop;
import io.mateu.erp.model.product.generic.StopSales;
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
public class GenericModule extends AbstractModule {
    @Override
    public String getName() {
        return "Generic";
    }

    @Override
    public List<MenuEntry> getMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new MDDAction("Products", Product.class));

        m.add(new MDDAction("Contracts", Contract.class));

        m.add(new MDDAction("Shops", Shop.class));

        m.add(new MDDAction("Stop sales", StopSales.class));


        return m;
    }
}
