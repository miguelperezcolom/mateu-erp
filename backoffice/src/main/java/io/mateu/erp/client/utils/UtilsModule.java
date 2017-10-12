package io.mateu.erp.client.utils;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.AbstractModule;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.app.MenuEntry;
import io.mateu.ui.mdd.client.ERPServiceAsync;
import io.mateu.ui.mdd.client.MDDCallback;
import io.mateu.ui.mdd.shared.ERPService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class UtilsModule extends AbstractModule {
    @Override
    public String getName() {
        return "Utils";
    }

    @Override
    public List<MenuEntry> getMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new AbstractAction("Queue") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.workflow.AbstractTask", new MDDCallback());
            }
        });

        m.add(new AbstractAction("SQL") {
            @Override
            public void run() {

                MateuUI.openView(new SQLView());
            }
        });

        m.add(new AbstractAction("JPQL") {
            @Override
            public void run() {

                MateuUI.openView(new JPQLView());
            }
        });

        return m;
    }
}
