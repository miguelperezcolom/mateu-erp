package io.mateu.erp.client.utils;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.AbstractModule;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.app.MenuEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class UtilsModule extends AbstractModule {
    @Override
    public List<MenuEntry> getMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new AbstractAction("Queue") {
            @Override
            public void run() {
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
