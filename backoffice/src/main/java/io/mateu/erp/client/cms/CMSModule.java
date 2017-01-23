package io.mateu.erp.client.cms;

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
public class CMSModule extends AbstractModule {
    @Override
    public List<MenuEntry> getMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new AbstractAction("Websites") {
            @Override
            public void run() {
                ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.cms.Website", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Pages") {
            @Override
            public void run() {
                ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.cms.Page", new MDDCallback());
            }
        });
        m.add(new AbstractAction("Assets") {
            @Override
            public void run() {
                ((ERPServiceAsync)MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.cms.Asset", new MDDCallback());
            }
        });

        return m;
    }
}
