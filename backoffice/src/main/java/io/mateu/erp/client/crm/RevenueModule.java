package io.mateu.erp.client.crm;

import io.mateu.erp.model.product.generic.Product;
import io.mateu.erp.model.revenue.Markup;
import io.mateu.erp.model.revenue.MarkupLine;
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

public class RevenueModule extends AbstractModule {
    @Override
    public String getName() {
        return "Revenue";
    }

    @Override
    public List<MenuEntry> buildMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new MDDAction("Products", Product.class));

        m.add(new MDDAction("Markups", Markup.class));

        m.add(new MDDAction("Markup lines", MarkupLine.class));

        return m;
    }
}
