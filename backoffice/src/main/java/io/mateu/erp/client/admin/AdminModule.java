package io.mateu.erp.client.admin;

import io.mateu.erp.client.mateu.MDDCallback;
import io.mateu.erp.shared.mateu.ERPService;
import io.mateu.erp.shared.mateu.ERPServiceAsync;
import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.AbstractModule;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.app.MenuEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class AdminModule extends AbstractModule {
    @Override
    public List<MenuEntry> getMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new AbstractAction("Users") {
            @Override
            public void run() {
                ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.authentication.User", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Offices") {
            @Override
            public void run() {
                ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.organization.Office", new MDDCallback());
            }
        });

        m.add(new AbstractAction("POS") {
            @Override
            public void run() {
                ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.organization.PointOfSale", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Customers") {
            @Override
            public void run() {
                ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.financials.Customer", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Suppliers") {
            @Override
            public void run() {
                ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.financials.Supplier", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Languages") {
            @Override
            public void run() {
                ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.multilanguage.Language", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Translations") {
            @Override
            public void run() {

            }
        });

        m.add(new AbstractAction("Templates") {
            @Override
            public void run() {

            }
        });

        return m;
    }
}
