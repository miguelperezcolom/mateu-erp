package io.mateu.erp.client.product;

import io.mateu.ui.core.client.app.*;
import io.mateu.ui.mdd.client.ERPServiceAsync;
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

        m.add(new AbstractAction("Products") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.product.generic.Product", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Contracts") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.product.generic.Contract", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Shop") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.product.generic.Shop", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Stop sales") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.product.generic.StopSales", new MDDCallback());
            }
        });


        return m;
    }
}
