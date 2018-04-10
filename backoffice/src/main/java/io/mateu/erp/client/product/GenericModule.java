package io.mateu.erp.client.product;

import io.mateu.erp.model.product.generic.Contract;
import io.mateu.erp.model.product.generic.ExtraPrice;
import io.mateu.erp.model.product.generic.Price;
import io.mateu.erp.model.product.generic.Product;
import io.mateu.ui.core.client.app.AbstractModule;
import io.mateu.ui.core.client.app.MenuEntry;
import io.mateu.ui.mdd.client.MDDAction;

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
    public List<MenuEntry> buildMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new MDDAction("Products", Product.class));

        m.add(new MDDAction("Contracts", Contract.class));

        m.add(new MDDAction("Product prices", Price.class));

        m.add(new MDDAction("Extra prices", ExtraPrice.class));

        return m;
    }
}
